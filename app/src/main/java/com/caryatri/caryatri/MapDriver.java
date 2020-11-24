package com.caryatri.caryatri;

import android.animation.ValueAnimator;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.Toolbar;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapDriver extends CrashActivity implements OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener {

    ConnectivityReceiver connectivityReceiver;
    private GoogleMap mMap;
    Toolbar toolbar;
    ICarYatri mService = Common.getAPI();
    private Marker carMarker;
    private float v;
    private double lat = 0, lng = 0;
    private LatLng startPosition, endPosition;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    DatabaseReference drivers;
    GeoFire geoFire;
    String Phone = null;

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);
        Common.setTop(this);
        if (getIntent().getStringExtra("Phone") == null) {
            compositeDisposable.add(Common.currentDriverRepository.getCurrentDriverDB()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<CurrentDriverDB>>() {
                        @Override
                        public void accept(final List<CurrentDriverDB> currentDriverDBS) throws Exception {
                            Phone = currentDriverDBS.get(0).DriverPhone;
                            DisplayLocation();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                        }
                    }));
        } else if (getIntent().getStringExtra("Phone") != null) {
            Phone = getIntent().getStringExtra("Phone");
            DisplayLocation();
        }
    }

    private void DisplayLocation() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drivers = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(drivers);
        geoFire.getLocation(Phone, new com.firebase.geofire.LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                Log.d("ERRORMAP", new Gson().toJson(location));
                if (location != null) {
                    final double latitude = location.latitude;
                    final double longitude = location.longitude;
                    if (endPosition != null) {
                        if ((!String.valueOf(endPosition.latitude).equalsIgnoreCase(String.valueOf(latitude)) ||
                                !String.valueOf(endPosition.longitude).equalsIgnoreCase(String.valueOf(longitude)))) {
                            startPosition = endPosition;
                            endPosition = new LatLng(location.latitude, location.longitude);

                            if (!String.valueOf(endPosition.latitude).equalsIgnoreCase(String.valueOf(startPosition.latitude)) &&
                                    !String.valueOf(endPosition.longitude).equalsIgnoreCase(String.valueOf(startPosition.longitude))) {
                                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                valueAnimator.setDuration(2000);
                                valueAnimator.setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        v = valueAnimator.getAnimatedFraction();
                                        lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                                        lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                                        LatLng newPos = new LatLng(lat, lng);
                                        carMarker.setPosition(newPos);
                                        carMarker.setAnchor(0.5f, 0.5f);
                                        carMarker.setRotation(getBearing(startPosition, newPos));
                                        CameraPosition currentPlace = new CameraPosition.Builder()
                                                .target(newPos)
                                                .bearing(getBearing(startPosition, newPos) + 0)
                                                .zoom(17.0f)
                                                .build();
                                        mMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(currentPlace));
                                    }
                                });
                                valueAnimator.start();
                            }
                        }
                    } else {
                        if (carMarker != null)
                            carMarker.remove();
                        carMarker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                .position(new LatLng(latitude, longitude))
                                .title("Your Location"));
                        CameraPosition currentPlace = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude)).zoom(17.0f).build();
                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(currentPlace));
                        startPosition = new LatLng(location.latitude, location.longitude);
                        endPosition = new LatLng(location.latitude, location.longitude);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    protected void onPause() {
        if (mMap != null)
            mMap.clear();
        super.onPause();

    }

    @Override
    protected void onStop() {
        if (mMap != null)
            mMap.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        MyApplication.getInstance().setConnectivityListener(this);
//        // register connection status listener
//        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            setContentView(R.layout.layout_no_internet);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.
                        FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.
                        FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.
                        TRANSPARENT);
            }
            Common.setTop(this);

            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
