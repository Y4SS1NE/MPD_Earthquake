// Yassine Lakhmarti S1903349
package org.me.gcu.equakestartercode;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    List<Item> itemList = new ArrayList<>();
/*
     onCreate() method performs the basic application start-up logic, we first associate the activity
      with activity_map View, set the title and bind the data to itemList. We then create the map fragment

*/
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Mapped Earthquakes");
        itemList.addAll(MainActivity.itemList);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
/*
    Override the onSupportNavigateUp() method to enable the user to return to the previous page
 */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
/*
    Steps to take when the Map is ready to run, we first create a LatLng object where we store the positions
    then we add a marker with the specified coordinates with the correct color, we set the map zoom and camera animation
 */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        for (Item item : itemList) {
            LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .title(item.getLocation())
                            .icon(getMarkerIcon(getPinColor(item.getMagnitude()))));
        }

        LatLng uk = new LatLng(54.2274814, -4.8523227);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(uk, 5F));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

/*
    Color code the pin according to the earthquake's magnitude
 */
    public int getPinColor(double magnitude) {
        if (magnitude >= 6.0) {
            return ContextCompat.getColor(MapActivity.this, R.color.great);
        } else if (magnitude <= 5.9 && magnitude >= 5.0) {
            return ContextCompat.getColor(MapActivity.this, R.color.red);
        } else if (magnitude <= 4.9 && magnitude >= 4.0) {
            return ContextCompat.getColor(MapActivity.this, R.color.major);
        } else if (magnitude <= 3.9 && magnitude >= 3.0) {
            return ContextCompat.getColor(MapActivity.this, R.color.orange);
        } else if (magnitude <= 2.9 && magnitude >= 2.0) {
            return ContextCompat.getColor(MapActivity.this, R.color.strong);
        } else {
            return ContextCompat.getColor(MapActivity.this, R.color.yellow);
        }
    }

/*
    Change Marker color
 */
    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}