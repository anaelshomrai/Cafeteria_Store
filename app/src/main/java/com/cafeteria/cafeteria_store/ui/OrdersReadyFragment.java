package com.cafeteria.cafeteria_store.ui;


        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.content.res.Configuration;
        import android.graphics.Point;
        import android.graphics.Typeface;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.ContextMenu;
        import android.view.Display;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.PopupWindow;
        import android.widget.ProgressBar;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.cafeteria.cafeteria_store.R;
        import com.cafeteria.cafeteria_store.data.Extra;
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

        import me.dm7.barcodescanner.zxing.ZXingScannerView;

        import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersReadyFragment extends Fragment {

    private RecyclerView rvOrders;
    private List<Order> ordersList;
    //    private ActionMode mActionMode;
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private ProgressBar progressBar;
    private RecyclerViewAdapter adapter;
    private TextView tvEmptyList;
    Toolbar toolbar;
    View fragmentView;
    private RelativeLayout bac_dim_layout;
    private Order readyOrder;
    private List<Order> removedOrders = new ArrayList<>();
    private PopupWindow popupWindow;

    public OrdersReadyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("DEBUG","Orders On Create View");
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_orderes_ready, container, false);
        toolbar = (Toolbar)fragmentView.findViewById(R.id.toolbar);
        tvEmptyList = (TextView)fragmentView.findViewById(R.id.tvEmptyList);
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.progress);
        rvOrders = (RecyclerView)fragmentView.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        bac_dim_layout = (RelativeLayout)fragmentView.findViewById(R.id.bac_dim_layout);
//        String json = getIntent().getStringExtra("orders_list");
//        if( json != null && json.equals("")) {
//                Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
//                Type listType = new TypeToken<ArrayList<Order>>() {
//                }.getType();
//                ordersList = gson.fromJson(json.toString(), listType);
//            } else {
//                Log.e("DEBUG","onCreate executr get Orders");
        startTimer();
//       }
//
        return fragmentView;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

        private List<Order> orders;
        private Context context;

        //private PopupWindow popupWindow;
        private LinearLayout llDetails;
        private TextView tvOrderNumber;
        private TextView tvTime;
        private TextView tvCustomerName;

        private boolean isActionModeOn = false;

        public RecyclerViewAdapter(Context context, List<Order> orders) {
            this.orders = orders;
            this.context = context;
        }

        @Override
        public RecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_order, parent, false);
            RecyclerViewAdapter.CustomViewHolder viewHolder = new RecyclerViewAdapter.CustomViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {

                // On Click Event - opens the popup window with the details of the order
                @Override
                public void onClick(View view) {

                    int currentOrientation = getResources().getConfiguration().orientation;
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    }
                    else {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }
//                    if( mActionMode != null && isActionModeOn ) {
//                        // if the user click on the order when it is already chosen with long click, the choice canceled
//                        // and the click is not opening the popup
//                        mActionMode.finish();
//                        return;
//                    }
                    int itemPosition = rvOrders.getChildLayoutPosition(view);
                    final Order o = orders.get(itemPosition);
                    LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View inflatedView = layoutInflater.inflate(R.layout.ready_order_details_popup, null,false);

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
                    int dp = (int) (getResources().getDimension(R.dimen.appTextSize) / getResources().getDisplayMetrics().density);

                    llDetails = (LinearLayout) inflatedView.findViewById(R.id.llDetails);
                    if( o.getItems() != null && o.getItems().size() > 0 ) {
                        TextView t = new TextView(getActivity());
                        t.setTextSize(dp);
                        t.setText(getResources().getString(R.string.items_title));
                        t.setTypeface(null, Typeface.BOLD);
                        llDetails.addView(t);
                        for( OrderedItem item : o.getItems() ) {
                            t = new TextView(getActivity());
                            t.setText(getResources().getString(R.string.star) + " " + item.getParentItem().getTitle());
                            t.setTextSize(dp);
                            llDetails.addView(t);
                            if( item.getComment() != null && !item.getComment().equals("")) {
                                t = new TextView(getActivity());
                                t.setTextSize(dp);
                                t.setText(getResources().getString(R.string.comment_title) + " " + item.getComment());
                                llDetails.addView(t);
                            }
                        }

                        t = new TextView(getActivity());
                        t.setTextSize(dp);
                        t.setText("Space");
                        t.setVisibility(View.INVISIBLE);
                        llDetails.addView(t);

                    } else {
                        Log.e("ORDER","ITEMS EMPTY");
                    }
                    if( o.getMeals() != null && o.getMeals().size() > 0 ) {
                        TextView t = new TextView(getActivity());
                        t.setTextSize(dp);
                        t.setText(getResources().getString(R.string.meals_title));
                        t.setTypeface(null, Typeface.BOLD);
                        llDetails.addView(t);
                        for( OrderedMeal meal : o.getMeals()) {
                            t = new TextView(getActivity());
                            t.setTextSize(dp);
                            t.setText(getResources().getString(R.string.star) + " " + getResources().getString(R.string.meal_title) + " " + meal.getTitle());
                            //t.setGravity(Gravity.CENTER);
                            llDetails.addView(t);
                            for(Extra extra : meal.getChosenExtras()) {
                                t = new TextView(getActivity());
                                t.setTextSize(dp);
                                t.setText(extra.getTitle());
                                llDetails.addView(t);
                            }

                            if( meal.getChosenDrink() != null ) {
                                t = new TextView(getActivity());
                                t.setTextSize(dp);
                                t.setText(getResources().getString(R.string.drink_title) + " " + meal.getChosenDrink().getTitle());
                                //t.setTypeface(null, Typeface.BOLD);
                                llDetails.addView(t);
                            }

                            if( meal.getComment() != null && !meal.getComment().equals("") ) {
                                t = new TextView(getActivity());
                                t.setTextSize(dp);
                                t.setText(getResources().getString(R.string.comment_title) + " " + meal.getComment());
                                t.setTypeface(null, Typeface.BOLD);
                                llDetails.addView(t);
                            }

                            t = new TextView(getActivity());
                            t.setTextSize(dp);
                            t.setText("Space");
                            t.setVisibility(View.INVISIBLE);
                            llDetails.addView(t);
                        }
                    } else {
                        Log.e("ORDER","MEALS EMPTY");
//                        llDetails.setVisibility(View.GONE);
                    }

                    Button btn = (Button)inflatedView.findViewById(R.id.btnMarkAsReady);
                    btn.setText(getResources().getString(R.string.mark_as_delivered_action));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            readyOrder = o;
                            markAsDelivered();
                        }
                    });

                    inflatedView.findViewById(R.id.btnScanQRcode).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            readyOrder = o;
                            scanQRcode();
                        }
                    });

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    int location[] = new int[2];
                    view.getLocationOnScreen(location);

                    float width = getResources().getDimension(R.dimen.widthPopUp);
                    float height = getResources().getDimension(R.dimen.heightPopUp);
                    popupWindow = new PopupWindow(inflatedView, (int)(size.x - width),(int)(size.y - height), true );
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
                    popupWindow.setAnimationStyle(android.R.anim.fade_in); // call this before showing the popup
                    Log.e("DEBUG","Activity Focus: " + getActivity().getCurrentFocus());
                    popupWindow.showAtLocation(fragmentView , Gravity.BOTTOM, 0,70);  // 0 - X postion and 150 - Y position
                    bac_dim_layout.setVisibility(View.VISIBLE);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            bac_dim_layout.setVisibility(View.GONE);
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }
                    });
                }
            });
            return viewHolder;
        }

        public void markAsDelivered( ) {
            Toast.makeText(getActivity(),"Delivered",Toast.LENGTH_SHORT).show();
            readyOrder.setDelivered(true);
            Log.e("DEBUG","Orders size() before remove - "+orders.size());
            adapter.orders.remove(readyOrder);
            removedOrders.add(readyOrder);
            Log.e("DEBUG","Orders size() after remove - "+orders.size());
            adapter.notifyDataSetChanged();
            // update in server
            new SetDeliveredTask().execute();
            popupWindow.dismiss();
        }

        public void scanQRcode() {
            Intent intent = new Intent(getActivity(),ScanQRcodeActivity.class);
            intent.putExtra("order_number",readyOrder.getId());
            startActivityForResult(intent,1);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.CustomViewHolder holder, int position) {
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
        }


        @Override
        public int getItemCount() {
            return (null != orders ? orders.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener   {

            protected TextView tvCustomer;
            protected TextView tvDate;
            protected RelativeLayout rlLayout;
            protected TextView tvReady;
            //protected Button btnMarkAsReady;

            public CustomViewHolder(View view) {
                super(view);
                this.tvCustomer = (TextView) view.findViewById(R.id.tvCustomer);
                this.tvDate = (TextView) view.findViewById(R.id.tvDate);
                this.rlLayout = (RelativeLayout) view.findViewById(R.id.rlLayout);
                this.tvReady = (TextView) view.findViewById(R.id.tvReady);
//                this.btnMarkAsReady = (Button) view.findViewById(R.id.btnMarkAsReady);
                view.setOnLongClickListener(this);
                view.setOnCreateContextMenuListener(this);
                view.setTag(this);
            }

            @Override

            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            }


            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        }

        public void setAdapterList( List<Order> orders ) {
            this.orders.clear();
            for(Order order : orders) {
                if( !removedOrders.contains(order)) {
                    this.orders.add(order);
                }
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
                if( rvOrders.getAdapter() == null ) {
                    adapter = new RecyclerViewAdapter( getActivity(), ordersList);
                    rvOrders.setAdapter(adapter);
                    rvOrders.setLongClickable(true);
                } else {
                    adapter = (RecyclerViewAdapter) rvOrders.getAdapter();
                    adapter.setAdapterList(ordersList);
                }
                progressBar.setVisibility(View.INVISIBLE);

                adapter.notifyDataSetChanged();
                if(ordersList.isEmpty() || ordersList.size() < 1) {
                    tvEmptyList.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyList.setVisibility(View.GONE);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.getAddress(ApplicationConstant.GET_ORDERS_READY_URL));
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


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        //on production we will reduce the time to(at least):
        timer.schedule(timerTask,0, 10000);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == 1 ) {
            if( resultCode == RESULT_OK) {
                if(data.getData().toString().equals(ApplicationConstant.getAddress(ApplicationConstant.ORDER_MATCH))) {
                    Toast.makeText(getActivity(),"Delivered",Toast.LENGTH_SHORT).show();
                    readyOrder.setDelivered(true);
                    Log.e("DEBUG","Orders size() before remove - "+adapter.orders.size());
                    adapter.orders.remove(readyOrder);
                    removedOrders.add(readyOrder);
                    Log.e("DEBUG","Orders size() after remove - "+adapter.orders.size());
                    adapter.notifyDataSetChanged();
                    // update in server
                    new SetDeliveredTask().execute();
                    popupWindow.dismiss();
                }
                Toast.makeText(getActivity(),data.getData().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SetDeliveredTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
            String jsonOrder = gson.toJson(readyOrder, Order.class);
            URL url = null;
            try {
                url = new URL(ApplicationConstant.getAddress(ApplicationConstant.UPDATE_ORDER_DELIVERED));
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
    }
}
