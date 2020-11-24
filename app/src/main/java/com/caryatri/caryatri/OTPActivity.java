package com.caryatri.caryatri;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    EditText edtOtp;
    Button btnOtp;
    TextView txtResend;
    ImageView logo;
    CountDownTimer countDownTimer;
    TextView timer;
    boolean isRunning = false;
    ConnectivityReceiver connectivityReceiver;
    ICarYatri mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        Common.setBack(this);
        mService = Common.getAPI();
        edtOtp = findViewById(R.id.edtOtp);
        btnOtp = findViewById(R.id.btnOtp);
        logo = findViewById(R.id.logo);
        timer = findViewById(R.id.timer);
        txtResend = findViewById(R.id.txtResend);

        verifyOtp();
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtOtp.getText().toString().length() < 6 || edtOtp.getText().toString().isEmpty()) {
                    edtOtp.setError("Check OTP");
                    edtOtp.requestFocus();
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Common.verificationCode, edtOtp.getText().toString());
                SigninWithPhone(credential);
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.sendOTP(OTPActivity.this, Common.AuthPhone);
            }
        });

    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (Common.fromActivity.equals("reg")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                mService.registerNewUser(Common.register.getUsername(),
                                        Common.register.getEmail(),
                                        Common.register.getPhone(),
                                        Common.register.getPassword())
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Common.currentUser = response.body();
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                assert Common.currentUser != null;
                                                if (Common.currentUser.getError_msg() == null) {
                                                    Common.register = null;
                                                    Intent intent = new Intent(OTPActivity.this, HomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    finish();
                                                    startActivity(intent);
                                                } else
                                                    Toast.makeText(OTPActivity.this, "" + Common.currentUser.getError_msg(), Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                Log.d("ERROR", t.getMessage());
                                            }
                                        });

                            } else if (Common.fromActivity.equals("fog")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                Intent intent = new Intent(OTPActivity.this, ChangePasswordActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Pair[] pairs = new Pair[1];
                                pairs[0] = new Pair<View, String>(logo, "imageTransition");
                                ActivityOptions options;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    options = ActivityOptions.makeSceneTransitionAnimation(OTPActivity.this, pairs);
                                    startActivity(intent, options.toBundle());
                                } else
                                    startActivity(intent);
                                finish();
                            } else if (Common.fromActivity.equals("pro")) {
                                if (isRunning)
                                    countDownTimer.cancel();
                                mService.updateUser(Common.currentUser.getId(),
                                        Common.name,
                                        Common.currentUser.getPhone(),
                                        Common.image,
                                        Common.phone,
                                        Common.email)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                assert response.body() != null;
                                                if (response.body().equalsIgnoreCase("OK")) {
                                                    mService.getUserInfo(Common.email, Common.currentUser.getPassword()).enqueue(new Callback<User>() {
                                                        @Override
                                                        public void onResponse(Call<User> call, Response<User> response) {
                                                            Common.currentUser = response.body();
                                                            assert Common.currentUser != null;
                                                            if (Common.currentUser.getError_msg() == null) {
                                                                Intent intent = new Intent(OTPActivity.this, ProfileActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(OTPActivity.this, "" + Common.currentUser.getError_msg(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<User> call, Throwable t) {
                                                            Log.d("Error", t.getMessage());
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.d("Error", t.getMessage());
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(OTPActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyOtp() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                if (millisUntilFinished / 1000 == 10)
                    timer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(OTPActivity.this, "Time Out!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRunning)
            countDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRunning)
            countDownTimer.cancel();
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
