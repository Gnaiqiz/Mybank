package com.example.mybank;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class accounts extends AppCompatActivity {
    private String url = "https://60102f166c21e10017050128.mockapi.io/labbbank/accounts";
    private ListView list_of_accounts;
    private ArrayList<String> accounts;
    private Button refresh_button;
    SharedPreferences pref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        list_of_accounts= findViewById(R.id.listView);
        refresh_button=findViewById(R.id.button_refresh);

        Intent intent = getIntent();

        if (isNetworkAvailable()==false) {
            Toast.makeText(accounts.this, "No internet connection!", Toast.LENGTH_LONG).show();
            try {

                FileInputStream fileIn=openFileInput("accounts.txt");
                InputStreamReader InputRead= new InputStreamReader(fileIn);
                BufferedReader bufferedReader = new BufferedReader(InputRead);

                String line;
                accounts = new ArrayList<String>();
                while ((line = bufferedReader.readLine()) != null) {
                    accounts.add(line.toString());
                }

                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<String>(accounts.this, android.R.layout.simple_list_item_1 , accounts);
                list_of_accounts.setAdapter(arrayAdapter);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(accounts.this, "Welcome!", Toast.LENGTH_LONG).show();
            volleyGet();
        }

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleyGet();
                Toast.makeText(accounts.this, "Page refreshed!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void volleyGet(){
        accounts = new ArrayList<String>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    FileOutputStream fileOut=openFileOutput("accounts.txt", MODE_PRIVATE);

                    for (int i=0;i<response.length();i++){
                        JSONObject account = response.getJSONObject(i);

                        String id = account.getString("id");
                        String accountName = account.getString("accountName");
                        String amount = account.getString("amount");
                        String iban = account.getString("iban");
                        String currency = account.getString("currency");
                        UserAccount userAccount = new UserAccount(id,accountName,amount,iban,currency);

                        Log.e(id,userAccount.toString());
                        accounts.add(userAccount.toString());
                        fileOut.write((userAccount.toString()+"\n").getBytes());
                    }
                    fileOut.close();
                    ArrayAdapter<String> arrayAdapter
                            = new ArrayAdapter<String>(accounts.this, android.R.layout.simple_list_item_1 , accounts);
                    list_of_accounts.setAdapter(arrayAdapter);

                }catch (JSONException | IOException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}