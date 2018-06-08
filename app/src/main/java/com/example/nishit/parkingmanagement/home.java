package com.example.nishit.parkingmanagement;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nishit on 2/20/2018.
 */

public class home extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    final Double latitude[] = new Double[2];
    final Double longitude[] = new Double[2];
    final int slots[] = new int[2];
    private RequestQueue requestQueue;
    int customerid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        customerid = Integer.parseInt(intent.getStringExtra("ID"));
        setVolleyMarker();
        if(isServicesOK()){
            init();
        }

    }
    private void init(){
        ImageView btnMap = (ImageView) findViewById(R.id.btnSubmit);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, map.class);
                intent.putExtra("lat1",latitude[0]);
                intent.putExtra("log1",longitude[0]);
                intent.putExtra("lat2",latitude[1]);
                intent.putExtra("log2",longitude[1]);
                intent.putExtra("slot1",slots[0]);
                intent.putExtra("slot2",slots[1]);
                intent.putExtra("customerid",customerid);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(home.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(home.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logout_fun();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void logout_fun(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void setVolleyMarker(){
        //url
        String url = "http://nishitshah.esy.es/public/api/location";

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("location");

                            for(int i=0;i<jsonArray.length();i++ ) {
                                JSONObject location = jsonArray.getJSONObject(i);
                                latitude[i] = location.getDouble("latitude");
                                longitude[i] = location.getDouble("longitude");
                                slots[i] = location.getInt("slots");
                            }
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage(), e);
                        }
                    }

                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Error",error.getMessage());
            }
        });
        requestQueue.add(getRequest);

    }

}
