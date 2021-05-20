package com.caryatri.caryatri;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDataSource;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWay;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayRepository;
import com.caryatri.caryatri.Database.Local.SNRoomDatabase;
import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.Database.Notification.NotificationDataSource;
import com.caryatri.caryatri.Database.Notification.NotificationRepository;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.Database.OneWay.CabOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.CabOneWayRepository;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWay;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayRepository;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.LocalDatabaseOfUser;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtUserEmail, edtPassword;
    ImageView logo;
    Button btnLogin;
    TextView txtCreateAccount, txtForgetPassword;
    ICarYatri mService;
    RelativeLayout rootLayout;
    ConnectivityReceiver connectivityReceiver;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Common.setBack(this);

        Paper.init(this);

        mService = Common.getAPI();

        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtPassword = findViewById(R.id.edtPassword);
        logo = findViewById(R.id.logo);
        rootLayout = findViewById(R.id.root_layout);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);

        btnLogin.setOnClickListener(view -> {
            btnLogin.setEnabled(false);
            if (edtUserEmail.getText().toString().trim().isEmpty()
                    || !edtUserEmail.getText().toString().contains("@")) {
                edtUserEmail.setError("check your registered Email");
                btnLogin.setEnabled(false);
                edtUserEmail.requestFocus();
                return;
            }
            if (edtPassword.getText().toString().trim().isEmpty()
                    || edtPassword.getText().toString().trim().replaceAll(" ", "").isEmpty() ||
                    edtPassword.getText().toString().trim().length() < 6) {
                edtPassword.setError("check your Password");
                btnLogin.setEnabled(false);
                edtPassword.requestFocus();
                return;
            }
            if (!edtUserEmail.getText().toString().isEmpty() && !edtPassword.getText().toString().isEmpty())
                LoginUser();
        });

        txtCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(logo, "imageTransition");
            ActivityOptions options;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            } else
                startActivity(intent);
        });

        txtForgetPassword.setOnClickListener(view -> {

            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(logo, "imageTransition");
            ActivityOptions options;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            } else
                startActivity(intent);
        });


    }

    private void LoginUser() {
        mService.getUserInfo(edtUserEmail.getText().toString(), edtPassword.getText().toString())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        Common.currentUser = response.body();
                        if (Common.currentUser != null) {
                            if (Common.currentUser.getError_msg() == null) {
                                Paper.book().write("email", edtUserEmail.getText().toString());
                                Paper.book().write("password", edtPassword.getText().toString());
                                btnLogin.setEnabled(true);
                                start_activity();
                            } else {
                                btnLogin.setEnabled(true);
                                Snackbar.make(rootLayout, "" + Common.currentUser.getError_msg(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            btnLogin.setEnabled(true);

                            Snackbar.make(rootLayout, "Error while get data try again",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d("ERROR", t.getMessage());
                        Toast.makeText(LoginActivity.this, "try Again", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void start_activity() {
        initDB();
        mService.getLocalDatabase(Common.currentUser.getPhone()).enqueue(new Callback<LocalDatabaseOfUser>() {
            @Override
            public void onResponse(@NonNull Call<LocalDatabaseOfUser> call, @NonNull Response<LocalDatabaseOfUser> response) {
                if (response.body() != null) {
                    if (check == 0) {
                        check = 1;
                        List<NotificationDB> notificationDBList = new Gson().fromJson(response.body().getNotificationDB(), new TypeToken<List<NotificationDB>>() {
                        }.getType());
                        if (notificationDBList != null)
                            for (int i = 0; i < notificationDBList.size(); i++)
                                Common.notificationRepository.insertToNotification(notificationDBList.get(i));

                        List<CabRoundWay> cabRoundWays = new Gson().fromJson(response.body().getCabRoundWay(), new TypeToken<List<CabRoundWay>>() {
                        }.getType());
                        if (cabRoundWays != null)
                            for (int i = 0; i < cabRoundWays.size(); i++)
                                Common.cabRoundWayRepository.insertToCabRoundWay(cabRoundWays.get(i));

                        List<CabOneWay> cabOneWays = new Gson().fromJson(response.body().getCabOneWay(), new TypeToken<List<CabOneWay>>() {
                        }.getType());
                        if (cabOneWays != null)
                            for (int i = 0; i < cabOneWays.size(); i++)
                                Common.cabOneWayRepository.insertToCabOneWay(cabOneWays.get(i));

                        List<UserDataOneWay> userDataOneWays = new Gson().fromJson(response.body().getUserDataOneWay(), new TypeToken<List<UserDataOneWay>>() {
                        }.getType());
                        if (userDataOneWays != null)
                            for (int i = 0; i < userDataOneWays.size(); i++)
                                Common.userDataOneWayRepository.insertToUserDataOneWay(userDataOneWays.get(i));

                        List<UserDataRoundWay> userDataRoundWays = new Gson().fromJson(response.body().getUserDataRoundWay(), new TypeToken<List<UserDataRoundWay>>() {
                        }.getType());
                        if (userDataRoundWays != null)
                            for (int i = 0; i < userDataRoundWays.size(); i++)
                                Common.userDataRoundWayRepository.insertToUserDataRoundWay(userDataRoundWays.get(i));
                        if (response.body().getDriverPhone() != null && !response.body().getDriverPhone().equalsIgnoreCase("")) {
                            List<CurrentDriverDB> currentDriverDBS = new Gson().fromJson(response.body().getMapStatus(),
                                    new TypeToken<List<CurrentDriverDB>>() {
                                    }.getType());
                            for (int i = 0; i < currentDriverDBS.size(); i++)
                                Common.currentDriverRepository.insertToCurrentDriver(currentDriverDBS.get(i));

                        }
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        finish();
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    finish();
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<LocalDatabaseOfUser> call, @NonNull Throwable t) {
                Log.d("ERROR", t.getMessage());
                Toast.makeText(LoginActivity.this, "try again to login", Toast.LENGTH_SHORT).show();

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

    private void initDB() {
        Common.snRoomDatabase = SNRoomDatabase.getInstance(LoginActivity.this);
        Common.cabOneWayRepository = CabOneWayRepository.getInstance(CabOneWayDataSource.getInstance(Common.snRoomDatabase.cabOneWayDAO()));
        Common.cabRoundWayRepository = CabRoundWayRepository.getInstance(CabRoundWayDataSource.getInstance(Common.snRoomDatabase.cabRoundWayDAO()));
        Common.notificationRepository = NotificationRepository.getInstance(NotificationDataSource.getInstance(Common.snRoomDatabase.notificationDAO()));
        Common.userDataOneWayRepository = UserDataOneWayRepository.getInstance(UserDataOneWayDataSource.getInstance(Common.snRoomDatabase.userDataOneWayDAO()));
        Common.userDataRoundWayRepository = UserDataRoundWayRepository.getInstance(UserDataRoundWayDataSource.getInstance(Common.snRoomDatabase.userDataRoundWayDAO()));
        Common.currentDriverRepository = CurrentDriverRepository.getInstance(CurrentDriverDataSource.getInstance(Common.snRoomDatabase.currentDriverDBDAO()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

}
