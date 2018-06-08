package com.example.nishit.parkingmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Nishit on 3/14/2018.
 */

public class register extends AppCompatActivity {
    EditText ed_fname ,ed_lname,ed_email,ed_phone,ed_password;
    RequestQueue requestQueue;
    Button btnSubmit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ed_email = (EditText)findViewById(R.id.emai2) ;
        ed_fname = (EditText)findViewById(R.id.fname);
        ed_lname = (EditText)findViewById(R.id.lname);
        ed_phone = (EditText)findViewById(R.id.phone);
        ed_password = (EditText)findViewById(R.id.password);
        requestQueue = Volley.newRequestQueue(this);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed_email.getText().toString();
                String fname = ed_fname.getText().toString();
                String lname = ed_lname.getText().toString();
                String password = ed_password.getText().toString();
                String phone = ed_phone.getText().toString();
                if(isEmpty(email) || isEmpty(fname)|| isEmpty(lname) || isEmpty(password) || isEmpty(phone))
                {
                    Toast.makeText(getApplicationContext(),"All fields are Required",Toast.LENGTH_SHORT).show();

                }
                else {

                    addCustomer();
                }
            }
        });
    }

    private void addCustomer(){
//        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nishitshah.esy.es/public/api/customers/add";
        Map<String,String> jsonParam = new HashMap<String, String>();
        jsonParam.put("fname",ed_fname.getText().toString());
        jsonParam.put("lname",ed_lname.getText().toString());
        jsonParam.put("email",ed_email.getText().toString());
        jsonParam.put("password",ed_password.getText().toString());
        jsonParam.put("phone",ed_phone.getText().toString());
        Log.d("JSON",""+new JSONObject(jsonParam));
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(jsonParam),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Register Scussfully",Toast.LENGTH_LONG).show();
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

}
