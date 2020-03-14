package com.example.atividade03_gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.logging.LogManager;

public class MainActivity extends AppCompatActivity {
    private static final int GPS_PERMISSION_REQUEST_ID = 1001;
    private Chronometer runTimeChronometer;
    private TextView distanceRunTextView;
    private EditText searchEditText;
    //private String search;
    private Button gpsPermissionButton;
    private Button onGpsButton;
    private Button offGpsButton;
    private Button beginButton;
    private Button endButton;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location distanceCalculator;
    private Location distanceCalculatorCurrent;

    private LocationModel locationModelProgress;
    private LocationModel locationModelBegin;

    private boolean chronometerIsRunning;

    Toolbar toolbar;
    FloatingActionButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        gpsPermissionButton = findViewById(R.id.gpsPermissionButton);
        onGpsButton = findViewById(R.id.onGpsButton);
        offGpsButton = findViewById(R.id.offGpsButton);
        beginButton = findViewById(R.id.beginButton);
        endButton = findViewById(R.id.endButton);
        runTimeChronometer = findViewById(R.id.runTimeChronometer);
        distanceRunTextView = findViewById(R.id.distanceRunTextView);
        searchButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        locationModelProgress = new LocationModel();
        locationModelBegin = new LocationModel();
        distanceCalculator = new Location("initialLocation");
        distanceCalculatorCurrent = new Location("crntLocation");

        configGPS();
        configGpsPermissionButton();
        configOnGpsButton();
        configOffGpsButton();
        configSearchButton();
        configChronometer();
        configBeginButton();
        configEndButton();
    }

    private void turnOffGPS(){
        locationManager.removeUpdates(locationListener);
        Toast.makeText(this, getString(R.string.gpsOff), Toast.LENGTH_SHORT).show();
    }

    private void endTrip(){
        if(chronometerIsRunning){
            runTimeChronometer.stop();
            runTimeChronometer.setBase(SystemClock.elapsedRealtime());
            chronometerIsRunning = false;
        }
        distanceRunTextView.setText("0");
    }

    private void configGPS(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationModelProgress.latitude = location.getLatitude();
                locationModelProgress.longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void configGpsPermissionButton(){
        gpsPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPermission();
            }
        });
    }

    private void getUserPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_PERMISSION_REQUEST_ID
            );
        }
        Toast.makeText(this, getString(R.string.permissionGranted), Toast.LENGTH_SHORT).show();
    }

    private void configOnGpsButton(){
        onGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnGPS();
            }
        });
    }

    private void turnOnGPS(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
            Toast.makeText(this, getString(R.string.gpsOn), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.permissionRequest), Toast.LENGTH_SHORT).show();
        }
    }

    private void configBeginButton(){
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip();
            }
        });
    }

    private void startTrip(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    distanceCalculator.setLatitude(location.getLatitude());
                    distanceCalculator.setLongitude(location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
            if(!chronometerIsRunning){
                runTimeChronometer.setBase(SystemClock.elapsedRealtime());
                runTimeChronometer.start();
                chronometerIsRunning = true;
            }
        }
        else{
            Toast.makeText(this, R.string.mustHaveLocationOn, Toast.LENGTH_SHORT).show();
        }
    }

    private void configOffGpsButton(){
        offGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffGPS();
            }
        });
    }

    private void configSearchButton(){
        setSupportActionBar(toolbar);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(String.format(Locale.getDefault(), "geo:%f,%f?q=%s", locationModelProgress.latitude, locationModelProgress.longitude, searchEditText.getText()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    private void configChronometer(){
        runTimeChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                distanceRunTextView.setText(String.format(String.valueOf(distanceCalculator.distanceTo(distanceCalculatorCurrent)/1000)));
            }
        });
    }

    private void configEndButton(){
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTrip();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GPS_PERMISSION_REQUEST_ID){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }
                else{
                    Toast.makeText(this,getString(R.string.no_gps_no_map), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
