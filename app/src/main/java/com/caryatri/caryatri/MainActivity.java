package com.caryatri.caryatri;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ImageView logo;
    ICarYatri mService;
    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.setBack(this);

        mService = Common.getAPI();
        Paper.init(this);

        logo = findViewById(R.id.logo);

        final ObjectAnimator animation = ObjectAnimator.ofFloat(logo, "rotationY", 0.0f, 360f);
        animation.setDuration(3600);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();

        String email = Paper.book().read("email");
        String password = Paper.book().read("password");

        if (email != null && password != null) {
            mService.getUserInfo(email, password)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Common.currentUser = response.body();
                            assert Common.currentUser != null;
                            if (Common.currentUser.getError_msg() == null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        animation.cancel();
                                        Intent mainIntent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }, 3600);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        animation.cancel();
                                        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(logo, "imageTransition");
                                        ActivityOptions options;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                                            startActivity(mainIntent, options.toBundle());
                                        } else
                                            startActivity(mainIntent);
                                        finish();
                                    }
                                }, 3600);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animation.cancel();
                    Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(logo, "imageTransition");
                    ActivityOptions options;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                        startActivity(mainIntent, options.toBundle());
                    } else
                        startActivity(mainIntent);
                    finish();
                }
            }, 3600);
        }


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
//            Common.setTop(this);

            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }
}