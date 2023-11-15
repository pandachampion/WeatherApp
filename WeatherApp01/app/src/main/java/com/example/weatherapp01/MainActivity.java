package com.example.weatherapp01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    android.widget.EditText zipCodeInput;
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    Button moreLocation;
    Spinner spinner;
    EditText numText;
    Button submitButton;
    ListView headlineText;
    String state = "";
    String city = "";
    String zipCode;
    EditText cityText;
    SupportMapFragment mapFragment;
    CustomAdapter adapter;
    double longitude;
    double latitude;
    boolean locationAvailable;

    ArrayList<HashMap<String, String>> headline = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> fireZone = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> shelters = new ArrayList<HashMap<String, String>>();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationResult locationResult;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        cityText = (EditText) findViewById(R.id.cityName);
        // Display a button for looking up alerts by specific location.
        moreLocation = (Button) findViewById(R.id.moreLocation);
        moreLocation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flipVisibility(false);
                    }
                });

        // Setup a state spinner that is initially invisible
        spinner = (Spinner) findViewById(R.id.state);
        ArrayAdapter<String> adapter;
        String states[] = {"AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE",
                "FL", "GA", "HI", "IA", "ID", "IL", "KS", "KY", "LA", "MA", "MD",
                "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ",
                "NM", "NM", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN",
                "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY",};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, states);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setMinimumHeight(60);

        // Setup a text button for zipCode that is initially invisible
        numText = (EditText) findViewById(R.id.zipCode);
        submitButton = (Button) findViewById(R.id.submit);


        headlineText = (ListView) findViewById(R.id.headlineText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numText.getText().toString().length() == 0 || numText.getText().toString().length() != 5) {
                    showToast("Please enter a valid 5 digit US zipcode!");
                } else {
                    city = cityText.getText().toString().toUpperCase();
                    zipCode = numText.getText().toString();

                    if (!Utilities.validateInput(state, zipCode)) {
                        showToast("ZipCode is not valid! ALL Alerts for the state will be shown!");
                    }
                }
                flipVisibility(true);

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        callRemoteFetch(false, 0.0, 0.0, city, zipCode, state);
                    }
                });
            }
        });
        System.out.println("Starting Location Updates");
        startLocationUpdates();
    }


    // Start fetching location updates
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Create the location request to start receiving updates.

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        // Request location updates in the background
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            onLocationChanged(locationResult.getLastLocation());

                        }
                    },
                    Looper.getMainLooper());
        } catch (SecurityException e) {
            System.out.println(e);
        }
    }

    // When a change of location is detected, fetch the alerts.
    public void onLocationChanged(final Location location) {
        // New location has now been determined
        System.out.println("Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude()));
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mapFragment.getMapAsync(this);
        flipVisibility(true);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                callRemoteFetch(true, location.getLongitude(), location.getLatitude(), getStuff(location).get("cityName"),
                        getStuff(location).get("zipCode"), "");
                // Location to hard-code to check for an alert - latitude = 35.2828; longitude = -120.6596;
            }
        });
    }

    public void callRemoteFetch(boolean haveLocation, double longitude, double latitude, String cityName, String zipCode, String state) {
        headline.clear();
        shelters.clear();
        if (haveLocation) { //Checks to see if permission granted for location
            headline = RemoteFetch.getAlert(longitude, latitude); //returns headline
            if(!headline.isEmpty()){
                fireZone = RemoteFetch.getFireZone(longitude, latitude); //returns firezone
                shelters = RemoteShelter.findShelter((String) fireZone.get("City").toUpperCase(), (String) fireZone.get("State"),
                        zipCode);
                System.out.println("THIS IS THE THE SHELTER SIZE" + shelters.size());
                System.out.println("City" + (String) fireZone.get("City").toUpperCase());
            }
        } else {
            HashMap<String, String> property_dict = new HashMap<>();
            property_dict.put("State", state);
            property_dict.put("Zipcode", zipCode);
            property_dict.put("City", city);
            shelters = RemoteShelter.findShelter(cityName.toUpperCase(), state, zipCode);
            headline = RemoteFetch.getAlert(false, property_dict);
        }
        state= "";
        zipCode = "";
        city = "";

        runOnUiThread(new Thread(new Runnable() {
            public void run() {
                HashMap<String, String> final_dict = new HashMap<String, String>();
                try {
                    if (headline.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Found no Active Alerts for your area! To look up alerts for other areas use \"Lookup More Locations\".");
                        HashMap<String, String> noAlert = new HashMap<String, String>();
                        noAlert.put("Alert", "NO ALERTS FOUND FOR YOUR AREA!");
                        headline.add(0, noAlert);
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), headline, shelters);
                        System.out.println("Now display the alerts!");
                        headlineText.setAdapter(customAdapter);
                    } else {
                        String[] headlineArray = new String[headline.size() + shelters.size()];
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), headline, shelters);
                        System.out.println("Now display the alerts!");
                        headlineText.setAdapter(customAdapter);
                    }
                } catch (Exception e) {
                }
            }
        }));
    }

    public HashMap<String, String> getStuff(Location location) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        HashMap<String, String> addressDict = new HashMap<String, String>();
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (address.size() > 0) {
                String cityName = address.get(0).getLocality();
                addressDict.put("cityName", cityName);
                addressDict.put("zipCode", address.get(0).getPostalCode());
                System.out.println(address.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressDict;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            System.out.println("I am here");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission granted. Fetching alerts",
                        Toast.LENGTH_SHORT).show();
                locationAvailable = true;
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        System.out.println("User denied permission permanently");
                        handleNoPermission();
                        return;
                    }
                    System.out.println("Permission granted, yay!");
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(final Location location) {
                                    if (location != null) {
                                        onLocationChanged(location);
                                    } else {
                                        System.out.println("No location! Please make sure location is enabled on your phone. Please try again.");
                                        showToast("No location! Please make sure location is enabled on your phone. Please try again.");
                                        flipVisibility(false);
                                    }
                                }
                            });
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                System.out.println("User denied permission");
                handleNoPermission();
            }
        }
    }

    private void handleNoPermission() {
        System.out.println("Handling the case of user denied permission");
        flipVisibility(false);
    }

    private void showToast(String text) {
        android.widget.Toast.makeText(MainActivity.this, text,
                android.widget.Toast.LENGTH_LONG).show();
    }

    private void flipVisibility(boolean display_alerts) {
        if (display_alerts) {
            // Display views to show the alerts.
            headlineText.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            moreLocation.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            numText.setVisibility(View.INVISIBLE);
            cityText.setVisibility(View.INVISIBLE);
            submitButton.setVisibility(View.INVISIBLE);
        } else {
            // Display views to request user location information
            headlineText.setVisibility(View.INVISIBLE);
            mapFragment.getView().setVisibility(View.INVISIBLE);
            moreLocation.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.VISIBLE);
            numText.setVisibility(View.VISIBLE);
            cityText.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        state = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (locationAvailable) {
            LatLng location = new LatLng(latitude, longitude);
            System.out.println("THIS IS YOUR LATITUDE" + latitude + " THIS IS YOUR LONGITUDE" + longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Marker Your Location"));
        } else {
            LatLng location = new LatLng(-33.852, 151.211);
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Marker in Sydney"));
        }
        LatLngBounds ADELAIDE = new LatLngBounds(
                new LatLng(latitude, longitude), new LatLng(latitude, longitude));
        // Constrain the camera target to the Adelaide bounds.
        googleMap.setLatLngBoundsForCameraTarget(ADELAIDE);
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }
};