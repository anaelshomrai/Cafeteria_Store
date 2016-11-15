package com.cafeteria.cafeteria_store.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cafeteria.cafeteria_store.R;
import com.cafeteria.cafeteria_store.data.Extra;
import com.cafeteria.cafeteria_store.data.InventoryItem;
import com.cafeteria.cafeteria_store.data.Item;
import com.cafeteria.cafeteria_store.data.Main;
import com.cafeteria.cafeteria_store.utils.ApplicationConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InventoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private ListView lvInventory;
    private List<InventoryItem> inventoryItems;
    private List<Item> items;
    private List<Extra> extras;
    private List<Main> mains;
    private getExtrasTask getExtrasTask;
    private getMainsTask getMainsTask;
    private getItemsTask getItemsTask;
    private SearchView searchView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> backup;
    private String urlRequest;
    private String json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        lvInventory = (ListView) findViewById(R.id.lvInventory);
        inventoryItems = new ArrayList<>();
        backup = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(this,R.layout.single_inventory,inventoryItems);

        getItemsTask = new getItemsTask();
        getItemsTask.execute();

        getExtrasTask = new getExtrasTask();
        getExtrasTask.execute();

        getMainsTask = new getMainsTask();
        getMainsTask.execute();

        searchView = (SearchView) findViewById(R.id.search); // inititate a search view
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        inventoryAdapter.filter(newText);
        return false;
    }


    public class InventoryAdapter extends BaseAdapter {
        private List<InventoryItem> inventoryItems;
        private Context context;
        private int layout;

        public InventoryAdapter(Context context, int layout, List<InventoryItem> inventoryItems) {
            this.context = context;
            this.layout = layout;
            this.inventoryItems = inventoryItems;
        }

        @Override
        public int getCount() {
            return inventoryItems.size();
        }

        @Override
        public Object getItem(int i) {
            return inventoryItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final InventoryItem inventoryItem = (InventoryItem) getItem(position);

            if (convertView == null) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layout, parent, false);
                holder = new ViewHolder();
                holder.toggleBtn = (ToggleButton) convertView.findViewById(R.id.toggleBtn);
                holder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Gson gson = new Gson();

                    if (isChecked){ // on stock
                        if (inventoryItem.getClass() == Item.class){
                            Item toUpdate = (Item) inventoryItem;
                            toUpdate.setInStock(true);
                            urlRequest = ApplicationConstant.UPDATE_ITEM_URL;
                            json = gson.toJson(toUpdate, Item.class);

                        }else if(inventoryItem.getClass() == Extra.class){
                            Extra toUpdate = (Extra) inventoryItem;
                            toUpdate.setInStock(true);
                            urlRequest = ApplicationConstant.UPDATE_EXTRA_URL;
                            json = gson.toJson(toUpdate, Extra.class);

                        }else if(inventoryItem.getClass() == Main.class){
                            Main toUpdate = (Main) inventoryItem;
                            toUpdate.setInStock(true);
                            urlRequest = ApplicationConstant.UPDATE_MAIN_URL;
                            json = gson.toJson(toUpdate, Main.class);
                        }

                    }else{ // not in stock
                        if (inventoryItem.getClass() == Item.class){
                            Item toUpdate = (Item) inventoryItem;
                            toUpdate.setInStock(false);
                            urlRequest = ApplicationConstant.UPDATE_ITEM_URL;
                            json = gson.toJson(toUpdate, Item.class);

                        }else if(inventoryItem.getClass() == Extra.class){
                            Extra toUpdate = (Extra) inventoryItem;
                            toUpdate.setInStock(false);
                            urlRequest = ApplicationConstant.UPDATE_EXTRA_URL;
                            json = gson.toJson(toUpdate, Extra.class);

                        }else if(inventoryItem.getClass() == Main.class){
                            Main toUpdate = (Main) inventoryItem;
                            toUpdate.setInStock(false);
                            urlRequest = ApplicationConstant.UPDATE_MAIN_URL;
                            json = gson.toJson(toUpdate, Main.class);
                        }
                    }

                    new UpdateInventoryTask().execute();

                }
            });
            holder.tvItemName.setText(inventoryItem.getTitle());
            return convertView;
        }

        private class ViewHolder {
            TextView tvItemName;
            ToggleButton toggleBtn;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            inventoryItems.clear();

            if (charText.length() == 0){
                inventoryItems.addAll(backup);
            } else {
                for (InventoryItem it : backup) {
                    if (it.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                        inventoryItems.add(it);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    private class getItemsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Type listType = new TypeToken<ArrayList<Item>>() {
                }.getType();
                items = new Gson().fromJson(response, listType);
                Log.e("TASKS","items size: " + items.size());
                if (getExtrasTask.getStatus() == Status.FINISHED && getMainsTask.getStatus() == Status.FINISHED){
                    setInventory();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_ITEMS_URL);
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

    private class getExtrasTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Type listType = new TypeToken<ArrayList<Extra>>() {
                }.getType();
                extras = new Gson().fromJson(response, listType);

                if (getItemsTask.getStatus() == Status.FINISHED && getMainsTask.getStatus() == Status.FINISHED){
                    setInventory();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_EXTRAS_URL);
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

    private class getMainsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Type listType = new TypeToken<ArrayList<Main>>() {
                }.getType();
                mains = new Gson().fromJson(response, listType);

                if (getExtrasTask.getStatus() == Status.FINISHED && getItemsTask.getStatus() == Status.FINISHED){
                    setInventory();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_MAINS_URL);
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

    private void setInventory(){
        InventoryItem it;

        for (Item item: items){
            it = new Item();
            it.setTitle(item.getTitle());
            it.setId(item.getId());
            inventoryItems.add(it);
        }

        for (Extra extra: extras){
            it = new Extra();
            it.setTitle(extra.getTitle());
            it.setId(extra.getId());

            inventoryItems.add(extra);
        }

        for (Main main: mains){
            it = new Main();
            it.setTitle(main.getTitle());
            it.setId(main.getId());

            inventoryItems.add(main);
        }

        backup.addAll(inventoryItems);
        lvInventory.setAdapter(inventoryAdapter);
    }

    private class UpdateInventoryTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;

            URL url = null;
            try {
                url = new URL(urlRequest);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestProperty("Accept", "text/plain");
                con.setRequestMethod("POST");

                OutputStream os = con.getOutputStream();
                os.write(json.getBytes("UTF-8"));
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

                if (response.toString().trim().equals("OK")) {
                    result = true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(InventoryActivity.this, "Action completed", Toast.LENGTH_SHORT).show();

        }
    }
}
