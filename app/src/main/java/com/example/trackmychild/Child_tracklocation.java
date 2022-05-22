package com.example.trackmychild;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Child_tracklocation<ValueEventListener> extends AppCompatActivity implements OnMapReadyCallback
{

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    private FirebaseAuth mAuth;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    FirebaseDatabase database;      // used for store URLs of uploaded files
    String licence_no,email,phone,name;
    DatabaseHelper databaseHelper;
    Location location;
    String Securekey;
    boolean College = false;
    int hourOfDay;


    String Childusername;
    boolean route_selected = false;

    private final String CHANNEL_ID = "simple_notification";
    private final int NOTIFICATION_ID = 01;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_tracklocation);

        database = FirebaseDatabase.getInstance();  // returns an object of firebase database

        databaseHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Current Location");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white,getTheme()));
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(getResources().getColor(R.color.darkblue2, this.getTheme()));
        // getWindow().setNavigationBarColor(getResources().getColor(R.color.yellow,this.getTheme()));



        // licence_no = getIntent().getStringExtra("licence_no");
        // phone = getIntent().getStringExtra("phone");
        // email = getIntent().getStringExtra("email");
        // name = getIntent().getStringExtra("name");

        route_dialog();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();


        if(id == R.id.share)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        if(id==R.id.about)
        {
            Intent intent = new Intent(Child_tracklocation.this,about.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void route_dialog()
    {

        Cursor res = databaseHelper.getData("1");
        if(res.moveToFirst())
        {
            name = res.getString(1);
            phone = res.getString(2);
            licence_no = res.getString(3);
            email = res.getString(4);
        }

        Childusername= res.getString(1);
        route_selected = true;
    }

    @Override
    public void onPause()
    {
        super.onPause();


        //stop location updates when Activity is no longer active
        // if (mFusedLocationClient != null)
        //  {
        //     mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        // }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //Location Permission already granted
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        else
        {
            //Request Location Permission
            checkLocationPermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //Location Permission already granted
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
        else
        {
            //Request Location Permission
            checkLocationPermission();
        }
    }

    LocationCallback mLocationCallback = new LocationCallback()
    {

        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0)
            {
                //The last location in the list is the newest
                location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null)
                {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker

                if(route_selected)
                {
                    // Toast.makeText(Child_tracklocation.this, "Location Sent : " + location.getLatitude() + "   " + location.getLongitude(), Toast.LENGTH_LONG).show();

                    final DatabaseReference reference = database.getReference();      //return the path to the root

                    reference.child(Childusername + "").child("Latitude").setValue(location.getLatitude()).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                reference.child(Childusername + "").child("Longitude").setValue(location.getLongitude());
                                reference.child(Childusername + "").child("Name").setValue(name);
                                reference.child(Childusername + "").child("Phone").setValue(phone);
                                reference.child(Childusername + "").child("Licence_no").setValue(licence_no);
                                reference.child(Childusername + "").child("Email").setValue(email);
                            }
                        }

                    });
                }



                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));



                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));


                double lat2 = location.getLatitude();
                double lng2 = location.getLongitude();
                //location of bvit
                double lat1 = 19.026665700171616;
                double lng1 = 73.05507713101898;

                SimpleDateFormat format = new SimpleDateFormat("HH", Locale.US);
                String hour;
                hour = format.format(new Date());
                Calendar calendar = Calendar.getInstance();
                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);





                if(distance(lat1, lng1, lat2, lng2)< 0.1 && hourOfDay >=9 && hourOfDay<=16){

                    College = true;
                    Toast.makeText(Child_tracklocation.this, "AT Bvit", Toast.LENGTH_SHORT).show();

                }

            }

        }

        /** calculates the distance between two locations in MILES */
        private double distance(double lat1, double lng1, double lat2, double lng2) {

            double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);

            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);

            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            double dist = earthRadius * c;

            return dist; // output distance, in MILES
        }


    };

    public void sendSMS(){
        if(College){

            SmsManager smsManager= SmsManager.getDefault();

            Date currentTime = Calendar.getInstance().getTime();
            String msg = "Your Child is reached College, on " + currentTime + " Check app for live location !" + "\n -Team Track My Child" ;
            smsManager.sendTextMessage(phone,null,msg,null,null);
            Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
        }
        else if(hourOfDay >=9 && hourOfDay<=16) {

            SmsManager smsManager= SmsManager.getDefault();

            Date currentTime = Calendar.getInstance().getTime();
            String msg = "Your Child is not in College Contact your child   " + currentTime + " Check app for live location !" + "\n -Team Track My Child" ;
            smsManager.sendTextMessage(phone,null,msg,null,null);
            // Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
        }
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Child_tracklocation.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





}