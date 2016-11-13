package com.cafeteria.cafeteria_store.ui;

import com.cafeteria.cafeteria_store.R;
import com.cafeteria.cafeteria_store.utils.ApplicationConstant;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private String emailTxt;
    private String passwordTxt;

    /**
     *  The Id of tbe user that this class logs in - PK for primary key
     *  this is the user id in the database
     */
    private int userPKId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);


        etPassword = (EditText) findViewById(R.id.etPassword);
        etMail = (EditText) findViewById(R.id.etMail);


        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginAndValidationTask().execute();
            }
        });

    }

    private class LoginAndValidationTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.equals("null")) {
                //continue to orders screen
                Log.e("DEBUG",response);
            } else {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.USER_VALIDATION_URL + "?email=" + emailTxt + "&pass=" + passwordTxt);
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

            String responseString = response.toString().trim();
            Log.e("DEBUG","user response : "+ responseString);
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            emailTxt = etMail.getText().toString().trim();
            passwordTxt = etPassword.getText().toString().trim();
        }
    }
}