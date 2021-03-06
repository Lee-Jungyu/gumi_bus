package com.jglee.busapp.ui.Activity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jglee.busapp.R;
import com.jglee.busapp.domain.RouteItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private class PoseItem {
        LatLng pose;
        String stationName;

        public PoseItem() {
        }

        public PoseItem(LatLng pose, String stationName) {
            this.pose = pose;
            this.stationName = stationName;
        }
    }

    private List<PoseItem> poseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Intent intent = getIntent();
        RouteItem routeItem = null;

        loadMarker();

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //draw marker and polyline
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i = 0; i < poseList.size(); i++) {
            if(i == 0) {
                mMap.addMarker(
                        new MarkerOptions()
                                .position(poseList.get(i).pose)
                                .title(poseList.get(i).stationName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else if(i == poseList.size() - 1) {
                mMap.addMarker(
                        new MarkerOptions()
                                .position(poseList.get(i).pose)
                                .title(poseList.get(i).stationName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
            else {
                mMap.addMarker(
                        new MarkerOptions()
                                .position(poseList.get(i).pose)
                                .title(poseList.get(i).stationName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            polylineOptions.add(poseList.get(i).pose);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);
        }


        //move camera
        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(poseList.get(0).pose));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.0f);
        mMap.animateCamera(zoom);
    }

    public void loadMarker() {
        poseList = new ArrayList<>();

        double source_lat = getIntent().getDoubleExtra("source_lat", 0.0);
        double source_lon = getIntent().getDoubleExtra("source_lon", 0.0);
        double dest_lat = getIntent().getDoubleExtra("dest_lat", 0.0);
        double dest_lon = getIntent().getDoubleExtra("dest_lon", 0.0);

        poseList.add(new PoseItem(new LatLng(source_lat, source_lon), "출발"));
        try {
            JSONObject info = new JSONObject(getIntent().getStringExtra("info"));
            JSONArray subPathList = new JSONArray(getIntent().getStringExtra("subPathList"));
            RouteItem item = new RouteItem(info, subPathList);

            for(int i = 0; i < item.getSubPathList().length(); i++) {
                JSONObject subPath = (JSONObject) item.getSubPathList().get(i);

                if(subPath.getInt("trafficType") == 2) {
                    double startX = subPath.getDouble("startX");
                    double startY = subPath.getDouble("startY");
                    String startName = subPath.getString("startName");
                    poseList.add(new PoseItem(new LatLng(startY, startX), startName));

                    double endX = subPath.getDouble("endX");
                    double endY = subPath.getDouble("endY");
                    String endName = subPath.getString("endName");
                    poseList.add(new PoseItem(new LatLng(endY, endX), endName));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        poseList.add(new PoseItem(new LatLng(dest_lat,dest_lon), "도착"));
    }

}