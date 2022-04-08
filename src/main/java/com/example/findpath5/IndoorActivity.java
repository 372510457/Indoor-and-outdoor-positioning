package com.example.findpath5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class IndoorActivity extends AppCompatActivity {
    private Button collect;

    public static final String EXTER_MESSAGE = "com.example.findpath5.Message";
    private boolean enable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        collect = (Button) findViewById(R.id.collect);
    }

    public void gotocollect(View view) {
        Intent intent = new Intent(this, CollectWiFi.class);
        startActivity(intent);
    }

    public void location(View view) {
        Intent location = new Intent(this, Locationing.class);

        if (enable == false) {

            location.putExtra(EXTER_MESSAGE, "0");

        } else if (enable == true) {

            location.putExtra(EXTER_MESSAGE, "1");

        }

        startActivity(location);

    }

    public void optimize(View view) {
        if (enable == false) {
            Toast.makeText(this, "Optimize enabled", Toast.LENGTH_SHORT).show();
            enable = true;
        } else if (enable == true) {
            Toast.makeText(this, "Optimize disabled", Toast.LENGTH_SHORT).show();
            enable = false;
        }

    }


}