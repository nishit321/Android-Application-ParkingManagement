package com.example.nishit.parkingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nishit on 2/20/2018.
 */

public class splash extends AppCompatActivity{
    private TextView tv;
    private ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        tv = (TextView)findViewById(R.id.txtname);
        img = (ImageView)findViewById(R.id.imgview);
        Animation myAnimation = AnimationUtils.loadAnimation(this,R.anim.myanimation);
        tv.setAnimation(myAnimation);
        img.setAnimation(myAnimation);
        final Intent intent = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch (InterruptedException e){
                        e.printStackTrace();
                }finally {
                    startActivity(intent);
                    finish();
                }
            }

        };
        timer.start();
    }
}
