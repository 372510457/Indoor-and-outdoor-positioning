package com.example.findpath5;
////////////////////////////////////////
/*
 *Authors: Hanlin Wang s2171010
 *
 *
 *
 *Description:    In this part, it mainly showing the function of
 *                tracking the path in the outdoor based on Google Map API
 *                and roadApi which located in JSON data.
 *
 *
 *Layoutfile:  activity_maps.xml
 *
 *
 * */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // All the statement initialized
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private List<LatLng> latLngs;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOC_PERMISSION_ACCESS_CODE = 1111;
    private static final float DEFAULT_ZOOM = 18f;
    private static final float CLOSING_DISTANCE = 18;
    private static final String API_KEY = "AIzaSyDFNqx30Y2u4gowURkqy56GPOH_e7zdGD4";


    // Start the function after click the button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final Button btn = findViewById(R.id.overlay_btn);
        AttachStartListener(btn);

    }


    private void AttachStartListener(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                getLocationPermission();
            }
        });
    }

    // The permission of user location
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions..");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOC_PERMISSION_ACCESS_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOC_PERMISSION_ACCESS_CODE);
        }
    }

    //initialize the map
    private void initMap() {
        Log.d(TAG, "initMap: initializing map..");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }


    // request the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: executing...");
        mLocationPermissionGranted = false;
        if (requestCode == LOC_PERMISSION_ACCESS_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                initMap();
            }
        }
    }


    // to get the location of phone
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Location Found");
                            Location currentLocation = (Location) task.getResult();
                            double currentLat = currentLocation.getLatitude();
                            double currentLong = currentLocation.getLongitude();

                            moveCamera(new LatLng(currentLat, currentLong), DEFAULT_ZOOM);
                            initLocationUpdate(currentLat, currentLong);

                            latLngs = new ArrayList<LatLng>();
                            latLngs.add(
                                    new LatLng(currentLat, currentLong)
                            );

                        } else {
                            Log.d(TAG, "onComplete: current location NULL");
                            Toast.makeText(MapsActivity.this, "Unable to get current location of device", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }


    // check if permission is passed, open the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map ready");
        Toast.makeText(this, "Map is Open", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }
    }

    // focus camera
    private void moveCamera(LatLng latlng, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to lat:" + latlng.latitude + ", lng:" + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }


    // update the location using the url data of road
    private void initLocationUpdate(double old_latitude, double old_longitude) {
        final double oLat = old_latitude;
        final double oLong = old_longitude;

        locationListener = new LocationListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onLocationChanged(Location location) {
                double changedLatitude = location.getLatitude();
                double changedlongitude = location.getLongitude();

                Log.d(TAG, "onLocationChanged: " + "New Latitude: " + changedLatitude + "New Longitude: " + changedlongitude);
                String roadAPIURL = String.format("https://roads.googleapis.com/v1/snapToRoads?path=%17f,%17f|%17f,%17f&interpolate=true&key=%s",
                        oLat,
                        oLong,
                        changedLatitude,
                        changedlongitude,
                        API_KEY);
                LocationChangeAsyncTask asyncUpdate = new LocationChangeAsyncTask();
                asyncUpdate.execute(roadAPIURL, location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //Not used
            }

            @Override
            public void onProviderEnabled(String s) {
                //Not used
            }

            @Override
            public void onProviderDisabled(String s) {
                //Not used
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1900,
                9,
                locationListener);

    }

    public class LocationChangeAsyncTask extends AsyncTask<Object, Void, JSONObject>{
        boolean isRoadsAPIAvail = true;
        double changedLatitude = 0;
        double changedlongitude = 0;

        @Override
        protected JSONObject doInBackground(Object... objects) {
            changedLatitude = ((Location)objects[1]).getLatitude();
            changedlongitude = ((Location)objects[1]).getLongitude();
            return JSONParser.getJSONFromUrl((String)objects[0]);
        }


        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            try{
                if(jsonObj == null || jsonObj.has("error")){
                    isRoadsAPIAvail = false;
                }
                else{
                    Log.d(TAG, "onPostExecute: isRoadsAPIAvail==true");
                    drawPolylinewithRoadAPI(jsonObj);
                }
            }catch (JSONException e){
                Log.e(TAG, "onPostExecute: JsonException" + e.getMessage() );
            }

            if(!isRoadsAPIAvail){
                Log.d(TAG, "onPostExecute: isRoadsAPIAvail==false");
               // Toast.makeText(MapsActivity.this, "RoadsAPI not available", Toast.LENGTH_SHORT);
                drawGreatCirclePolyline(changedLatitude, changedlongitude);
            }

            Log.d(TAG, "onPostExecute: latLngs (End)::" + latLngs);
            if(isDeviceBackToFirstLocation(latLngs)){
                drawPolygon(latLngs);
                addDistanceMarker(latLngs);
                displayRestartButton();
            };
        }
    }

    private boolean isDeviceBackToFirstLocation(List<LatLng> latLngs) {
        return latLngs.size()> 2 && distanceBetweenFirstAndLastPoint(latLngs)<= CLOSING_DISTANCE;
    }

    private void drawPolylinewithRoadAPI(JSONObject jsonObj) throws JSONException {
        JSONArray snappedPointsArray = jsonObj.getJSONArray("snappedPoints");
        Log.d(TAG, "onPostExecute: "+snappedPointsArray.toString());
        for(int i =0 ; i < snappedPointsArray.length(); i++){
            JSONObject location = snappedPointsArray.getJSONObject(i).getJSONObject("location");
            if(latLngs.size() >0 ){
                double snappedLat = location.getDouble("latitude");
                double snappedLong = location.getDouble("longitude");

                Log.d(TAG, "onPostExecute: added Latlngs ArrayList::" + latLngs);
                Polyline newLine = mMap.addPolyline(
                        new PolylineOptions()
                                .add( latLngs.get(latLngs.size()-1),
                                        new LatLng(snappedLat, snappedLong))
                                .width(10)
                                .color(Color.BLUE)
                );
                latLngs.add(
                        new LatLng(snappedLat,snappedLong)
                );
                newLine.setClickable(true);
            }
        }
    }

    //draw the polyline of the path
    private void drawGreatCirclePolyline(double changedLatitude, double changedlongitude) {
        Polyline newLine = mMap.addPolyline(
                new PolylineOptions()
                        .add( latLngs.get(latLngs.size()-1),
                                new LatLng(changedLatitude, changedlongitude))
                        .width(10)
                        .color(Color.BLUE)
        );
        newLine.setClickable(true);
        latLngs.add(
                new LatLng(changedLatitude,changedlongitude)
        );
    }


    private void drawPolygon(List<LatLng> latLngs) {
        mMap.clear();
        Polygon polygon = mMap.addPolygon(new PolygonOptions().addAll(latLngs));
        polygon.setStrokeColor(Color.RED);
        polygon.setFillColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
        polygon.setStrokeWidth(10);
        polygon.setGeodesic(true);
    }


    private float distanceBetweenFirstAndLastPoint(List<LatLng> latlngs){
        float[] results = new float[1];
        Location.distanceBetween(
                latlngs.get(0).latitude, latlngs.get(0).longitude,
                latlngs.get(latlngs.size()-1).latitude, latlngs.get(latlngs.size()-1).longitude,
                results);
        return results[0];
    }


    // show the distance of path
    private float totalDistanceInPolygon(List<LatLng> latLngs){
        float total = 0;
        for(int i=1; i< latLngs.size(); i++){
            float[] results = new float[1];
            Location.distanceBetween(
                    latLngs.get(i).latitude, latLngs.get(i).longitude,
                    latLngs.get(i-1).latitude, latLngs.get(i-1).longitude,
                    results
            );
            total+=results[0];
        }
        return total;
    }


    private void addDistanceMarker(List<LatLng> latLngs) {
        float totalDistance = totalDistanceInPolygon(latLngs);
        mMap.addMarker(new MarkerOptions()
                .position(latLngs.get(latLngs.size()-1))
                .title(String.format("Total Distance: %f m",totalDistance))
                .visible(true)).showInfoWindow();
    }


    // give the option to user to restart the track
    private void displayRestartButton() {
        Button restartBtn  = findViewById(R.id.overlay_btn);
        restartBtn.setText("Restart");
        restartBtn.setVisibility(View.VISIBLE);
        Log.d(TAG, "displayRestartButton: Make button visible");

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                view.setVisibility(View.GONE);
                latLngs.clear();
                Log.d(TAG, "displayRestartButton:onClick:: reset markers and List ");
                getDeviceLocation();
            }
        });
    }

}