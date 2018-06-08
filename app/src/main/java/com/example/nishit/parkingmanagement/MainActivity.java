package com.example.nishit.parkingmanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText ed_name,ed_pass;
    Button btnSubmit;
    TextView btnRegistration;
    ImageView img;
    TextView txtUser,txtPass;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        requestQueue = Volley.newRequestQueue(this);
        ed_name = (EditText)findViewById(R.id.username);
        ed_pass = (EditText)findViewById(R.id.password);
        img = (ImageView)findViewById(R.id.imageView2);
        btnSubmit = (Button)findViewById(R.id.button);
        txtUser = (TextView) findViewById(R.id.validationUser);
        txtPass = (TextView) findViewById(R.id.validationPassword);
        btnRegistration = (TextView) findViewById(R.id.btnRegistration);

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),register.class);
                startActivity(intent);
            }
        });


        Animation animation = AnimationUtils.loadAnimation(this,R.anim.myanimation);
        ed_name.setAnimation(animation);
        ed_pass.setAnimation(animation);
        img.setAnimation(animation);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(ed_name)){
                    //Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
                    txtUser.setVisibility(view.VISIBLE);
                }
                if(isEmpty(ed_pass)){
                    //Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
                    txtPass.setVisibility(view.VISIBLE);
                }

                if(!isEmpty(ed_name) && !isEmpty(ed_pass)){
                    volleyLogin();
                }
            }
        });

    }

    private void volleyLogin(){
//        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nishitshah.esy.es/public/api/customers/checklogin/login";
        Map<String,String> jsonParam = new HashMap<String, String>();

        jsonParam.put("email",ed_name.getText().toString());
        jsonParam.put("password",ed_pass.getText().toString());

        Log.d("JSON",""+new JSONObject(jsonParam));
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(jsonParam),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        String id = null;
                        String arg = null;
                        try {
                            id = (String) response.get("id");

                            arg = (String) response.get("msg");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(arg.equals("success")){
                            Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),home.class);
                            intent.putExtra("ID",id);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Incorrect Login Details",Toast.LENGTH_LONG).show();

                        }

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

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
