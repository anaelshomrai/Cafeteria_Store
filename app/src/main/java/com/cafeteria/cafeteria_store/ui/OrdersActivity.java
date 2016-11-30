package com.cafeteria.cafeteria_store.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_store.R;
import com.cafeteria.cafeteria_store.data.Extra;
import com.cafeteria.cafeteria_store.data.Item;
import com.cafeteria.cafeteria_store.data.Order;
import com.cafeteria.cafeteria_store.data.OrderedItem;
import com.cafeteria.cafeteria_store.data.OrderedMeal;
import com.cafeteria.cafeteria_store.utils.ApplicationConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private List<Order> ordersList;
    private ActionMode mActionMode;
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private ProgressBar progressBar;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        rvOrders = (RecyclerView)findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        String json = getIntent().getStringExtra("orders_list");
        if( json != null && json.equals("")) {
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
            Type listType = new TypeToken<ArrayList<Order>>() {
            }.getType();
            ordersList = gson.fromJson(json.toString(), listType);
        } else {
            Log.e("DEBUG","onCreate executr get Orders");
            new GetOrdersTask().execute();
        }

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

        private List<Order> orders;
        private Context context;

        private PopupWindow popupWindow;
        private LinearLayout llDetails;
        private TextView tvOrderNumber;
        private TextView tvTime;
        private TextView tvCustomerName;

        public RecyclerViewAdapter(Context context, List<Order> orders) {
            this.orders = orders;
            this.context = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_order, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {

                // On Click Event - opens the popup window with the details of the order
                @Override
                public void onClick(View view) {
                    int itemPosition = rvOrders.getChildLayoutPosition(view);
                    Order o = orders.get(itemPosition);
                    LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View inflatedView = layoutInflater.inflate(R.layout.order_details_popup, null,false);

                    tvOrderNumber = (TextView) inflatedView.findViewById(R.id.tvOrderNumber);
                    tvOrderNumber.setText(o.getId()+"");
                    tvCustomerName = (TextView) inflatedView.findViewById(R.id.tvCustomerName);
                    tvCustomerName.setText(o.getCustomer().getFirstName() + " " + o.getCustomer().getLastName());
                    tvTime = (TextView) inflatedView.findViewById(R.id.tvTime);
                    if( o.getPickupTime() != null ) {
                        tvTime.setText(o.getPickupTime());
                    } else {
                        tvTime.setText(getResources().getString(R.string.time_not_specified));
                    }

                    llDetails = (LinearLayout) inflatedView.findViewById(R.id.llDetails);
                    if( o.getItems() != null && o.getItems().size() > 0 ) {
                        TextView t = new TextView(OrdersActivity.this);
                        t.setText(getResources().getString(R.string.items_title));
                        t.setTypeface(null, Typeface.BOLD);
                        llDetails.addView(t);
                        for( OrderedItem item : o.getItems() ) {
                            t = new TextView(OrdersActivity.this);
                            t.setText(getResources().getString(R.string.star) + " " + item.getParentItem().getTitle());
                            llDetails.addView(t);
                            if( item.getComment() != null && !item.getComment().equals("")) {
                                t = new TextView(OrdersActivity.this);
                                t.setText(getResources().getString(R.string.comment_title) + " " + item.getComment());
                                llDetails.addView(t);
                            }
                        }

                        t = new TextView(OrdersActivity.this);
                        t.setText("Space");
                        t.setVisibility(View.INVISIBLE);
                        llDetails.addView(t);

                    }
                    if( o.getMeals() != null && o.getMeals().size() > 0 ) {
                        TextView t = new TextView(OrdersActivity.this);
                        t.setText(getResources().getString(R.string.meals_title));
                        t.setTypeface(null, Typeface.BOLD);
                        llDetails.addView(t);
                        for( OrderedMeal meal : o.getMeals()) {
                            t = new TextView(OrdersActivity.this);
                            t.setText(getResources().getString(R.string.star) + " " + getResources().getString(R.string.meal_title) + " " + meal.getTitle());
                            //t.setGravity(Gravity.CENTER);
                            llDetails.addView(t);
                            for(Extra extra : meal.getChosenExtras()) {
                                t = new TextView(OrdersActivity.this);
                                t.setText(extra.getTitle());
                                llDetails.addView(t);
                            }

                            if( meal.getComment() != null && !meal.getComment().equals("") ) {
                                t = new TextView(OrdersActivity.this);
                                t.setText(getResources().getString(R.string.comment_title) + " " + meal.getComment());
                                t.setTypeface(null, Typeface.BOLD);
                                llDetails.addView(t);
                            }

                            t = new TextView(OrdersActivity.this);
                            t.setText("Space");
                            t.setVisibility(View.INVISIBLE);
                            llDetails.addView(t);
                        }
                    } else {
                        llDetails.setVisibility(View.GONE);
                    }

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    popupWindow = new PopupWindow(inflatedView, size.x - 50,size.y - 500, true );
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
                    popupWindow.setAnimationStyle(android.R.anim.fade_in); // call this before showing the popup
                    popupWindow.showAtLocation(getCurrentFocus(), Gravity.BOTTOM, 0,150);  // 0 - X postion and 150 - Y position
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            Order order = orders.get(position);

            //Setting text view title
            holder.tvCustomer.setText(order.getCustomer().getFirstName() + " " +order.getCustomer().getLastName());
            SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_FORMAT);
            holder.tvDate.setText(sdf.format(order.getDate()));
            if( order.isReady() ) {
                Log.e("DEBUG","READY!!!!!!!!!!!!!");
                  holder.tvReady.setVisibility(View.VISIBLE);
            } else {
                holder.tvReady.setVisibility(View.INVISIBLE);
            }
            holder.rlLayout.setBackgroundColor(getColor(R.color.colorOddRow));


        }


        @Override
        public int getItemCount() {
            return (null != orders ? orders.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener,
                MenuItem.OnMenuItemClickListener {

            protected TextView tvCustomer;
            protected TextView tvDate;
            protected RelativeLayout rlLayout;
            protected TextView tvReady;

            public CustomViewHolder(View view) {
                super(view);
                this.tvCustomer = (TextView) view.findViewById(R.id.tvCustomer);
                this.tvDate = (TextView) view.findViewById(R.id.tvDate);
                this.rlLayout = (RelativeLayout) view.findViewById(R.id.rlLayout);
                this.tvReady = (TextView) view.findViewById(R.id.tvReady);
                view.setOnLongClickListener(this);
                view.setOnCreateContextMenuListener(this);
                view.setTag(this);
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }

            @Override

            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            }


            @Override
            public boolean onLongClick(View view) {
                int position = getAdapterPosition();
                final Order order = orders.get(position);
                final View innerView = view;
                final CustomViewHolder holder = (CustomViewHolder)view.getTag();
                holder.rlLayout.setBackgroundColor(getResources().getColor(R.color.colorSelectedRow));
                mActionMode = (OrdersActivity.this).startSupportActionMode(
                        new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                mode.getMenuInflater().inflate(R.menu.order_actions, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
                                //So here show action menu according to SDK Levels
                                if (Build.VERSION.SDK_INT < 11) {
                                    MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_ready), MenuItemCompat.SHOW_AS_ACTION_NEVER);
                                    MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delivered), MenuItemCompat.SHOW_AS_ACTION_NEVER);
                                } else {
                                    menu.findItem(R.id.action_ready).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                    menu.findItem(R.id.action_delivered).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                                }

                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_ready:
                                        Toast.makeText(OrdersActivity.this,"Ready",Toast.LENGTH_SHORT).show();
                                        order.setReady(true);
                                        // update in server
                                        new AsyncTask<String, Void, Void>() {

                                            @Override
                                            protected Void doInBackground(String... strings) {
                                                Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
                                                String jsonOrder = gson.toJson(order, Order.class);
                                                URL url = null;
                                                try {
                                                    url = new URL(ApplicationConstant.UPDATE_ORDER_READY);
                                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                                    con.setDoOutput(true);
                                                    con.setDoInput(true);
                                                    con.setRequestProperty("Content-Type", "text/plain");
                                                    con.setRequestProperty("Accept", "text/plain");
                                                    con.setRequestMethod("POST");

                                                    OutputStream os = con.getOutputStream();
                                                    os.write(jsonOrder.getBytes("UTF-8"));
                                                    os.flush();

                                                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                                        return null;
                                                    }

                                                    // Response
                                                    StringBuilder response = new StringBuilder();
                                                    BufferedReader input = new BufferedReader(
                                                            new InputStreamReader(con.getInputStream()));

                                                    String line;
                                                    while ((line = input.readLine()) != null) {
                                                        response.append(line + "\n");
                                                    }

                                                    input.close();

                                                    con.disconnect();

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                }  catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        }.execute();
                                        holder.tvReady.setVisibility(View.VISIBLE);
                                        break;
                                    case R.id.action_delivered:
                                        Toast.makeText(OrdersActivity.this,"Delivered",Toast.LENGTH_SHORT).show();
                                        order.setDelivered(true);
                                        // update in server
                                        new AsyncTask<String, Void, Void>() {

                                            @Override
                                            protected Void doInBackground(String... strings) {
                                                Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
                                                String jsonOrder = gson.toJson(order, Order.class);
                                                URL url = null;
                                                try {
                                                    url = new URL(ApplicationConstant.UPDATE_ORDER_DELIVERED);
                                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                                    con.setDoOutput(true);
                                                    con.setDoInput(true);
                                                    con.setRequestProperty("Content-Type", "text/plain");
                                                    con.setRequestProperty("Accept", "text/plain");
                                                    con.setRequestMethod("POST");

                                                    OutputStream os = con.getOutputStream();
                                                    os.write(jsonOrder.getBytes("UTF-8"));
                                                    os.flush();

                                                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                                        return null;
                                                    }

                                                    // Response
                                                    StringBuilder response = new StringBuilder();
                                                    BufferedReader input = new BufferedReader(
                                                            new InputStreamReader(con.getInputStream()));

                                                    String line;
                                                    while ((line = input.readLine()) != null) {
                                                        response.append(line + "\n");
                                                    }

                                                    input.close();

                                                    con.disconnect();

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                }  catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        }.execute();
                                        orders.remove(order);
                                        notifyDataSetChanged();
                                        break;
                                }
                                return false;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                holder.rlLayout.setBackgroundColor(getResources().getColor(R.color.colorOddRow));
//                                if( getAdapterPosition() % 2 == 0 ) {
//                                    holder.rlLayout.setBackgroundColor(getResources().getColor(R.color.colorEevenRow));
//                                } else {
//                                    holder.rlLayout.setBackgroundColor(getResources().getColor(R.color.colorOddRow));
//                                }
                            }
                        });
                return true;
            }
        }
    }

    private class GetOrdersTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Type listType = new TypeToken<ArrayList<Order>>() {
                }.getType();
                ordersList = new Gson().fromJson(response, listType);
                Log.e("DEBUG","Orders size : " + ordersList.size());
                adapter = new RecyclerViewAdapter( OrdersActivity.this, ordersList);
                rvOrders.setAdapter(adapter);
                rvOrders.setLongClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
                if( mActionMode != null ) {
                    mActionMode.finish();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_ORDERS_URL);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return response.toString();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        //on production we will reduce the time to(at least):
        timer.schedule(timerTask,60000, 60000);
        //timer.schedule(timerTask, 5000, 10000);
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        Log.e("DEBUG","timer executr get Orders");
                        new GetOrdersTask().execute();
                    }
                });
            }
        };
    }


//    private class UpdateOrderStateTask extends AsyncTask<String,Void,Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//        }
//    }

    private class ItemsListAdapter extends BaseAdapter {

        private Context context;
        List<OrderedItem> items;
        int layout;

        public ItemsListAdapter(Context context, List<OrderedItem> items, int layout){
            this.context = context;
            this.items = items;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            OrderedItem item = items.get(i);
            Log.e("DEBUG","Item in get view (adapter) : " + item.getId());

            if( view == null ) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(layout, viewGroup, false);

                holder = new ViewHolder();
                holder.tvItemTitle = (TextView) view.findViewById(R.id.tvItemTitle);
                //holder.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
                holder.tvComment = (TextView) view.findViewById(R.id.tvComment);

                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            holder.tvItemTitle.setText(item.getParentItem().getTitle());
            //holder.tvPrice.setText(item.getParentItem().getPrice()+"");
            if( item.getComment() != null ) {
                holder.tvComment.setText(item.getComment());
            } else {
                holder.tvComment.setVisibility(View.GONE);
            }
            return view;
        }

        class ViewHolder {
            TextView tvItemTitle;
            //TextView tvPrice;
            TextView tvComment;
        }
    }

//    private class MealsListAdapter extends BaseAdapter {
//
//        private Context context;
//        List<OrderedMeal> meals;
//        int layout;
//
//        public MealsListAdapter(Context context, List<OrderedMeal> meals, int layout){
//            this.context = context;
//            this.meals = meals;
//            this.layout = layout;
//        }
//
//        @Override
//        public int getCount() {
//            return meals.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            ViewHolder holder;
//            OrderedMeal meal = meals.get(position);
//
//
//            if( view == null ) {
//                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                view = inflater.inflate(layout, viewGroup, false);
//
//                holder = new ViewHolder();
//                holder.tvMealTitle = (TextView) view.findViewById(R.id.tvMealTitle);
//                holder.llExtras = (LinearLayout) view.findViewById(R.id.llExtras);
//                //holder.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
////                holder.tvComment = (TextView) view.findViewById(R.id.tvComment);
//
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder)view.getTag();
//            }
//
//            Log.e("DEBUG","extras : " + meal.getChosenExtras().size());
//            for(Extra extra : meal.getChosenExtras()) {
//                TextView t = new TextView(OrdersActivity.this);
//                t.setText(extra.getTitle());
//                holder.llExtras.addView(t);
//            }
//
//            holder.tvMealTitle.setText(meal.getTitle());
//            //holder.tvPrice.setText(item.getParentItem().getPrice()+"");
//            if( meal.getComment() != null ) {
////                holder.tvComment.setText(meal.getComment());
//            } else {
////                holder.tvComment.setVisibility(View.GONE);
//            }
//            return view;
//        }
//
//        class ViewHolder {
//            TextView tvMealTitle;
//            LinearLayout llExtras;
//            //TextView tvPrice;
////            TextView tvComment;
//        }
//    }
}
