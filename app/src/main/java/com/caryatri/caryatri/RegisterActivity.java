package com.caryatri.caryatri;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
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
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.CheckUserResponse;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtUserName, edtUserEmail, edtPhoneNumber, edtPassword;
    Button btnRegister;
    public static ImageView logo;
    TextView txtBackLogin;
    ICarYatri mService;
    ConnectivityReceiver connectivityReceiver;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Common.setBack(this);
        Common.StartFirebaseLogin(RegisterActivity.this);
        mService = Common.getAPI();
        rootLayout = findViewById(R.id.root_layout);

        edtUserName = findViewById(R.id.edtUserName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtPassword = findViewById(R.id.edtPassword);
        logo = findViewById(R.id.logo);
        btnRegister = findViewById(R.id.btnRegister);
        txtBackLogin = findViewById(R.id.txtBackLogin);

        btnRegister.setOnClickListener(view -> check_data());

        txtBackLogin.setOnClickListener(view -> onBackPressed());

    }

    private void check_data() {
        if (edtUserName.getText().toString().isEmpty()) {
            edtUserName.setError("Enter Your Name");
            edtUserName.requestFocus();
            return;
        }
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

        if (edtUserEmail.getText().toString().isEmpty()) {
            edtUserEmail.setError("Enter Your Email");
            edtUserEmail.requestFocus();
            return;
        }

        if (!edtUserEmail.getText().toString().contains("@")) {
            edtUserEmail.setError("Enter Your Email");
            edtUserEmail.requestFocus();
            return;
        }
        if (edtPhoneNumber.getText().toString().isEmpty() || edtPhoneNumber.getText().toString().length() < 10) {
            edtPhoneNumber.setError("Check Phone Number");
            edtPhoneNumber.requestFocus();
            return;
        }

        mService.CheckUserExists(edtUserEmail.getText().toString(), edtPhoneNumber.getText().toString())
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CheckUserResponse> call, @NonNull Response<CheckUserResponse> response) {
                        if (response != null) {
                            CheckUserResponse checkUserResponse = response.body();
                            if (checkUserResponse.getError_msg().equals("ok")) {
                                Common.fromActivity = "reg";
                                Common.AuthPhone = edtPhoneNumber.getText().toString();
                                Common.register = new User(edtUserName.getText().toString(),
                                        edtUserEmail.getText().toString(),
                                        edtPhoneNumber.getText().toString(),
                                        edtPassword.getText().toString(), null);
                                Common.sendOTP(RegisterActivity.this, edtPhoneNumber.getText().toString());
                            } else {
                                Snackbar.make(rootLayout, "" + checkUserResponse.getError_msg(),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CheckUserResponse> call, @NonNull Throwable t) {
                        Toast.makeText(RegisterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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

            findViewById(R.id.btnTry).setOnClickListener(view -> recreate());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }
}
