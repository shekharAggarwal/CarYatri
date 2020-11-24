package com.caryatri.caryatri;

import android.content.Intent;
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
import android.widget.Toast;

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

public class ChangePasswordActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ImageView logo;
    EditText edtPassword, edtRePassword;
    Button btnChange;
    ICarYatri mService;
    RelativeLayout rootLayout;
    ConnectivityReceiver connectivityReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Common.setBack(this);
        rootLayout = findViewById(R.id.root_layout);

        logo = findViewById(R.id.logo);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnChange = findViewById(R.id.btnChange);
        mService = Common.getAPI();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError("Enter Your Password");
                    edtPassword.requestFocus();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    edtPassword.setError("Password length should be more then 6");
                    edtPassword.requestFocus();
                    return;
                }
                if (edtRePassword.getText().toString().isEmpty() || !edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
                    edtRePassword.setError("Re-Type your password");
                    edtRePassword.requestFocus();
                    return;
                }

                mService.updatePassword(edtPassword.getText().toString(), Common.AuthPhone)
                        .enqueue(new Callback<CheckUserResponse>() {
                            @Override
                            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                CheckUserResponse checkUserResponse = response.body();
                                if (checkUserResponse.getError_msg().equals("ok")) {
                                    Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
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

    }

    @Override
    public void onBackPressed() {
        if (Common.fromActivity != null) {
            if (Common.fromActivity.equals("update")) {
                super.onBackPressed();
            } else {
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
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
}
