package com.caryatri.caryatri;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.CheckUserResponse;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtPhoneNumber;
    Button btnForget;
    TextView txtBackLogin;
    public static ImageView logo;
    ICarYatri mService;
    RelativeLayout rootLayout;
    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Common.setBack(this);
        Common.StartFirebaseLogin(ForgotActivity.this);
        mService = Common.getAPI();
        rootLayout = findViewById(R.id.root_layout);

        logo = findViewById(R.id.logo);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnForget = findViewById(R.id.btnForget);
        txtBackLogin = findViewById(R.id.txtBackLogin);

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.checkUser(edtPhoneNumber.getText().toString())
                        .enqueue(new Callback<CheckUserResponse>() {
                            @Override
                            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                CheckUserResponse checkUserResponse = response.body();
                                if (checkUserResponse.getError_msg().equals("ok")) {
                                    Common.fromActivity = "fog";
                                    Common.AuthPhone = edtPhoneNumber.getText().toString();
                                    Common.sendOTP(ForgotActivity.this, edtPhoneNumber.getText().toString());
                                } else {
                                    Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                            }
                        });
            }
        });

        txtBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
