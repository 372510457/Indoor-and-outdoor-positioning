package com.example.findpath5;
////////////////////////////////////////
/*
 *Authors: Hanlin Wang s2171010
 *         Yang Meng s2019143
 *         Xinhu Feng s2059747
 *
 *
 *Description:    In this project, it mainly has the following functions
 *              1. Using Sensor data to implement the indoor-positioning
 *              2. Using PDR to implement the trajectory alignment in world coordinates
 *              3. Using Google Map API and GPS to tracking the path in the outdoors
 *              4. WIFI collecting to create the database
 *              5. Investigate the algorithm of KNN to find the path automatically
 *
 *Layoutfile: activity_main.xml
 *            activity_maps.xml
 *            activity_locationing.xml
 *            activity_second.xml
 *
 * */

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    public static final String TAG ="MainActivity";
    public static final int ERR_DIALOG_REQ =9000;

    private Button imindoor;

    private  boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imindoor = (Button) findViewById(R.id.indoor);
        RelativeLayout relativeLayout = findViewById(R.id.rl);
        AnimationDrawable anim = (AnimationDrawable) relativeLayout.getBackground();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    anim.start();
                    flag = false;
                }else{
                    anim.stop();
                    flag = true;
                }

            }
        });
        if(isServicesUp()){
            initActivity();
        }
    }

    //Initializing the outdoors tracking
    private void initActivity(){
        Button startBtn = findViewById(R.id.start_map_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Checking the Google map service is working
    public boolean isServicesUp(){
        Log.d(TAG, "isServicesUp: checking version...");
        int avail = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(avail == ConnectionResult.SUCCESS){
            Log.d("TAG","isServicesUp: Google Play Services working.");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(avail)){
            Log.d(TAG, "isServicesUp: error occured:");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, avail, ERR_DIALOG_REQ);
            dialog.show();
        }
        else{
            Log.d(TAG, "isServicesUp: error in connection:");
            Toast.makeText(this, "Unable to connect to Google Map",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void gotoindoor(View view) {
        Intent intent1 = new Intent(this, IndoorActivity.class);
        startActivity(intent1);
    }
}