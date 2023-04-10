package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.capstone.foodify.API.GoogleMapDirectionApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.DirectionJSONParser;
import com.capstone.foodify.Model.GoogleMap.Location;
import com.capstone.foodify.R;
import com.capstone.foodify.databinding.ActivityTrackingOrderBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrderActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private ActivityTrackingOrderBinding binding;
    private String trackingOrderNumber, latOrder, lngOrder = null;
    private ImageView back_image;
    private FirebaseDatabase database;
    private DatabaseReference orderDatabase;
    private Marker shippingMarker;
    private Polyline polyline;
    private static final String TAG = "TrackingOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get tracking number order
        if(getIntent() != null){
            trackingOrderNumber = getIntent().getStringExtra("trackingOrderNumber");
            latOrder = getIntent().getStringExtra("latOrder");
            lngOrder = getIntent().getStringExtra("lngOrder");
        }

        if(trackingOrderNumber == null){
            Toast.makeText(this, "Chưa lấy được mã đơn hàng! Xin vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            finish();
        }
        binding = ActivityTrackingOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        orderDatabase = database.getReference("Order");
        orderDatabase.addValueEventListener(this);

        //Init component
        back_image = findViewById(R.id.back_image);

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        trackingLocation();
    }

    private void trackingLocation() {
        //Get order location
        LatLng location = new LatLng(Double.parseDouble(latOrder), Double.parseDouble(lngOrder));

        mMap.addMarker(new MarkerOptions().position(location)
                .title("Đơn hàng #" + trackingOrderNumber)
                .icon(BitmapDescriptorFactory.defaultMarker()));

        //Set shipper location
        orderDatabase.child(trackingOrderNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Location shipperLocation = snapshot.getValue(Location.class);

                LatLng latLng = new LatLng(shipperLocation.getLat(), shipperLocation.getLng());
                if(shippingMarker == null){
                    shippingMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Người giao")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shipper)));
                } else {
                    shippingMarker.setPosition(latLng);
                }

                //Update camera
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(16)
                        .bearing(0)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //Draw routes
                if(polyline != null)
                    polyline.remove();
                GoogleMapDirectionApi.apiService.getDirections(shipperLocation.getLat() + "," + shipperLocation.getLng(),
                        latOrder + "," + lngOrder, Common.MAP_API
                        ).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        assert response.body() != null;
                        new ParserTask().execute(response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(TrackingOrderActivity.this, "Có lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        trackingLocation();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            ArrayList points =  new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for(int i = 0; i < lists.size(); i++){

                List<HashMap<String, String>> path = lists.get(i);

                for(int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }
            polyline = mMap.addPolyline(lineOptions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        orderDatabase.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        orderDatabase.removeEventListener(this);
        super.onStop();
    }
}