package com.example.civiladvocacyapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.civiladvocacyapp.Offices.Offices;
import com.example.civiladvocacyapp.Offices.OfficesAdaptor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private final List<Offices> officelist = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView;
    OfficesAdaptor oadaptor;

    TextView loc;

    String location = "";
    private static final int LOCATION_REQUEST = 123;
    private static String locationString = "Unspecified Location";
    private int ct = 0;
    private boolean isNW = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_recycler);
        loc = findViewById(R.id.main_location);
        oadaptor = new OfficesAdaptor(officelist, this);
        recyclerView.setAdapter(oadaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Know Your Government");

        NWConn();
        if (ct == 1) {
            return;
        }

        if (savedInstanceState != null) {
            location = savedInstanceState.getString("LOCATIONORIENTATIONCHANGE");
            doDownload();
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            determineLocation();
            doDownload();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("LOCATIONORIENTATIONCHANGE", location);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doDownload() {
        OfficesLoader officesLoader = new OfficesLoader(this);
        officesLoader.setLoc(location);
        officesLoader.getdata(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_location) {
            NWConn();
            if (ct == 1) {
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText et = new EditText(this);
            OfficesLoader ol = new OfficesLoader(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                String txt = et.getText().toString();
                getPlace(txt);
            });
            builder.setNegativeButton("CANCEL", null);
            builder.setTitle("Enter Address");

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else {
            showtoast("Not a valid Option");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Offices o = officelist.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("LIST", o);
        intent.putExtra("LOCATION", location);
        startActivity(intent);
    }

    public void updateData(ArrayList<Offices> cList) {
        ArrayList<Offices> o = cList;
        location = cList.get(0).getLoc();
        loc.setText(location);
        officelist.clear();
        officelist.addAll(cList);
        oadaptor.notifyDataSetChanged();
    }

    @SuppressLint("MissingPermission")
    private void determineLocation() {
        if (checkAppPermissions()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            getPlace(location);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    loc.setText("Location Permission denied!!");
                    showtoast("Find civic data by manually setting location");
                }
            }
        }
    }

    public void getPlace(String addr) {           //get location info
        locHelper(null, null, addr);
    }

    private void getPlace(Location loc) {           //get location info
        locHelper(loc.getLatitude(), loc.getLongitude(), null);
    }

    private void locHelper(Double lat, Double lon, String add) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        OfficesLoader ol = new OfficesLoader(this);
        List<Address> address;

        try {
            if (lat == null && lon == null) {
                address = geocoder.getFromLocationName(add, 4);
            } else {
                address = geocoder.getFromLocation(lat, lon, 1);
            }

            String st = "";
            String plce = "";
            String state = "";
            String pcode = "";

            if (address == null || address.isEmpty()) {
                showtoast("Cannot find Address. Please enter valid Address");
                //loc.setText("NO ADDRESS FOUND");
                return;
            }
            String country = address.get(0).getCountryCode();
            if (country.equals("US")) {                //check If location is in USA
                st = address.get(0).getAddressLine(0);
//                plce = address.get(0).getLocality() == null ? "" :address.get(0).getLocality()+",";
//                state = address.get(0).getAdminArea() == null ? "" :address.get(0).getAdminArea();
//                pcode = address.get(0).getPostalCode() == null ? "" :address.get(0).getPostalCode();
//                location = plce +" "+ state + " " + pcode;
                location = st.substring(0, st.length() - 5);
                // loc.setText(location);
                ol.setLoc(st);
                ol.getdata(MainActivity.this);
            } else {              //if location is outside USA
                showtoast("Current location is out of USA");
                loc.setText("INVALID ADDRESS");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void NWConn() {               //check internet connection;
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean b = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (!b) {
            ct = 1;
            isNW = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
            builder.setTitle("No Network Connection");
            loc.setText("No Data For Location");
            AlertDialog dialog = builder.create();
            dialog.show();
            officelist.clear();
            oadaptor.notifyDataSetChanged();
        } else if (!isNW) {
            ct = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                doDownload();
            }
        }
    }

    void showtoast(String message) {            //show toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}