package com.example.findpath5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Locationing extends AppCompatActivity implements OriandSteps.Callback {

    //set two var with the following types
    ViewSteps viewSteps;
    OriandSteps orientation;

    TextView mOrientText;
    TextView mStepcounterText;
    EditText setlength;

    Button set;

    //set the step length
    private int mstepln = 3;
    //judge whether the optimize is enabled or not
    private int optimize_Enable = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationing);

        //get the previous intent activity
        Intent intent = getIntent();
        String enable = intent.getStringExtra(IndoorActivity.EXTER_MESSAGE);
        optimize_Enable = Integer.valueOf(enable); //transfer the passed value into integer

        //find the items
        mOrientText = (TextView) findViewById(R.id.textView);
        mStepcounterText = (TextView) findViewById(R.id.textView2);
        setlength = (EditText) findViewById(R.id.editText);

        set = (Button) findViewById(R.id.button);

        //instance viewSteps
        viewSteps = new ViewSteps(this);
        viewSteps = findViewById(R.id.viewSteps);

        //instance orientation
        orientation = new OriandSteps(this, this);
    }

    //set the step lenght
    public void SetStepln(View view) {
        String textnum = setlength.getText().toString();
        mstepln = Integer.valueOf(textnum);
        viewSteps.SetStepLength(mstepln);
    }


    //register sensors
    @Override
    protected void onResume() {

        super.onResume();
        orientation.registerSensor();

    }

    //unregister sensors
    @Override
    protected void onPause() {
        super.onPause();
        orientation.unregiterSensor();

    }

    //Override the functions in the Callback interface
    @Override
    public void Orientation (int degree) {
        //filter the degrees if optimize enabled
        if(optimize_Enable == 1) {
            if (degree >= 175 && degree <= 185
                    || degree >= 85 && degree <= 95
                    || degree >= 265 && degree <= 275
                    || degree >= 355 && degree <= 360
                    || degree >= 0 && degree <= 5) {
                viewSteps.Turn(degree);//set the degree
                mOrientText.setText("Degree:" + degree);
            } //not filter
        } else if(optimize_Enable == 0) {
            viewSteps.Turn(degree);
            mOrientText.setText("Degree:" + degree);
        }

    }

    //Override the functions in the Callback interface
    @Override
    public void Steps(int steps) {

        mStepcounterText.setText("Steps:" + steps);
        viewSteps.UpdatePosition(steps);

    }

}