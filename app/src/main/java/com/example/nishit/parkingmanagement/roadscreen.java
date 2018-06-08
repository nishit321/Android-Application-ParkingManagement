package com.example.nishit.parkingmanagement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;
import java.util.Timer;
/**
 * Created by Nishit on 3/11/2018.
 */

public class roadscreen extends AppCompatActivity {
    RequestQueue requestQueue;

    private  Geocoder geocoder;
    private List<Address> addresses;
    private DatabaseReference mdatabase;
    Button slot1,slot2,slot5,btnbook;
    TextView txtLocation;
    String name="null",value="null";
    Handler handler;
    int locationid,customerid,totalslot;
    Double booklatitude,booklongitude;
    int flag=0;
    int slot11,slot21,slot51;
    int led_1,led_2,led_5;
    int isCheck;

    int globalvar;
    CountDownTimer timer_count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkingslot_screen);
        FirebaseApp.initializeApp(roadscreen.this);

        txtLocation = (TextView)findViewById(R.id.loaction);
        txtLocation.setTypeface(null, Typeface.BOLD);
        requestQueue = Volley.newRequestQueue(this);
        //firebase
        firebase();

        Intent intent = getIntent();
        customerid= intent.getIntExtra("customerid",0);
        locationid = intent.getIntExtra("locationid",0);
        totalslot = intent.getIntExtra("totalslot",0);
        booklatitude = intent.getDoubleExtra("makerLatitude",0);
        booklongitude = intent.getDoubleExtra("makerLongitude",0);
        isCheck = intent.getIntExtra("IsCheck",0);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(booklatitude,booklongitude,1);
            txtLocation.setText("Parking Location - "+addresses.get(0).getAddressLine(0));
        }catch (IOException e){
            e.printStackTrace();
        }

        btnbook =(Button)findViewById(R.id.btnbook);
        if(isCheck == 0){
            btnbook.setVisibility(View.INVISIBLE);
        }

        slot1 = (Button)findViewById(R.id.buttonleft_1);
        slot2 =(Button) findViewById(R.id.buttonleft_2);
        slot5 =(Button) findViewById(R.id.buttonright_2);

        btnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(roadscreen.this);
                builder.setTitle("Confirmation ");
                builder.setMessage("Are you sure want to confirm the book slot ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                globalvar=0;
                                if(slot11 == 0 && slot111 ==0){
                                    globalvar = 1;
                                }else if(slot21 == 0 && slot211 ==0){
                                    globalvar = 2;
                                }else if(slot51 == 0 && slot511 ==0){
                                    globalvar = 5;
                                }else{
                                    globalvar = 10;
                                }
                                volleyPOSTbooking();
                                mdatabase = FirebaseDatabase.getInstance().getReference();

                                if(globalvar == 1 ){
                                   mdatabase.child("led_1").setValue(1);

                                }
                                if(globalvar == 2){
                                   mdatabase.child("led_2").setValue(1);

                                }
                                if(globalvar == 5){
                                 mdatabase.child("led_3").setValue(1);

                                }
                                if(globalvar!=10){
                                    Intent intent = new Intent(getApplicationContext(),payment.class);
                                    intent.putExtra("locationid", locationid);
                                    intent.putExtra("customerid", customerid);
                                    intent.putExtra("slotnum", globalvar);

                                    intent.putExtra("makerLatitude", booklatitude);
                                    intent.putExtra("makerLongitude", booklongitude);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext()," Sorry! No slot available! ",Toast.LENGTH_LONG).show();
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
        });



    }

    public void volleyPOSTbooking(){

        String url = "http://nishitshah.esy.es/public/api/booking/add";
        Map<String,Integer> jsonParam = new HashMap<String,Integer>();
        jsonParam.put("customer_id",customerid);
        jsonParam.put("location_id",locationid);
        jsonParam.put("slot_num",globalvar);
        Log.d("JSON",""+new JSONObject(jsonParam));
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(jsonParam),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }

                });
        requestQueue.add(getRequest);
    }


    int slot111,slot211,slot511;
   public void firebase(){

       //slot1
       mdatabase = FirebaseDatabase.getInstance().getReference().child("led_1");
       //   mdatabase.child("Name").setValue("Shivam Shah");
       mdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               slot111 = dataSnapshot.getValue(Integer.class);
               Log.d("LED 1",""+slot111);
               if(slot111 == 1){
                    slot1.setBackgroundResource(R.color.orange);
               }else if(slot111 == 0){
                   if(slot11 == 1 ){
                       slot1.setBackgroundResource(R.color.red);
                   }else if(slot11 == 0){
                       slot1.setBackgroundResource(R.color.green);
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


       //slot2
       mdatabase = FirebaseDatabase.getInstance().getReference().child("led_2");
       //   mdatabase.child("Name").setValue("Shivam Shah");
       mdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               slot211 = dataSnapshot.getValue(Integer.class);
               Log.d("LED 2",""+slot211);
               if(slot211 == 1){
                   slot2.setBackgroundResource(R.color.orange);
               }else if(slot211 == 0){
                    if(slot21 == 1){
                       slot2.setBackgroundResource(R.color.red);
                   }else if(slot21 == 0){
                       slot2.setBackgroundResource(R.color.green);
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

       //slot2
       mdatabase = FirebaseDatabase.getInstance().getReference().child("led_3");
       //   mdatabase.child("Name").setValue("Shivam Shah");
       mdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               slot511 = dataSnapshot.getValue(Integer.class);
               Log.d("LED 3",""+slot511);
                if(slot511 == 1){
                    slot5.setBackgroundResource(R.color.orange);
                }else if(slot511 == 0){
                    if(slot51 == 1 ){
                        slot5.setBackgroundResource(R.color.red);
                    }else if(slot51 == 0){
                        slot5.setBackgroundResource(R.color.green);
                    }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


//-----------------Check parking(orange)---------------------


       //slot1
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Slot_1");
        //   mdatabase.child("Name").setValue("Shivam Shah");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                slot11 = dataSnapshot.getValue(Integer.class);
                Log.d("slot111",""+slot111);
                if(slot111 == 1){
                    slot1.setBackgroundResource(R.color.orange);
                }
                else if(slot11 == 1 ){
                    slot1.setBackgroundResource(R.color.red);
                }else if(slot11 == 0){
                    slot1.setBackgroundResource(R.color.green);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


       //slot2
       mdatabase = FirebaseDatabase.getInstance().getReference().child("Slot_2");
       //   mdatabase.child("Name").setValue("Shivam Shah");
       mdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               slot21 = dataSnapshot.getValue(Integer.class);
               Log.d("slot211",""+slot211);
               if(slot211 == 1){
                   slot2.setBackgroundResource(R.color.orange);
               }
               else if(slot21 == 1 ){
                   slot2.setBackgroundResource(R.color.red);
               }else if(slot21 == 0){
                   slot2.setBackgroundResource(R.color.green);
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

       //slot2
       mdatabase = FirebaseDatabase.getInstance().getReference().child("Slot_3");
       //   mdatabase.child("Name").setValue("Shivam Shah");
       mdatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               slot51 = dataSnapshot.getValue(Integer.class);
               Log.d("slot511",""+slot511);
               if(slot511 == 1) {
                   slot5.setBackgroundResource(R.color.orange);
               }
               else if(slot51 == 1 ){
                   slot5.setBackgroundResource(R.color.red);
               }else if(slot51 == 0){
                   slot5.setBackgroundResource(R.color.green);
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


   }



}
