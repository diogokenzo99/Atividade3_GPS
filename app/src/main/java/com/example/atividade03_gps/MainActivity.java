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

    private TextView textView;
    private TextView textView2;



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
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);


        configGPS();
//        configSearchEditText();
        configGpsPermissionButton();
        configOnGpsButton();
        configOffGpsButton();
        configSearchButton();
        configChronometer();
        configBeginButton();
        configEndButton();
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

    private void turnOffGPS(){
        locationManager.removeUpdates(locationListener);
        Toast.makeText(this, getString(R.string.gpsOff), Toast.LENGTH_SHORT).show();
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

    private void startTrip(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationModelBegin.latitude = location.getLatitude();
                    locationModelBegin.longitude = location.getLongitude();
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
        }

        textView.setText(String.valueOf(locationModelBegin.latitude));
        textView2.setText(String.valueOf(locationModelBegin.latitude));

        distanceCalculator.setLatitude(locationModelBegin.latitude);
        distanceCalculator.setLongitude(locationModelBegin.longitude);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationModelProgress.latitude = location.getLatitude();
                    locationModelProgress.longitude = location.getLongitude();
                    distanceCalculatorCurrent.setLatitude(locationModelProgress.latitude);
                    distanceCalculatorCurrent.setLongitude(locationModelProgress.longitude);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }, null);
            Toast.makeText(this, getString(R.string.startTrip), Toast.LENGTH_SHORT).show();

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

    private void endTrip(){
        if(chronometerIsRunning){
            runTimeChronometer.setBase(SystemClock.elapsedRealtime());
        }
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

//    private void configSearchEditText(){
//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                search = (String)charSequence;
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }

    private void configGpsPermissionButton(){
        gpsPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPermission();
            }
        });
    }

    private void configOnGpsButton(){
        onGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnGPS();
            }
        });
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
                Uri uri = Uri.parse(String.format(Locale.getDefault(), "geo:%f,%f?q" + searchEditText.getText(), locationModelProgress.latitude, locationModelProgress.longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
            }
        });
    }

    private void configChronometer(){
        runTimeChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                distanceRunTextView.setText(String.format(String.valueOf(distanceCalculator.distanceTo(distanceCalculatorCurrent))));
            }
        });
    }

    private void configBeginButton(){


        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip();
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
    protected void onStop() {
        super.onStop();
        turnOffGPS();
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
