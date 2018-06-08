package com.example.nishit.parkingmanagement;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nishit on 3/20/2018.
 */


public class map extends AppCompatActivity implements OnMapReadyCallback {



    private static final String TAG = "Map";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 14f;
    private  Geocoder geocoder;
    private List<Address> addresses;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageView mGps,imageButtonGo;
    private EditText edCurrent;
    private LatLng currentLatLng;
    private RequestQueue requestQueue;
    private CountDownTimer timer_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mGps = (ImageView) findViewById(R.id.gps);
        imageButtonGo = (ImageView) findViewById(R.id.imageButtonGo);
        edCurrent = (EditText) findViewById(R.id.input_search);
        requestQueue = Volley.newRequestQueue(this);
        getLocationPermission();

    }

    private void init(){
        Log.d(TAG, "init: initializing");

        parkingSlotsMarker();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick clicked gps  icon");
                getDeviceLocation();
            }
        });

        imageButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNearestParkingSlotAlgorithm();
            }
        });


    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(map.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;


        if (mLocationPermissionsGranted) {

            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            // mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
            init();

        }
    }


    private void parkingSlotsMarker(){
        Log.d(TAG, "parkingslotMarker: geolocating");
        String parkingmarker1="",parkingmarker2="";
        if(mLocationPermissionsGranted){
            //setVolleyMarker();
            List<Address> addresses_slot1,addresses_slot2;

            Intent intent = getIntent();
            Double lat1 = intent.getDoubleExtra("lat1",0);
            Double log1 = intent.getDoubleExtra("log1",0);
            Double lat2 = intent.getDoubleExtra("lat2",0);
            Double log2 = intent.getDoubleExtra("log2",0);
            try {

                addresses_slot1 = geocoder.getFromLocation(lat1, log1,1);
                addresses_slot2 = geocoder.getFromLocation(lat2, log2,1);
                parkingmarker1 = addresses_slot1.get(0).getAddressLine(0);
                parkingmarker2 = addresses_slot1.get(0).getAddressLine(0);


            } catch (IOException e) {
                e.printStackTrace();
            }


            moveCamera(new LatLng(lat1
                            ,log1), DEFAULT_ZOOM,
                    parkingmarker1);

            moveCamera(new LatLng(
                            lat2
                            ,log2), DEFAULT_ZOOM,
                    parkingmarker2);

        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());



                            try {
                                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(),1);
                                edCurrent.setText(addresses.get(0).getAddressLine(0));
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                            //Radius
                            CircleOptions circleOptions = new CircleOptions()
                                    .center(currentLatLng)
                                    .radius(2000)
                                    .fillColor(0x330000FF)
                                    .strokeWidth(3)
                                    .strokeColor(Color.RED);

                            mMap.addCircle(circleOptions);

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,"My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(map.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom),5000,null);

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

/*    final Double latitude[] = new Double[2];
    final Double longitude[] = new Double[2];*/
        public void findNearestParkingSlotAlgorithm(){

            Log.d("Current Location lat", ""+currentLatLng.latitude);
            Log.d("Current Location log", ""+currentLatLng.longitude);

            final double current_longitude = currentLatLng.longitude;
            final double current_latitude = currentLatLng.latitude;


            /*getVolleyLocation(new DataCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("location");

                        for(int i=0;i<jsonArray.length();i++ ) {
                            JSONObject location = jsonArray.getJSONObject(i);

                            latitude[i] = location.getDouble("latitude");
                            longitude[i] = location.getDouble("longitude");
                            Log.d("Distance 1", "" + longitude[i]);

                            //distanceKM[i] = distance(current_latitude,current_longitude,latitude[i],longitude[i]);

                        }
                        } catch (JSONException e) {
                        Log.e("Error", e.getMessage(), e);
                    }
                }
            });*/
                    Double distanceTotal1, distanceTotal2;
                    Intent intent = getIntent();
                    Double lat1 = intent.getDoubleExtra("lat1",0);
                    Double log1 = intent.getDoubleExtra("log1",0);
                    Double lat2 = intent.getDoubleExtra("lat2",0);
                    Double log2 = intent.getDoubleExtra("log2",0);
                    final int customerid = intent.getIntExtra("customerid",0);
                    final int slot1 = intent.getIntExtra("slot1",0);
                    final int slot2 = intent.getIntExtra("slot2",0);




                    /* lat1 = latitude[0];
                            log1 = longitude[0];
                            lat2 = latitude[1];
                            log2 = longitude[1];
                    */
                    distanceTotal1 = distance(current_latitude,current_longitude,lat1,log1);
                    distanceTotal2 = distance(current_latitude,current_longitude,lat2,log2);

                    Log.d("Dis 1",""+distanceTotal1);
                    Log.d("Dis 2",""+distanceTotal2);


                    // Maker Selection Logic is here
                    int MarkerId=0;
                    if(distanceTotal1 > distanceTotal2){
                        if(distanceTotal2 <= 2){
                            MarkerId = 2;
                        }else if(distanceTotal1 <= 2){
                            MarkerId = 1;
                        }else {
                            MarkerId = 3;
                        }
                    }else{
                        if(distanceTotal1 < distanceTotal2){
                            if(distanceTotal1 <= 2){
                                MarkerId = 1;
                            }else if(distanceTotal2 <= 2){
                                MarkerId = 2;
                            }else{
                                MarkerId = 3;
                            }

                        }
                    }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Setting message manually and performing action on button click
            final int finalMarkerId = MarkerId;
            int totalslots = 0;
            Double markerLat = null,markerLog=null;
            String locationname="";
            if(finalMarkerId==1){
                totalslots = slot1;
                markerLat = lat1;
                markerLog = log1;
            }else if(finalMarkerId == 2){
                totalslots = slot2;
                markerLat = lat2;
                markerLog = log2;
            }

            final int finalTotalslots = totalslots;
            final Double finalMarkerLat = markerLat;
            final Double finalMarkerLog = markerLog;

            builder.setMessage("Do you want to proceed this application ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(finalMarkerId!=3){

                                Intent intent = new Intent(getApplicationContext(),roadscreen.class);
                                intent.putExtra("locationid", finalMarkerId);
                                intent.putExtra("customerid", customerid);
                                intent.putExtra("totalslot", finalTotalslots);
                                intent.putExtra("makerLatitude", finalMarkerLat);
                                intent.putExtra("makerLongitude", finalMarkerLog);
                                intent.putExtra("IsCheck",1);

                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"Marker is far away from your current location",Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Confirmation Message");
            alert.show();


        }




    /*public void getVolleyLocation(final DataCallback callback){

            final Double distanceKM[] = new Double[2];



            //url
            String url = "http://nishitshah.esy.es/public/api/location";

            // prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                        }

                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("Error",error.getMessage());
                        }
                    });
            Log.d("Distance error ",""+longitude[0]);
            //Wait_until_Downloaded();
           // requestQueue.add(getRequest);
            //return new Object[]{longitude[0],longitude[0]};
            NetworkController.getInstance().addToRequestQueue(getRequest);
        }

*/


/*
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

                                moveCamera(new LatLng(location.getDouble("latitude")
                                                , location.getDouble("longitude")), DEFAULT_ZOOM,
                                        "Parking Slot"+i);


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
        Log.d("Distance error ",""+longitude[0]);
         requestQueue.add(getRequest);

    }

*/

    private double distance(double lat1, double lon1, double lat2, double lon2) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }

        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        private double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }




}

interface DataCallback {
    void onSuccess(JSONObject result);
}
