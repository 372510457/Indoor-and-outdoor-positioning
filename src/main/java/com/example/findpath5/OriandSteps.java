package com.example.findpath5;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OriandSteps  implements SensorEventListener {


    //sensor manager
    private SensorManager sensorManager;

    //Sensors magnetic and Accelerator
    private Sensor mMag;
    private Sensor mAcc;

    //define a var with Callback type
    private Callback callback;


    //array for values of each sensor
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    //parameters used for counting steps
    private int step = 0;   //steps
    private double lstValue = 0;  //Last values of magnitude
    private double curValue = 0;  //Current values of magnitude
    private int moveState = 0;   //The current move state(0 for up and 1 for down)
    private int up = 0;
    private int down = 1;

    //Constructor of this class
    public OriandSteps (Context context, Callback callback) {
        //get access to the sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //get access to the sensors
        mMag = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAcc = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callback = callback;

    }

    //callback interface, used to get the calculated degrees and steps
    public interface Callback {

        void Orientation(int degree);
        void Steps(int steps);

    }

    //register the sensors
    public void registerSensor() {

        sensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);

    }

    //unregister the sensors
    public void unregiterSensor() {
        sensorManager.unregisterListener(this);
    }

    //get the sensors value and calculate orientations and steps
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){

            magneticFieldValues = event.values;

        }

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            accelerometerValues = event.values;

        }

        calOrientation();
        calSteps();

    }

    //calculate orientation
    private void calOrientation() {

        //calculate the orientation
        float[] values = new float[3];
        float[] R = new float[9];

        //get rotation matrix
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        //get rotation around each axis out of rotation matrix
        SensorManager.getOrientation(R, values);

        //convert rotation value to degrees and set the initial orientation
        int degree = (int) Math.toDegrees(values[0]) - 123;

        if (degree < 0) {
            degree += 360;
        } //degree range [0,360]

        callback.Orientation(degree); //pass the calculated degree to the function in the interface

    }

    private void calSteps() {

        double range = 1;   //a range to change the move state
        curValue = magnitude(accelerometerValues);   //calculate the current magnitude

        //Move state: up
        if (moveState == up) {
            if (curValue >= lstValue) {
                lstValue = curValue;
            }
            else {
                //a peak value is detected means the up move state is over
                if (Math.abs(curValue - lstValue) > range) {
                    moveState = 1;//reset the move state to down
                }
            }
        }

        //Move state: down
        if (moveState == down) {
            if (curValue <= lstValue) {
                lstValue = curValue;
            }
            else {
                //a peak value is detected means the down move state is over
                if (Math.abs(curValue - lstValue) > range) {
                    step++;  //Increase the steps
                    moveState = 0;//reset the move state to up
                }
            }
        }

        callback.Steps(step); //pass the calculated steps to the function in the interface

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //get the magnitude
    public double magnitude(float [] acce) {

        double magnitude = Math.sqrt(acce[0]*acce[0]+acce[1]*acce[1]+acce[2]*acce[2]);
        return magnitude; //return the calculated magnitude

    }
}
