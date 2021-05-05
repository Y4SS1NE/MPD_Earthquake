// Yassine Lakhmarti S1903349
package org.me.gcu.equakestartercode;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

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

public class EarthquakeActivity extends AppCompatActivity implements OnMapReadyCallback {
    Item item;
    TextView textViewDate, textViewLocation, textViewMagnitude, textViewDepth, textViewLatLong;
    /*
         onCreate() method performs the basic application start-up logic, we first link the activity to the correct view
         in this case, it's activity_earthquake
         we add a back icon in the toolbar for the user to go back
         We get the item from the adapter
         We initialize the widgets which are all the earthquake: Date & Time, Location, Mag, Depth, Lat/Long
         We set the values of these widgets with setText()
         We create the map fragment to hold the google map
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        item = getIntent().getParcelableExtra("item");

        textViewDate = findViewById(R.id.specific_date);
        textViewLocation = findViewById(R.id.text_view_location);
        textViewMagnitude = findViewById(R.id.text_view_magnitude);
        textViewDepth = findViewById(R.id.text_view_depth);
        textViewLatLong = findViewById(R.id.text_view_lat_long);

        textViewDate.setText("Date & Time: " + item.getPubDate());
        textViewLocation.setText("Location: " + item.getLocation());
        textViewMagnitude.setText("Magnitude: " + item.getMagnitude());
        textViewDepth.setText("Depth: " + item.getDepth() + " km");
        textViewLatLong.setText("Lat/long: " + item.getLatitude() + "," + item.getLongitude());

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
        LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(item.getLocation())
                        .icon(getMarkerIcon(getPinColor(item.getMagnitude()))));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
    /*
        Color code the pin according to the earthquake's magnitude
     */
    public int getPinColor(double magnitude) {
        if (magnitude >= 6.0) {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.great);
        } else if (magnitude <= 5.9 && magnitude >= 5.0) {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.red);
        } else if (magnitude <= 4.9 && magnitude >= 4.0) {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.major);
        } else if (magnitude <= 3.9 && magnitude >= 3.0) {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.orange);
        } else if (magnitude <= 2.9 && magnitude >= 2.0) {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.strong);
        } else {
            return ContextCompat.getColor(EarthquakeActivity.this, R.color.yellow);
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
