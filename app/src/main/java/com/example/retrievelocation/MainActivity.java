package com.example.retrievelocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //For position
    private static final int REQUEST_CODE = 1000;
    TextView txt_location;
    Button btn_start,btn_stop;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    //For direction
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    TextView tvHeading;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode)
        {
            case REQUEST_CODE:
            {
                if(grantResults.length>0)
                {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {

                    }
                    else if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                    {

                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init View
        txt_location = (TextView) findViewById(R.id.txt_location);
        btn_start = (Button) findViewById(R.id.btn_start_updates);
        btn_stop =  (Button) findViewById(R.id.btn_stop_updates);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
        else
        {
            //if permission is granted
            buildLocationRequest();
            buildLocationCallBack();

            //Cr
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            //Set event for button
            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

                    btn_start.setEnabled(!btn_start.isEnabled());
                    btn_stop.setEnabled(!btn_stop.isEnabled());


                }
            });

            btn_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    btn_start.setEnabled(!btn_start.isEnabled());
                    btn_stop.setEnabled(!btn_stop.isEnabled());
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations())
                    txt_location.setText(String.valueOf(location.getLatitude())
                            + "/"
                            + String.valueOf(location.getLongitude()));

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }


}
