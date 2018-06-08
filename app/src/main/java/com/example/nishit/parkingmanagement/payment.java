package com.example.nishit.parkingmanagement;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nishit on 3/22/2018.
 */

public class payment extends AppCompatActivity {

    Button btnNavi,checkslot;
    int locationid,customerid,totalslot,slot_num;
    Double booklatitude,booklongitude;
    private List<Address> addresses;
    TextView txtaddress,txtslot;
    private Geocoder geocoder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        Intent intent = getIntent();
        customerid= intent.getIntExtra("customerid",0);
        locationid = intent.getIntExtra("locationid",0);
        slot_num =  intent.getIntExtra("slotnum",0);
        booklatitude = intent.getDoubleExtra("makerLatitude",0);
        booklongitude = intent.getDoubleExtra("makerLongitude",0);
        txtaddress = (TextView) findViewById(R.id.address);
        txtaddress.setTypeface(null, Typeface.BOLD);
        txtslot = (TextView) findViewById(R.id.slotnum);
        txtslot.setTypeface(null, Typeface.BOLD);
        btnNavi = (Button) findViewById(R.id.navigateLocation);
        checkslot = (Button) findViewById(R.id.checkslot);
        checkslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),roadscreen.class);
                intent.putExtra("IsCheck",0);
                intent.putExtra("locationid", locationid);
                intent.putExtra("customerid", customerid);
                intent.putExtra("makerLatitude", booklatitude);
                intent.putExtra("makerLongitude", booklongitude);
                intent.putExtra("totalslot", 10);

                startActivity(intent);
            }
        });
        try {
            addresses = geocoder.getFromLocation(booklatitude,booklongitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtaddress.setText("Parking Location - "+addresses.get(0).getAddressLine(0));
        txtslot.setText("Allocated Slot - "+slot_num);


        btnNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),navigationMap.class);
                intent.putExtra("makerLatitude", booklatitude);
                intent.putExtra("makerLongitude", booklongitude);
                intent.putExtra("ID",customerid);
                intent.putExtra("Slotnum",slot_num);
                startActivity(intent);
            }
        });
    }
}
