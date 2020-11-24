package com.caryatri.caryatri;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.CheckUserResponse;
import com.caryatri.caryatri.model.ImageUser;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.caryatri.caryatri.retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int PIC_FILE_REQUEST = 2222;
    TextView txt_name, txt_phone_number, txt_email;
    Toolbar toolbar;
    CircleImageView imgEdit, imgProfile;
    Button btnDone;
    ICarYatri mService = Common.getAPI();
    LinearLayout ViewName, ViewPhone, ViewEmail, edtName, edtPhone, edtEmail;
    EditText edtFullName, edtPhoneNumber, edtEmailId;
    ConnectivityReceiver connectivityReceiver;
    String uploaded_img_path = "";
    boolean isClick = false;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Common.setBack(this);
        Common.StartFirebaseLogin(this);
        toolbar = findViewById(R.id.toolbar);
        txt_name = findViewById(R.id.txt_name);
        txt_phone_number = findViewById(R.id.txt_phone_number);
        txt_email = findViewById(R.id.txt_email);
        ViewName = findViewById(R.id.ViewName);
        ViewPhone = findViewById(R.id.ViewPhone);
        ViewEmail = findViewById(R.id.ViewEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);

        imgProfile = findViewById(R.id.imgProfile);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmailId = findViewById(R.id.edtEmailId);

        imgEdit = findViewById(R.id.imgEdit);
        btnDone = findViewById(R.id.btnDone);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Common.currentUser != null) {
            txt_name.setText(Common.currentUser.getUsername());
            txt_phone_number.setText(Common.currentUser.getPhone());
            txt_email.setText(Common.currentUser.getEmail());
            edtFullName.setText(Common.currentUser.getUsername());
            edtPhoneNumber.setText(Common.currentUser.getPhone());
            edtEmailId.setText(Common.currentUser.getEmail());
            uploaded_img_path = Common.currentUser.getImage();
            Picasso.get()
                    .load(Common.currentUser.getImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(getResources().getDrawable(R.drawable.map_round))
                    .into(imgProfile);
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (isClick) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PIC_FILE_REQUEST);
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgEdit.setVisibility(View.GONE);
                ViewName.setVisibility(View.GONE);
                ViewPhone.setVisibility(View.GONE);
                ViewEmail.setVisibility(View.GONE);
                edtFullName.setVisibility(View.VISIBLE);
                edtPhoneNumber.setVisibility(View.VISIBLE);
                edtEmailId.setVisibility(View.VISIBLE);
                btnDone.setVisibility(View.VISIBLE);
                isClick = true;
                btnDone.setEnabled(true);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });
    }

    void checkData() {

        if (edtEmailId.getText().toString().isEmpty()) {
            edtEmailId.setError("Enter Your Email");
            edtEmailId.requestFocus();
            return;
        }

        if (!edtEmailId.getText().toString().contains("@")) {
            edtEmailId.setError("Enter Your Email");
            edtEmailId.requestFocus();
            return;
        }

        if (edtFullName.getText().toString().isEmpty()) {
            edtFullName.setError("Enter Your Name");
            edtFullName.requestFocus();
            return;
        }

        if (edtPhoneNumber.getText().toString().isEmpty() || edtPhoneNumber.getText().toString().length() < 10) {
            edtPhoneNumber.setError("Check Phone Number");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (!txt_email.getText().toString().equalsIgnoreCase(edtEmailId.getText().toString())) {
            mService.checkUserEmail(edtEmailId.getText().toString()).enqueue(new Callback<CheckUserResponse>() {
                @Override
                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                    if (!response.body().equals("ok")) {
                        Toast.makeText(ProfileActivity.this, "" + response.body(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    return;
                }
            });
        }

        if (!txt_phone_number.getText().toString().equalsIgnoreCase(edtPhoneNumber.getText().toString())) {
            mService.checkUser(edtPhoneNumber.getText().toString()).enqueue(new Callback<CheckUserResponse>() {
                @Override
                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                    assert response.body() != null;
                    if (response.body().getError_msg().equals("User not exists")) {
                        Common.fromActivity = "pro";
                        Common.AuthPhone = edtPhoneNumber.getText().toString();
                        Common.name = edtFullName.getText().toString();
                        Common.image = uploaded_img_path;
                        Common.phone = edtPhoneNumber.getText().toString();
                        Common.email = edtEmailId.getText().toString();
                        Common.sendOTP(ProfileActivity.this, edtPhoneNumber.getText().toString());
                        isClick = false;
                    } else {
                        Toast.makeText(ProfileActivity.this, "user exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
            return;

        }

        if (bitmap != null)
            UploadImageToServer();
        else
            mService.updateUser(Common.currentUser.getId(),
                    edtFullName.getText().toString(),
                    Common.currentUser.getPhone(),
                    uploaded_img_path,
                    edtPhoneNumber.getText().toString(),
                    edtEmailId.getText().toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            assert response.body() != null;
                            if (response.body().equalsIgnoreCase("OK")) {
                                mService.getUserInfo(edtEmailId.getText().toString(), Common.currentUser.getPassword())
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Common.currentUser = response.body();
                                                assert Common.currentUser != null;
                                                if (Common.currentUser.getError_msg() == null) {
                                                    recreate();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, "" + Common.currentUser.getError_msg(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertToString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    void UploadImageToServer() {
        String image = convertToString();
        String imageName = Common.currentUser.getPhone();
        ICarYatri apiInterface = RetrofitClient.getClient(Common.BASE_URL).create(ICarYatri.class);
        Call<ImageUser> call = apiInterface.UploadUserImage(imageName, image);

        call.enqueue(new Callback<ImageUser>() {
            @Override
            public void onResponse(Call<ImageUser> call, Response<ImageUser> response) {
                uploaded_img_path = "http://myinvented.com/urdriver/UserImage/" + Common.currentUser.getPhone() + ".jpeg";
                mService.updateUser(Common.currentUser.getId(),
                        edtFullName.getText().toString(),
                        Common.currentUser.getPhone(),
                        uploaded_img_path,
                        edtPhoneNumber.getText().toString(),
                        edtEmailId.getText().toString())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                assert response.body() != null;
                                if (response.body().equalsIgnoreCase("OK")) {
                                    mService.getUserInfo(edtEmailId.getText().toString(), Common.currentUser.getPassword())
                                            .enqueue(new Callback<User>() {
                                                @Override
                                                public void onResponse(Call<User> call, Response<User> response) {
                                                    Common.currentUser = response.body();
                                                    assert Common.currentUser != null;
                                                    if (Common.currentUser.getError_msg() == null) {
                                                        recreate();
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "" + Common.currentUser.getError_msg(), Toast.LENGTH_SHORT).show();
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

            @Override
            public void onFailure(Call<ImageUser> call, Throwable t) {
                Log.d("Server Response", "" + t.toString());
            }
        });
    }
}
