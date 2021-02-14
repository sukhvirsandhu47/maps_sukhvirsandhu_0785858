package com.example.maps_sukhvirsandhu_0785858;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private static final int Request_code = 1;
    private Marker homemarker;
    private Marker destMarker;
    LocationManager locationManager;
    LocationListener locationListener;
    int i=0;
    double lat ,lang;
    Polygon shape;
    LatLng usrLoc;
    public static final int POLYGON_SIDES= 4;
    List<Marker> markerList= new ArrayList<>();
    double distance;

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
        gMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                SetHomeMarker(location);
            }
        };
        if (!isGrantedPermission())
            requestLocalationPermission();
        else
            startUpdateLocation();

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setMarker(latLng);
            }
        });
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                float []d =new float[1];
                Location.distanceBetween(lat,lang, marker.getPosition().latitude,marker.getPosition().longitude,d);
                marker.setSnippet(String.valueOf(d[0])+"km");
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> list= geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
                    if (list.size() > 0) {
                        Toast.makeText(
                                MapsActivity.this,
                                list.get(0).getAddressLine(0),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        gMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                List<LatLng> lat;
                Array[] t = new Array[0];
                polyline.setColor(Color.GREEN);
                lat= polyline.getPoints();
                float []d= new float[1];
                Location.distanceBetween(lat.get(0).latitude,lat.get(0).longitude, lat.get(1).latitude,lat.get(1).latitude,d);
                Toast.makeText(
                        MapsActivity.this,
                        "Distance="+d[0],
                        Toast.LENGTH_LONG
                ).show();

            }
        });
        gMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

            }
        });
    }

    private void setMarker(LatLng latLng) {
        String[] a = {"A","B","C","D"};
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(a[i]);
        i++;
//        if(destMarker != null)
//            clearMap();
//        destMarker= gMap.addMarker(options);
//        drawLine();
        if(markerList.size()== POLYGON_SIDES)
            cleanMap();
        markerList.add(gMap.addMarker(options));
        if(markerList.size() == POLYGON_SIDES)
            drawShape();
    }


    private void drawShape() {
        PolygonOptions options =new PolygonOptions()
                .fillColor(0x3500FF00)
                .strokeColor(Color.RED)
                .strokeWidth(20)
                .clickable(true);
        for(int i=0; i<POLYGON_SIDES;i++)
            options.add(markerList.get(i).getPosition());

        shape = gMap.addPolygon(options);

    }

    private void cleanMap() {
        for(Marker marker: markerList)
            marker.remove();

        markerList.clear();
        shape.remove();
        shape =null;
    }

//    private void drawLine() {
//
//    }


    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocalationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
    }

    private boolean isGrantedPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;
    }

    private void SetHomeMarker(Location location) {
        LatLng usrLoc= new LatLng(location.getLatitude(),location.getLongitude());
        lat=location.getLatitude();
        lang=location.getLongitude();
        MarkerOptions options =new MarkerOptions().position(usrLoc)
                .title("Your Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet("your location");
        homemarker= gMap.addMarker(options);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLoc,15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Request_code ==requestCode)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
            }
        }
    }
}
