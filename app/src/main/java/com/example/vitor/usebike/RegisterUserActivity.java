package com.example.vitor.usebike;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RegisterUserActivity extends AppCompatActivity {

    private Button btnRegistrar;
    private String ipAddress;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfPass;
    private EditText txtDtNasc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        this.setIpAddress(getString(R.string.server_ip));

        this.btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        this.txtUsername = (EditText) findViewById(R.id.txtUsername);
        this.txtEmail = (EditText) findViewById(R.id.txtEmail);
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtConfPass = (EditText) findViewById(R.id.txtConfPass);
        this.txtDtNasc = (EditText) findViewById(R.id.txtDtNasc);

        this.btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }

        });

    }

    private String formatDataAsJSON(){
        final JSONObject root = new JSONObject();
        try {
            root.put("usr_username", txtUsername.getText().toString());
            root.put("usr_email", txtEmail.getText().toString());
            root.put("usr_password", txtPassword.getText().toString());
            root.put("usr_dt_nascimento", txtDtNasc.getText().toString());

            //Se precisar..

//            JSONArray anyarray = new JSONArray();

//            anyarray.put("something");
//            anyarray.put("something2");

//            root.put("arrayOfStuff", anyarray);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d("JWP", "Can't format JSON");
        }

        return null;
    }

    private void sendDataToServer(){

        final String json = formatDataAsJSON();

        new AsyncTask<Void, Void, String>(){
            @Override
            protected void onPostExecute(String result) {
                //Pra dar update na view, só é possivel neste metódo.
                Toast.makeText(RegisterUserActivity.this, "Usuário Cadastrado com Sucesso.",
                        Toast.LENGTH_LONG).show();
                Intent intentLogin = new Intent(RegisterUserActivity.this,
                        LoginActivity.class);
                startActivity(intentLogin);
            }

            @Override
            protected String doInBackground(Void... params) {
                return getServerResponse(json);
            }


        }.execute();

    }

    private String getServerResponse(String json) {

        HttpPost post = new HttpPost(getIpAddress()+"/usuarios");
        try {
            StringEntity entity = new StringEntity(json);

            post.setEntity(entity);
            post.setHeader("Content-type", "application/json");

            DefaultHttpClient client = new DefaultHttpClient();

            BasicResponseHandler handler = new BasicResponseHandler();

            try {
                String response = client.execute(post, handler);

                return response;
            } catch (IOException e) {
                Log.d("VQZ", e.toString());
            }

        } catch (UnsupportedEncodingException e) {
            Log.d("VQZ", e.toString());
        }

        return "Unable to Contact Server";
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
