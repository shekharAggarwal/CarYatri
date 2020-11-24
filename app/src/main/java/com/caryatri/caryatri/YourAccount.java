package com.caryatri.caryatri;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWay;
import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWay;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourAccount extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView txtName, txtPhone, txtEmail;
    ImageView ic_profile, imgShare, changePassword, cancelCab;
    Toolbar toolbar;
    ICarYatri mService = Common.getAPI();
    LinearLayout logout;
    ConnectivityReceiver connectivityReceiver;
    List<String> data = new ArrayList<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int check = 0, one = 0, two = 0, cabone = 0, cabtwo = 0, notifi = 0;

    String DriverPhone = "",
            MapStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        ic_profile = findViewById(R.id.ic_profile);
        logout = findViewById(R.id.logout);
        imgShare = findViewById(R.id.imgShare);
        changePassword = findViewById(R.id.changePassword);
        cancelCab = findViewById(R.id.cancelCab);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Paper.init(this);

        if (Common.currentUser != null) {
            txtName.setText(Common.currentUser.getUsername());
            txtPhone.setText(Common.currentUser.getPhone());
            txtEmail.setText(Common.currentUser.getEmail());
        }
        ic_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, ProfileActivity.class));
            }
        });

        cancelCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourAccount.this, CancelCabActivity.class));
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLink();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.fromActivity = "update";
                Common.AuthPhone = Common.currentUser.getPhone();
                startActivity(new Intent(YourAccount.this, ChangePasswordActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutData();
            }
        });
    }

    private void logoutData() {
        data = new ArrayList<>();
        getData_cabOneWayRepository();
    }

    private void getData_cabOneWayRepository() {
        if (cabone == 0) {
            cabone = 1;
            compositeDisposable.add(Common.cabOneWayRepository.getCabOneWayItem()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<CabOneWay>>() {
                        @Override
                        public void accept(final List<CabOneWay> cabOneWays) throws Exception {
                            //1st
                            data.add(new Gson().toJson(cabOneWays));
                            Log.d("ERROR", new Gson().toJson(data.get(0)));
                            getData_cabRoundWayRepository();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            //1st
                            data.add("");
                            getData_cabRoundWayRepository();
                        }
                    }));
        }

    }

    private void getData_cabRoundWayRepository() {
        if (cabtwo == 0) {
            cabtwo = 1;
            compositeDisposable.add(Common.cabRoundWayRepository.getCabRoundWayItem()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<CabRoundWay>>() {
                        @Override
                        public void accept(List<CabRoundWay> cabRoundWays) throws Exception {
                            //2nd
                            data.add(new Gson().toJson(cabRoundWays));
                            Log.d("ERROR", new Gson().toJson(data.get(1)));
                            getData_notificationRepository();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            //2nd
                            data.add("");
                            getData_notificationRepository();
                        }
                    }));
        }
    }

    private void getData_notificationRepository() {
        if (notifi == 0) {
            notifi = 1;
            compositeDisposable.add(Common.notificationRepository.getNotificationDB()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<NotificationDB>>() {
                        @Override
                        public void accept(List<NotificationDB> notificationDBS) throws Exception {
                            //3rd
                            data.add(new Gson().toJson(notificationDBS));
                            Log.d("ERROR", new Gson().toJson(data.get(2)));
                            getData_userDataOneWayRepository();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            //3nd
                            data.add("");
                            getData_userDataOneWayRepository();
                        }
                    }));
        }
    }

    private void getData_userDataOneWayRepository() {
        if (one == 0) {
            one = 1;
            compositeDisposable.add(Common.userDataOneWayRepository.getUserDataOneWayItem()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<UserDataOneWay>>() {
                        @Override
                        public void accept(List<UserDataOneWay> userDataOneWays) throws Exception {
                            //4th
                            data.add(new Gson().toJson(userDataOneWays));
                            Log.d("ERROR", new Gson().toJson(data.get(3)));
                            getData_userDataRoundWayRepository();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                            //4th
                            data.add("");
                            getData_userDataRoundWayRepository();
                        }
                    }));
        }
    }

    private void getData_userDataRoundWayRepository() {
        final int[] c = {0};
        if (two == 0) {
            two = 1;
            compositeDisposable.add(Common.userDataRoundWayRepository.getUserDataRoundWayItem()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<UserDataRoundWay>>() {
                        @Override
                        public void accept(List<UserDataRoundWay> userDataRoundWays) throws Exception {
                            //5th
                            c[0] = 1;
                            data.add(new Gson().toJson(userDataRoundWays));
                            Log.d("ERROR1", new Gson().toJson(data.get(4)));
                            sendData();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (c[0] != 1) {
                                Log.d("ERROR2", "" + throwable.getMessage());
                                //5th
                                data.add("");
                                sendData();
                            }
                        }
                    }));
        }
    }

    private void sendData() {
        if (check == 0) {
            compositeDisposable.add(Common.currentDriverRepository.getCurrentDriverDB()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<CurrentDriverDB>>() {
                        @Override
                        public void accept(List<CurrentDriverDB> currentDriverDBS) throws Exception {
                            DriverPhone = new Gson().toJson(currentDriverDBS);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ERROR", throwable.getMessage());
                        }
                    }));
            mService.insertLocalDatabase(Common.currentUser.getPhone(), data.get(3), data.get(0), data.get(4),
                    data.get(1), data.get(2), DriverPhone, MapStatus)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            assert response.body() != null;
                            if (response.body().equals("ok")) {
                                deleteData();
                            } else {
                                Log.d("ERROR", response.body());
                                Toast.makeText(YourAccount.this, "Try again to logout", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
        }
    }

    private void deleteData() {
        Common.cabRoundWayRepository.emptyCabRoundWay();
        Common.cabOneWayRepository.emptyCabOneWay();
        Common.userDataOneWayRepository.emptyUserDataOneWay();
        Common.userDataRoundWayRepository.emptyUserDataRoundWay();
        Common.notificationRepository.emptyNotification();
        Common.currentDriverRepository.emptyCurrentDriver();
        Paper.book().delete("email");
        Paper.book().delete("password");
        Intent intent = new Intent(YourAccount.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        if (Common.currentUser != null) {
            txtName.setText(Common.currentUser.getUsername());
            txtPhone.setText(Common.currentUser.getPhone());
            txtEmail.setText(Common.currentUser.getEmail());
        }
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

            findViewById(R.id.btnTry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }

    void shareLink() {
        FirebaseDatabase.getInstance().getReference("Share").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Link = dataSnapshot.getValue(String.class);
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CarYatri");
                    String shareMessage = "\nLet me recommend you this application\n\n";
//                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + Link);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

    }
}
