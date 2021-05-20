package com.caryatri.caryatri;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDataSource;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayRepository;
import com.caryatri.caryatri.Database.Local.SNRoomDatabase;
import com.caryatri.caryatri.Database.Notification.NotificationDataSource;
import com.caryatri.caryatri.Database.Notification.NotificationRepository;
import com.caryatri.caryatri.Database.OneWay.CabOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.CabOneWayRepository;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayRepository;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.adapter.PagerViewAdapter;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        NavigationView.OnNavigationItemSelectedListener {

    TextView one_way, round_way;
    ViewPager viewPager;
    PagerViewAdapter pagerViewAdapter;
    Toolbar toolbar;
    ConnectivityReceiver connectivityReceiver;
    ImageView notification_icon;
    NotificationBadge badge;
    CircleImageView imageView;
    ICarYatri mService = Common.getAPI();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        //init Database
        initDB();
        Paper.init(this);
        Common.setTop(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        one_way = findViewById(R.id.one);
        round_way = findViewById(R.id.round);
        viewPager = findViewById(R.id.pagerView);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onChangeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        one_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        round_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View nv = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        imageView = nv.findViewById(R.id.imageView);

        if (Common.currentUser == null) {
            String email = Paper.book().read("email");
            String password = Paper.book().read("password");

            mService.getUserInfo(email, password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Common.currentUser = response.body();
                    assert Common.currentUser != null;
                    if (Common.currentUser.getError_msg() == null) {
                        viewPager.removeAllViews();
                        Common.SearchedTextTo = null;
                        Common.SearchedTextFrom = null;
                        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(pagerViewAdapter);
                        updateToken();
                        onChangeTab(0);
                        if (Common.currentUser.getImage() != null) {
                            Picasso.get()
                                    .load(Common.currentUser.getImage())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .error(getResources().getDrawable(R.drawable.ontime))
                                    .into(imageView);
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });
        } else {
            viewPager.removeAllViews();
            Common.SearchedTextTo = null;
            Common.SearchedTextFrom = null;
            pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerViewAdapter);
            updateToken();
            onChangeTab(0);
            if (Common.currentUser.getImage() != null) {
                Picasso.get()
                        .load(Common.currentUser.getImage())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .error(getResources().getDrawable(R.drawable.ontime))
                        .into(imageView);
            }
        }

        compositeDisposable.add(Common.currentDriverRepository.getInvoice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(currentDriverDBS -> {
                    Trip trip = new Gson().fromJson(currentDriverDBS.get(0).CabDetails, Trip.class);
                    Common.id = trip.getId();
                    Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(new Intent(Common.OK));
                }, throwable -> Log.d("ERROR", throwable.getMessage())));

    }

    private void initDB() {
        Common.snRoomDatabase = SNRoomDatabase.getInstance(HomeActivity.this);
        Common.cabOneWayRepository = CabOneWayRepository.getInstance(CabOneWayDataSource.getInstance(Common.snRoomDatabase.cabOneWayDAO()));
        Common.cabRoundWayRepository = CabRoundWayRepository.getInstance(CabRoundWayDataSource.getInstance(Common.snRoomDatabase.cabRoundWayDAO()));
        Common.notificationRepository = NotificationRepository.getInstance(NotificationDataSource.getInstance(Common.snRoomDatabase.notificationDAO()));
        Common.userDataOneWayRepository = UserDataOneWayRepository.getInstance(UserDataOneWayDataSource.getInstance(Common.snRoomDatabase.userDataOneWayDAO()));
        Common.userDataRoundWayRepository = UserDataRoundWayRepository.getInstance(UserDataRoundWayDataSource.getInstance(Common.snRoomDatabase.userDataRoundWayDAO()));
        Common.currentDriverRepository = CurrentDriverRepository.getInstance(CurrentDriverDataSource.getInstance(Common.snRoomDatabase.currentDriverDBDAO()));
    }

    private void onChangeTab(int position) {
        if (position == 0) {
            one_way.setBackgroundResource(R.drawable.cab_tab_left_background);
            round_way.setBackgroundResource(R.drawable.cab_tab_left_background_with_shadow);

        }
        if (position == 1) {
            round_way.setBackgroundResource(R.drawable.cab_tab_right_background);
            one_way.setBackgroundResource(R.drawable.cab_tab_background_with_shadow);

        }
    }

    private void updateToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    ICarYatri mService = Common.getAPI();
                    mService.updateToken(Common.currentUser.getPhone(),
                            instanceIdResult.getToken(),
                            "0")
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Log.d("DEBUG2", response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("DEBUG1", t.getMessage());

                                }
                            });

                })
                .addOnFailureListener(e -> Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        View view = menu.findItem(R.id.action_notification).getActionView();
        badge = view.findViewById(R.id.badge);

        updateNotification();

        notification_icon = view.findViewById(R.id.img_notification);
        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            }
        });
        return true;
    }

    private void updateNotification() {

        if (badge == null) return;
        final int Noti = Common.notificationRepository.countNotificationItems("incomplete");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Noti == 0) {
                    badge.setVisibility(View.INVISIBLE);
                } else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Noti));
                }
            }
        });
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
        viewPager.removeAllViews();
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
        updateNotification();
        if (Common.currentUser != null)
            if (Common.currentUser.getImage() != null) {
                Picasso.get()
                        .load(Common.currentUser.getImage())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .error(getResources().getDrawable(R.drawable.ontime))
                        .into(imageView);
            }
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

            findViewById(R.id.btnTry).setOnClickListener(view -> recreate());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connectivityReceiver.isOrderedBroadcast())
            unregisterReceiver(connectivityReceiver);
        viewPager.removeAllViews();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_account) {
            startActivity(new Intent(HomeActivity.this, YourAccount.class));
        } else if (id == R.id.nav_trip) {
            startActivity(new Intent(HomeActivity.this, MyTripActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
