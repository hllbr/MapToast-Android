package com.hllbr.maptoast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager ;
    LocationListener locationListener ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
    locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }
    };
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
    }else{
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

        Location lastLocationKnow = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocationKnow != null){
            LatLng userLastLocation = new LatLng(lastLocationKnow.getAltitude(),lastLocationKnow.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLastLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
        }
    }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length > 0){
            if(requestCode ==1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setTitle("Change");
        alert.setMessage("Are you sure");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this, "marker has been successfully changed", Toast.LENGTH_SHORT).show();

                //
                mMap.clear();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                String address = "";

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        if (addressList.get(0).getThoroughfare() != null) {
                            address += addressList.get(0).getThoroughfare();
                            if (addressList.get(0).getSubThoroughfare() != null) {
                                address += addressList.get(0).getSubThoroughfare();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title(address));

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MapsActivity.this,"transaction canceled",Toast.LENGTH_SHORT).show();

                //
            }
        });
        alert.show();
    }
}