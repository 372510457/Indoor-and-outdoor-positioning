package com.example.findpath5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class CollectWiFi extends AppCompatActivity {


    // Check and ask for the permissions
    WifiManager wm;
    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private BroadcastReceiver wifiScanReceiver ;

    private void askWiFiPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int wifiAccessPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_WIFI_STATE);
            int wifiChangePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CHANGE_WIFI_STATE);
            int coarseLocationPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (wifiAccessPermission != PackageManager.PERMISSION_GRANTED ||
                    wifiChangePermission != PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }

        }
    }
    //The WifiScanReceiver need to be registered for the receiver to start receiving the scan results.
    // The receiver will be registered on onResume() and unregistered on onPause().
    protected void onResume() {
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        super.onPause();
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permission[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {
                // If request is cancelled, the result arrays are empty
                // Permissions granted
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                }
                // Cancelled or denied
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_wi_fi);

        Intent intent1 = getIntent();

        SQLiteStudioService.instance().start(this);
        askWiFiPermissions();

        dbHelper = new MyDatabaseHelper(this, "WiFiStore.db", null, 1);
        dbHelper.getWritableDatabase();

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wm.getWifiState() == wm.WIFI_STATE_DISABLED) {
            wm.setWifiEnabled(true);
        }
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wm.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
        final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> results = wm.getScanResults();  //Get the results of the scan
                unregisterReceiver(this);
                System.out.println(results);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                // According to the Scanning of BSSID and level of WiFi to input to the local database
                for (ScanResult result : results) {
                    if (result.BSSID.equals("bc:9f:e4:e6:ea:d2") && result.level > -1000) {  //Based on BSSID and Level
                        values.put("SSID", "central");
                        values.put("BSSID", "bc:9f:e4:e6:ea:d2");
                        db.insert("WiFi", null, values);
                        values.clear();
                    }
                    if (result.BSSID.equals("bc:9f:e4:e6:ea:d0") && result.level > -1000) {
                        values.put("SSID", "eduroam");
                        values.put("BSSID", "bc:9f:e4:e6:ea:d0");
                        db.insert("WiFi", null, values);
                        values.clear();
                    }
                    if (result.BSSID.equals("24:01:c7:f7:7f:1e") && result.level > -1000) {
                        values.put("SSID", "eduroam");
                        values.put("BSSID", "24:01:c7:f7:7f:1e");
                        db.insert("WiFi", null, values);
                        values.clear();
                    }
                    if (result.BSSID.equals("00:3a:7d:a2:97:6e") && result.level > -1000) {
                        values.put("SSID", "eduroam");
                        values.put("BSSID", "00:3a:7d:a2:97:6e");
                        db.insert("WiFi", null, values);
                        values.clear();
                    }
                    if (result.BSSID.startsWith("24:01:c7:f7:7f:11") && result.level > -1000) {
                        values.put("SSID", "eduroam");
                        values.put("BSSID", "bc:9f:e4:e6:ea:c0");
                        db.insert("WiFi", null, values);
                    }
                }
            }
        };
        Button addData = (Button) findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (wm.getWifiState() == wm.WIFI_STATE_DISABLED) {
                    wm.setWifiEnabled(true);
                }
                registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wm.startScan();

                // Prepare a BroadcastReceiver to receive Wi-Fi scan results
                // According to the Scanning of BSSID and level of WiFi to input to the local database
                BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        List<ScanResult> results = wm.getScanResults();  //Get the results of the scan
                        unregisterReceiver(this);
                        System.out.println(results);// Output to the local database
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        for (ScanResult result : results) {
                            if (result.BSSID.equals("bc:9f:e4:e6:ea:d2") && result.level > -1000) {  //Based on BSSID and Level
                                values.put("SSID", "central");
                                values.put("BSSID", "bc:9f:e4:e6:ea:d2");
                                db.insert("WiFi", null, values);
                                values.clear();
                            }
                            if (result.BSSID.equals("bc:9f:e4:e6:ea:d0") && result.level > -1000) {
                                values.put("SSID", "eduroam");
                                values.put("BSSID", "bc:9f:e4:e6:ea:d0");
                                db.insert("WiFi", null, values);
                                values.clear();
                            }
                            if (result.BSSID.equals("24:01:c7:f7:7f:1e") && result.level > -1000) {
                                values.put("SSID", "eduroam");
                                values.put("BSSID", "24:01:c7:f7:7f:1e");
                                db.insert("WiFi", null, values);
                                values.clear();
                            }
                            if (result.BSSID.equals("00:3a:7d:a2:97:6e") && result.level > -1000) {
                                values.put("SSID", "eduroam");
                                values.put("BSSID", "00:3a:7d:a2:97:6e");
                                db.insert("WiFi", null, values);
                                values.clear();
                            }
                            if (result.BSSID.startsWith("24:01:c7:f7:7f:11") && result.level > -1000) {
                                values.put("SSID", "eduroam");
                                values.put("BSSID", "bc:9f:e4:e6:ea:c0");
                                db.insert("WiFi", null, values);
                            }
                        }
                    }
                };

                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                Cursor cursor = db1.query("WiFi", null, null, null, null, null, null);

                // Generate the local database including columns of SSID, BSSID and Level
                if (cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String SSID = cursor.getString(cursor.getColumnIndex("SSID"));
                        @SuppressLint("Range") String BSSID = cursor.getString(cursor.getColumnIndex("BSSID"));
                        @SuppressLint("Range") String Level = cursor.getString(cursor.getColumnIndex("Level"));
                        Log.d("MainAvtivity", "SSID: " + SSID);
                        Log.d("MainAvtivity", "BSSID: " + BSSID);
                        Log.d("MainAvtivity", "Level: " + Level);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        });
    }
}