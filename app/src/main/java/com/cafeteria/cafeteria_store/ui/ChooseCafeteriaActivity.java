package com.cafeteria.cafeteria_store.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_store.App;
import com.cafeteria.cafeteria_store.R;
import com.cafeteria.cafeteria_store.data.Cafeteria;
import com.cafeteria.cafeteria_store.utils.ApplicationConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChooseCafeteriaActivity extends AppCompatActivity {
    private List<Cafeteria> servers = new ArrayList<>();
    private List<String> serversNames = new ArrayList<>();
    private AutoCompleteTextView autoCompleteTvCafeteria;
    private Button btnNext;
    private SharedPreferences sharedPreferences;
    private boolean passwordMatch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cafeteria);
        App.changeLocale(this.getResources(), "iw");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Cafeteria cafeteria = new Gson().fromJson(sharedPreferences.getString("cafeteria", ""),Cafeteria.class);
        if (cafeteria != null){
            ApplicationConstant.setCafeteria(cafeteria);
            goToHomeScreen();
        } else {

            autoCompleteTvCafeteria = (AutoCompleteTextView)
                    findViewById(R.id.autoCompleteTvCafeteria);
            btnNext = (Button) findViewById(R.id.btnNext);

            new GetServers().execute();

            autoCompleteTvCafeteria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ApplicationConstant.setCafeteria(servers.get(i));
                    Log.e("SERVERS", "server chosen: " + serversNames.get(i) + " in server ip " + servers.get(i).getServerIp());
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ApplicationConstant.getCafeteria() == null || !serversNames.contains(autoCompleteTvCafeteria.getText().toString())) {
                        Snackbar snackbar = Snackbar
                                .make(view, getString(R.string.choose_cafeteria_toast), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(ChooseCafeteriaActivity.this, android.R.color.white));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.BLACK);
                        snackbar.show();
                    } else {
                        showAlert();
                        if (passwordMatch){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cafeteria", new Gson().toJson(ApplicationConstant.getCafeteria()));
                            editor.apply();

                            goToHomeScreen();
                        }
                    }
                }
            });
        }
    }


    private void showAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layout = getLayoutInflater();
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorHeadlines));
        input.setLinkTextColor(ContextCompat.getColor(getBaseContext(),R.color.colorHeadlines));
        input.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialogBuilder.setView(input);
        alertDialogBuilder
                .setTitle(getString(R.string.dialog_manager_password_title))
                .setMessage(getString(R.string.dialog_manager_password_message))
                .setPositiveButton(getResources().getString(R.string.dialog_positve),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String password = input.getText().toString();
                                if (ApplicationConstant.getCafeteria() != null){
                                    Log.e("CONSTANT",ApplicationConstant.getCafeteria().getAdminPassword()+ " == " + password);
                                    if (ApplicationConstant.getCafeteria().getAdminPassword().equals(password)){
                                        passwordMatch = true;
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("cafeteria", new Gson().toJson(ApplicationConstant.getCafeteria()));
                                        editor.apply();

                                        goToHomeScreen();
                                    }else{
                                        Toast.makeText(ChooseCafeteriaActivity.this,getString(R.string.dialog_not_match),Toast.LENGTH_LONG).show();
                                        passwordMatch = false;
                                    }
                                }
                                dialogInterface.dismiss();

                            }
                        })
                .setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void goToHomeScreen() {
        finish();
        Intent splashIntent = new Intent(ChooseCafeteriaActivity.this, MainActivity.class);
        startActivity(splashIntent);
    }

    private class GetServers extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_SERVERS);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.e("DEBUG",conn.getResponseCode()+"");
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DEBUG",conn.getResponseMessage());
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

        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Type listType = new TypeToken<ArrayList<Cafeteria>>() {
                }.getType();
                servers = new Gson().fromJson(response, listType);
                for (Cafeteria server: servers){
                    Log.e("SERVERS","servers name: " + server.getCafeteriaName());
                    serversNames.add(server.getCafeteriaName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseCafeteriaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, serversNames);
                autoCompleteTvCafeteria.setAdapter(adapter);
                autoCompleteTvCafeteria.setVisibility(View.VISIBLE);
                autoCompleteTvCafeteria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        autoCompleteTvCafeteria.showDropDown();
                    }
                });
            }
            Log.e("SERVERS","servers size: " + servers.size());

        }
    }



}
