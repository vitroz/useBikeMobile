package com.example.vitor.usebike;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import adapter.BikeAdapter;
import modelo.Bike;

import com.example.vitor.usebike.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static java.lang.Double.*;

public class UserViewActivity extends AppCompatActivity  implements OnMapReadyCallback {

    TabHost tabHost;
    private EditText txtUsernameData;
    private EditText txtEmailData;
    private EditText txtDtNascimentoData;
    private String ipAddress;
    private ArrayList<Bike> bikes = new ArrayList<Bike>();
    private ArrayList<Bike> bikesGeral = new ArrayList<Bike>();

    private ListView listbikes;
    GoogleMap mMap;
    MapView mMapView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng calhau = new LatLng(-2.491694, -44.269220);
        for(Bike bike : bikesGeral){
            LatLng pos = new LatLng(parseDouble(bike.getLatitude()), parseDouble(bike.getLongitude()));
            googleMap.addMarker(new MarkerOptions().position(pos)
                    .title("BIKE: "+bike.getNome()));
        }
        googleMap.addMarker(new MarkerOptions().position(calhau)
                .title("Calhau"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(calhau));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 11.0f ) );
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        this.setIpAddress(getString(R.string.server_ip));

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        txtUsernameData = (EditText) findViewById(R.id.txtUsernameData);
        txtEmailData = (EditText) findViewById(R.id.txtEmailData);
        txtDtNascimentoData = (EditText) findViewById(R.id.txtDtNascimentoData);
        listbikes = (ListView) findViewById(R.id.listbikes);
        Bundle bundle = getIntent().getExtras();

        mMapView = (MapView) findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String user_id = bundle.getString("USER_ID");
        new JSONTask().execute(getIpAddress()+"/usuarios/"+user_id+"/bikes");
        new JSONTaskGetBikes().execute(getIpAddress()+"/bikes");

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab User");
        spec.setContent(R.id.User);
        spec.setIndicator("User");
        host.addTab(spec);

        //Extract the data…
        String username = bundle.getString("USER_NAME");
        String email = bundle.getString("USER_EMAIL");
        String dtNascimento = bundle.getString("USER_DT_NASCIMENTO");
        dtNascimento =  dtNascimento.substring( 0, dtNascimento.indexOf("T"));
        txtUsernameData.setText(username);
        txtEmailData.setText(email);
        txtDtNascimentoData.setText(dtNascimento);

        //Tab 2
        spec = host.newTabSpec("Tab Bikes");
        spec.setContent(R.id.Bikes);
        spec.setIndicator("Bikes");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab LogOut");
        spec.setContent(R.id.LogOut);
        spec.setIndicator("LogOut");
        host.addTab(spec);


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
                JSONArray parentArray = parentObject.getJSONArray("bikes");

                for(int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    Bike bike = new Bike();

                    bike.setNome(finalObject.getString("bks_nome"));
                    bike.setLatitude(finalObject.getString("bks_latitude"));
                    bike.setLongitude(finalObject.getString("bks_longitude"));
                    bike.setDisponivel(finalObject.getBoolean("bks_disponivel"));

                    bikes.add(bike);
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

            BikeAdapter bkAdapter = new BikeAdapter(UserViewActivity.this,bikes);
            listbikes.setAdapter(bkAdapter);

        }

    }

    public class JSONTaskGetBikes extends AsyncTask<String, String, String> {

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
                JSONArray parentArray = parentObject.getJSONArray("bikes");

                for(int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    Bike bike = new Bike();

                    bike.setNome(finalObject.getString("bks_nome"));
                    bike.setLatitude(finalObject.getString("bks_latitude"));
                    bike.setLongitude(finalObject.getString("bks_longitude"));
                    bike.setDisponivel(finalObject.getBoolean("bks_disponivel"));

                    bikesGeral.add(bike);
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

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

}
