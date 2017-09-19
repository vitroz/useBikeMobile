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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import modelo.User;


public class LoginActivity extends AppCompatActivity {

    private Button btnCadUser;
    private Button btnLogIn;
    private EditText txtUser;
    private EditText txtPassword;
    private User user = new User();

    //Para testar localmente, sempre necessario verificar o IP da maquina HOST. ifconfig
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setIpAddress(getString(R.string.server_ip));


        //Para testar localmente, sempre necessario verificar o IP da maquina HOST. ifconfig
        // GET DE USUARIOS!!
        // new JSONTask().execute(getIpAddress()+"/usuarios");

        this.btnCadUser = (Button) findViewById(R.id.btnCadUser);
        this.btnLogIn = (Button) findViewById(R.id.btnLogIn);

        this.btnCadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginActivity.this,
                        RegisterUserActivity.class);
                startActivity(intent1);
            }

        });

        this.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             authenticate();
            }

        });


    }

    public String getIpAddress() {
        return ipAddress;
    }


    public class JSONTask extends AsyncTask<String, String, String> {


        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray  parentArray = parentObject.getJSONArray("users");

                for(int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    String usr_name = finalObject.getString("usr_username");
                    String usr_email = finalObject.getString("usr_email");

                    System.out.println("");
                    System.out.println(usr_name);
                    System.out.println(usr_email);
                }


                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
        }

    }

    private String formatDataAsJSON(){
        final JSONObject root = new JSONObject();
        try {

            txtUser = (EditText) findViewById(R.id.txtUser);
            txtPassword = (EditText) findViewById(R.id.txtPassword);

            root.put("usr_username", txtUser.getText().toString());
            root.put("usr_password", txtPassword.getText().toString());

            //Se precisar..

//           JSONArray anyarray = new JSONArray();

//           anyarray.put("something");
//           anyarray.put("something2");

//           root.put("arrayOfStuff", anyarray);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d("JWP", "Can't format JSON");
        }

        return null;
    }

    private void authenticate(){

        final String json = formatDataAsJSON();

        new AsyncTask<String, String, String>(){
            @Override
            protected void onPostExecute(String result) {
                //Pra dar update na view, só é possivel neste metódo.
                System.out.println(result);
            if(result.equals("Unable to Contact Server")){
                Toast.makeText(LoginActivity.this, "Server encontra-se indisponível no momento!",
                        Toast.LENGTH_LONG).show();
            }if(result.equals("{\"users\":[]}")) {
                    Toast.makeText(LoginActivity.this, "Usuário, Email ou Senha incorretos!",
                            Toast.LENGTH_LONG).show();
            }if( (!result.equals("Unable to Contact Server")) && (!result.equals("{\"users\":[]}")) ){
                    Toast.makeText(LoginActivity.this, "Usuário "+user.getUsername()+" Localizado com Sucesso!",
                            Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent(LoginActivity.this,
                            UserViewActivity.class);

                    //Create the bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("USER_ID", getUser().getId());
                    bundle.putString("USER_NAME", getUser().getUsername());
                    bundle.putString("USER_EMAIL",getUser().getEmail());
                    bundle.putString("USER_DT_NASCIMENTO",getUser().getDtNascimento());

                    intent2.putExtras(bundle);

                    startActivity(intent2);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String jsonArray = getServerResponse(json);

                carregaDadosUser(getUser(),jsonArray);

                return jsonArray;
            }


        }.execute();

    }

    private String getServerResponse(String json) {

        HttpPost post = new HttpPost(getIpAddress()+"/authenticate");
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

    private void carregaDadosUser(User user, String arrayResponse){
        BufferedReader reader = null;
        try {

            String finalJson = arrayResponse;
            System.out.println("carregar dados: "+finalJson);

            JSONObject parentObject = new JSONObject(finalJson);
            JSONArray  parentArray = parentObject.getJSONArray("users");

            for(int i = 0; i < parentArray.length(); i++) {
                JSONObject finalObject = parentArray.getJSONObject(i);

                user.setId(finalObject.getString("usr_id"));
                user.setUsername(finalObject.getString("usr_username"));
                user.setEmail(finalObject.getString("usr_email"));
                user.setDtNascimento(finalObject.getString("usr_dt_nascimento"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public User getUser(){
        return this.user;
    }



}



