package edu.wwu.csci412.whatsfordinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private boolean loadedStore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void markNearbyGroceryStore(Location location) {

        // use Google Maps Web API to search for grocery stores withing 2000 meters of the user's current location
        String findPlaceQuery = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=grocery%20store&inputtype=textquery";
        findPlaceQuery += "&fields=formatted_address,name,geometry/location";
        findPlaceQuery += "&locationbias=circle:2000@" + location.getLatitude() + "," + location.getLongitude();
        findPlaceQuery += "&key=" + getString(R.string.google_maps_key);

        new APICall(findPlaceQuery).getString(response -> {
            Log.d("Maps API Response: ", response);
            try {
                JSONObject mapResult = new JSONObject(response);
                JSONArray r = mapResult.getJSONArray("candidates");

                if (r.length() == 0) {
                    Toast.makeText(this, "no stores found nearby!", Toast.LENGTH_LONG).show();
                    return;
                }

                // parse the API results and get the name, address, and location of the nearby store
                JSONObject store = (JSONObject) r.get(0);
                JSONObject storeGeo = store.getJSONObject("geometry");
                JSONObject storeLoc =  storeGeo.getJSONObject("location");
                LatLng storeCords = new LatLng(storeLoc.getDouble("lat"), storeLoc.getDouble("lng"));
                String storeName = store.getString("name");
                String storeAddress = store.getString("formatted_address");

                // add the store onto the map, zoom into it, and make an alert toast
                mMap.addMarker(new MarkerOptions().position(storeCords).title(storeName + " " + storeAddress));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storeCords, 12.0f));
                Toast.makeText(this, storeName+" found nearby!", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.e("Maps API Call", "JSON Exception");
            }
        });
    }

    private void initMap() {
        @SuppressLint("MissingPermission") Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // if the user's GPS location is recent then use it, but if not then use the GPS to get the current location
        if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            markNearbyGroceryStore(location);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // once the GPS location is acquired then start the store search
                    // only load once
                    if (!loadedStore) {
                        markNearbyGroceryStore(location);
                        loadedStore = true;
                    }

                }
            });
        }
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

        // if location permission has not been granted yet, request
        // it then handle the map update in the callback function
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 0);
            return;
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap.setMyLocationEnabled(true);

        initMap();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // once if the location permission is acquired (user clicks accept button) then we can load their location
            mMap.setMyLocationEnabled(true);
            initMap();
        }
    }

}