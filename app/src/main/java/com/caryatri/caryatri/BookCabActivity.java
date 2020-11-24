package com.caryatri.caryatri;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.adapter.CabItemOneWayAdapter;
import com.caryatri.caryatri.adapter.CabItemRoundWayAdapter;
import com.caryatri.caryatri.adapter.SliderOneWayAdapter;
import com.caryatri.caryatri.adapter.SliderRoundWayAdapter;
import com.caryatri.caryatri.model.DataMessage;
import com.caryatri.caryatri.model.MyResponse;
import com.caryatri.caryatri.model.PaymentData;
import com.caryatri.caryatri.model.Token;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.payment.JSONParser;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.caryatri.caryatri.retrofit.IFCMService;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookCabActivity extends CrashActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener,
        PaytmPaymentTransactionCallback {

    Toolbar toolbar;
    EditText edtPickUpAddress, edtDropOffAddress, edtFullName, edtMobileNumber, edtEmailId;
    CompositeDisposable compositeDisposable;
    int listSize = 0;
    CardView payment;
    private ViewPager mSliderViewPager;
    private TextView mDotLayout;
    TextView est_fare, total_price, txtDiscountAmount, txtAfterRide;
    ICarYatri mService;
    ImageView info_img;
    List<CabOneWay> oneWayList = new ArrayList<>();
    List<CabRoundWay> roundWayList = new ArrayList<>();
    private Button btnBookNow;
    double cabTotal = 0, payAmount = 0, Discount = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    String CHECKSUMHASH = "", PaymentDetail = "";
    PaymentData paymentData = new PaymentData();
    String custid = "", orderId = "", mid = "", amount = "";
    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Common.setTop(this);
        if (ContextCompat.checkSelfPermission(BookCabActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BookCabActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        mService = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
        Common.setBack(this);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        Paper.init(this);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            if (Common.currentUser == null) {
                String email = Paper.book().read("email");
                String password = Paper.book().read("password");
                mService.getUserInfo(email, password)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                Common.currentUser = response.body();
                                sittingId();
                                onClick();
                                edtPickUpAddress.setText("");
                                edtDropOffAddress.setText("");
                                loadCabList();
                            }

                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                swipeRefreshLayout.setRefreshing(false);
                                Log.d("EEROR", t.getMessage());
                            }
                        });
            } else {
                sittingId();
                onClick();
                edtPickUpAddress.setText("");
                edtDropOffAddress.setText("");
                loadCabList();
            }

        });

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            if (Common.currentUser == null) {
                String email = Paper.book().read("email");
                String password = Paper.book().read("password");
                mService.getUserInfo(email, password)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                Common.currentUser = response.body();
                                sittingId();
                                onClick();
                                edtPickUpAddress.setText("");
                                edtDropOffAddress.setText("");
                                loadCabList();
                            }

                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                swipeRefreshLayout.setRefreshing(false);
                                Log.d("EEROR", t.getMessage());
                            }
                        });
            } else {
                sittingId();
                onClick();
                edtPickUpAddress.setText("");
                edtDropOffAddress.setText("");
                loadCabList();
            }
        });

    }

    private void onClick() {
        btnBookNow.setOnClickListener(view -> {
            if (btnBookNow.getText().toString().equalsIgnoreCase("book now")) {
                checkData();
            } else if (btnBookNow.getText().toString().equalsIgnoreCase("confirm")) {
                FirebaseDatabase.getInstance().getReference("MID").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderId = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
                        Log.d("ERROR", orderId);
                        custid = Common.currentUser.getPhone();
                        mid = dataSnapshot.getValue(String.class); /// your marchant key
                        amount = total_price.getText().toString();
                        paymentData.setTnxAmount(amount);
                        paymentData.setOrderId(orderId);
                        paymentData.setTxnType("Receiving");
                        sendUserDetailTOServer dl = new sendUserDetailTOServer();
                        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });


            }
        });
        info_img.setOnClickListener(view -> showCabsDialog());
    }

    private void sendData() {
        btnBookNow.setEnabled(false);
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                for (int i = 0; i < oneWayList.size(); i++) {
                    final String list = new Gson().toJson(oneWayList.get(i));
                    final String cabModel = oneWayList.get(i).cabModel;
                    String c = "" + UUID.randomUUID().toString();
                    paymentData.setUID(c);
                    PaymentDetail = new Gson().toJson(paymentData);
                    mService.insertOneWay(edtFullName.getText().toString(),
                            edtMobileNumber.getText().toString(),
                            edtEmailId.getText().toString(),
                            edtPickUpAddress.getText().toString(),
                            edtDropOffAddress.getText().toString(),
                            Common.tripDetailOneWay.getPickUpDate(),
                            Common.tripDetailOneWay.getPickUpTime(),
                            Common.tripDetailOneWay.getFrom(),
                            Common.tripDetailOneWay.getTo(),
                            list,
                            Common.currentUser.getPhone(),
                            String.valueOf(500),
                            "00",
                            0,
                            cabModel,
                            PaymentDetail)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body().equals("OK")) {
                                        FirebaseDatabase.getInstance().getReference("AdminPhone")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        sendNotificationToDriver(dataSnapshot.getValue(String.class), "0");
                                                        Common.tripDetailOneWay = null;
                                                        Common.WayActivity = null;
                                                        Common.cabOneWayRepository.emptyCabOneWay();
                                                        Common.userDataOneWayRepository.emptyUserDataOneWay();
                                                        paymentData = new PaymentData();
                                                        compositeDisposable.dispose();
                                                        Toast.makeText(BookCabActivity.this, "Cab Request Sent", Toast.LENGTH_SHORT).show();
                                                        Common.sendMessage("Working Ok", Common.currentUser.getPhone());
                                                        sendNotificationToUser();
                                                        Intent intent = new Intent(BookCabActivity.this, HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("ERROR", databaseError.getMessage());
                                                    }
                                                });
                                    } else {
                                        Log.d("Error", response.body());
                                        Toast.makeText(BookCabActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
                }
            } else if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                for (int i = 0; i < roundWayList.size(); i++) {
                    final String list = new Gson().toJson(roundWayList.get(i));
                    final String cabModel = roundWayList.get(i).cabModel;
                    String c = "" + UUID.randomUUID().toString();
                    paymentData.setUID(c);
                    PaymentDetail = new Gson().toJson(paymentData);
                    mService.insertRoundWay(edtFullName.getText().toString(),
                            edtMobileNumber.getText().toString(),
                            edtEmailId.getText().toString(),
                            edtPickUpAddress.getText().toString(),
                            edtDropOffAddress.getText().toString(),
                            Common.tripDetailRoundWay.getPickUpDate(),
                            Common.tripDetailRoundWay.getDropDate(),
                            Common.tripDetailRoundWay.getPickUpTime(),
                            Common.tripDetailRoundWay.getFrom(),
                            Common.tripDetailRoundWay.getTo(),
                            list,
                            Common.currentUser.getPhone(),
                            String.valueOf(500),
                            "00",
                            0,
                            cabModel,
                            PaymentDetail)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.body().equals("OK")) {
                                        FirebaseDatabase.getInstance().getReference("AdminPhone")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        sendNotificationToDriver(dataSnapshot.getValue(String.class), "1");
                                                        Common.tripDetailRoundWay = null;
                                                        Common.WayActivity = null;
                                                        Common.cabRoundWayRepository.emptyCabRoundWay();
                                                        Common.userDataRoundWayRepository.emptyUserDataRoundWay();
                                                        compositeDisposable.dispose();
                                                        Toast.makeText(BookCabActivity.this, "Cab Request Sent", Toast.LENGTH_SHORT).show();
                                                        Common.sendMessage("Working Ok", Common.currentUser.getPhone());
                                                        sendNotificationToUser();
                                                        Intent intent = new Intent(BookCabActivity.this, HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Log.d("ERROR", databaseError.getMessage());
                                                    }
                                                });
                                    } else {
                                        Log.d("Error", response.body());
                                        Snackbar.make(Common.rootLayout, "" + response.message(), Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.d("Error", t.getMessage());
                                }
                            });
                }
            }
        }
    }

    private void showCabsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Cab Fare");
        View view = LayoutInflater.from(this).inflate(R.layout.item_cab_dialog_layout, null);
        RecyclerView list = view.findViewById(R.id.list_item);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                CabItemOneWayAdapter cabItemOneWayAdapter = new CabItemOneWayAdapter(this, oneWayList);
                list.setAdapter(cabItemOneWayAdapter);
            } else if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                CabItemRoundWayAdapter cabItemRoundWayAdapter = new CabItemRoundWayAdapter(this, roundWayList);
                list.setAdapter(cabItemRoundWayAdapter);
            }
        }
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

    private void checkData() {
        if (edtPickUpAddress.getText().toString().isEmpty()) {
            edtPickUpAddress.setError("Enter Your Pickup Address");
            edtPickUpAddress.requestFocus();
            return;
        }
        if (edtDropOffAddress.getText().toString().isEmpty()) {
            edtDropOffAddress.setError("Enter Your DropOff Address");
            edtDropOffAddress.requestFocus();
            return;
        }
        if (edtFullName.getText().toString().isEmpty()) {
            edtFullName.setError("Enter Your Full Name");
            edtFullName.requestFocus();
            return;
        }
        if (edtMobileNumber.getText().toString().isEmpty() || edtMobileNumber.getText().toString().length() < 10) {
            edtMobileNumber.setError("Enter Your Phone Number");
            edtMobileNumber.requestFocus();
            return;
        }
        if (edtEmailId.getText().toString().isEmpty() || !edtEmailId.getText().toString().contains("@")) {
            edtEmailId.setError("Enter Your Email Address");
            edtEmailId.requestFocus();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Cab's");
        builder.setMessage("You are sure entered information is correct!");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                settingPayment();
                edtPickUpAddress.setEnabled(false);
                edtDropOffAddress.setEnabled(false);
                edtFullName.setEnabled(false);
                edtMobileNumber.setEnabled(false);
                edtEmailId.setEnabled(false);
                btnBookNow.setText("confirm");
                payment.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

    private void settingPayment() {
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                calculateOneWayPrice();
            } else if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                calculateRoundWayPrice();
            }
        }
    }

    private void calculateOneWayPrice() {
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                for (int i = 0; i < oneWayList.size(); i++) {
                    cabTotal = (oneWayList.get(i).cabPrice * Common.distanceValue);
                    payAmount = 500;
                    Discount = cabTotal - (cabTotal / 100) * 95;
                    Common.cabOneWayRepository.UpdateCabOneWay(String.valueOf(Math.round(cabTotal)), oneWayList.get(i).id);
                }
                txtAfterRide.setText("" + Math.round(cabTotal - Discount));
                txtDiscountAmount.setText("-" + Math.round(Discount));
                est_fare.setText(String.valueOf(Math.round(cabTotal)));
                total_price.setText(String.valueOf(Math.round(payAmount)));
            }
        }
    }

    private void calculateRoundWayPrice() {
        float totalDistance = Math.round(Common.distanceValue);//total distance
        if (totalDistance < 250 || Common.getCountOfDays(Common.Date1, Common.Date2) > 1)
            totalDistance = 250 * Common.getCountOfDays(Common.Date1, Common.Date2);
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                for (int i = 0; i < roundWayList.size(); i++) {
                    cabTotal = (roundWayList.get(i).cabPrice * totalDistance);
                    payAmount = 500;
                    Discount = cabTotal - (cabTotal / 100) * 95;
                    Common.cabRoundWayRepository.UpdateCabRoundWay(String.valueOf(Math.round(cabTotal)), roundWayList.get(i).id);
                }
                txtAfterRide.setText("" + Math.round(cabTotal - Discount));
                txtDiscountAmount.setText("-" + Math.round(Discount));
                est_fare.setText(String.valueOf(Math.round(cabTotal)));
                total_price.setText(String.valueOf(Math.round(payAmount)));
            }
        }
    }

    private void sittingId() {
        edtPickUpAddress = findViewById(R.id.edtPickUpAddress);
        edtDropOffAddress = findViewById(R.id.edtDropOffAddress);
        edtFullName = findViewById(R.id.edtFullName);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        edtEmailId = findViewById(R.id.edtEmailId);
        toolbar = findViewById(R.id.toolbar);
        Common.rootLayout = findViewById(R.id.root_layout);
        setSupportActionBar(toolbar);
        payment = findViewById(R.id.payment);
        btnBookNow = findViewById(R.id.btnBookNow);
        info_img = findViewById(R.id.info_img);
        est_fare = findViewById(R.id.est_fare);
        txtDiscountAmount = findViewById(R.id.txtDiscountAmount);
        txtAfterRide = findViewById(R.id.txtAfterRide);
        total_price = findViewById(R.id.total_price);
        mDotLayout = findViewById(R.id.linearLayout);
        mSliderViewPager = findViewById(R.id.slideViewPage);
        setTextView();
    }

    private void setTextView() {
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                toolbar.setTitle(Common.tripDetailOneWay.getFrom() + getResources().getString(R.string.arrow) + Common.tripDetailOneWay.getTo());
                toolbar.setSubtitle(Common.tripDetailOneWay.getPickUpDate() + " | " + Common.tripDetailOneWay.getPickUpTime() + " | " + Common.WayActivity);
                edtPickUpAddress.setHint(getResources().getString(R.string.pickup_address) + " @" + Common.tripDetailOneWay.getFrom());
                edtDropOffAddress.setHint(getResources().getString(R.string.drop_off_address) + " @" + Common.tripDetailOneWay.getTo());
                edtFullName.setText(Common.currentUser.getUsername());
                edtMobileNumber.setText(Common.currentUser.getPhone());
                edtEmailId.setText(Common.currentUser.getEmail());
            } else if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                toolbar.setTitle(Common.tripDetailRoundWay.getFrom() + getResources().getString(R.string.arrow) + Common.tripDetailRoundWay.getTo());
                toolbar.setSubtitle(Common.tripDetailRoundWay.getPickUpDate() + getResources().getString(R.string.arrow) + Common.tripDetailRoundWay.getDropDate() + " | " + Common.tripDetailRoundWay.getPickUpTime() + " | " + Common.WayActivity);
                edtPickUpAddress.setHint(getResources().getString(R.string.pickup_address) + " @" + Common.tripDetailRoundWay.getFrom());
                edtDropOffAddress.setHint(getResources().getString(R.string.drop_off_address) + " @" + Common.tripDetailRoundWay.getTo());
                edtFullName.setText(Common.currentUser.getUsername());
                edtMobileNumber.setText(Common.currentUser.getPhone());
                edtEmailId.setText(Common.currentUser.getEmail());
            }
        }
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //////////////////////*******************LoadCab List***************************///////////
    private void loadCabList() {
        if (Common.WayActivity != null) {
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                compositeDisposable.add(Common.cabOneWayRepository.getCabOneWayItemByStatus(Common.status)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<CabOneWay>>() {
                            @Override
                            public void accept(List<CabOneWay> cabOneWays) throws Exception {
                                Log.d("ERROR", new Gson().toJson(cabOneWays));
                                displayCabOneWayItem(cabOneWays);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                swipeRefreshLayout.setRefreshing(false);
                                Log.d("Error", throwable.getMessage());
                            }
                        }));
            } else if (Common.WayActivity.equalsIgnoreCase("Round Way")) {
                compositeDisposable.add(Common.cabRoundWayRepository.getCabRoundWayItemByStatus(Common.status)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<CabRoundWay>>() {
                            @Override
                            public void accept(List<CabRoundWay> cabRoundWays) throws Exception {
                                displayCabRoundWayItem(cabRoundWays);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                swipeRefreshLayout.setRefreshing(false);
                                Log.d("ERROR", throwable.getMessage());
                            }
                        }));
            }
        }

    }

    private void displayCabOneWayItem(List<CabOneWay> cabs) {
        listSize = cabs.size();
        oneWayList = cabs;
        SliderOneWayAdapter sliderOneWayAdapter = new SliderOneWayAdapter(BookCabActivity.this, cabs);
        mSliderViewPager.setAdapter(sliderOneWayAdapter);
        addDotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewListener);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void displayCabRoundWayItem(List<CabRoundWay> cabs) {
        listSize = cabs.size();
        roundWayList = cabs;
        SliderRoundWayAdapter sliderRoundWayAdapter = new SliderRoundWayAdapter(BookCabActivity.this, cabs);
        mSliderViewPager.setAdapter(sliderRoundWayAdapter);
        addDotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewListener);
        swipeRefreshLayout.setRefreshing(false);
    }

    /////////////***************************************************************/////////////////
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Common.WayActivity != null)
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                Common.cabOneWayRepository.emptyCabOneWay();
            } else {
                Common.cabRoundWayRepository.emptyCabRoundWay();
            }
        Intent intent = new Intent(BookCabActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void addDotsIndicator(int position) {
        mDotLayout.setText(position + 1 + "/" + listSize);
    }

    private void sendNotificationToUser() {
        //get Server Token
        mService.getToken(Common.currentUser.getPhone(), "0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token

                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "Cab Request");
                        contentSend.put("message", "Your request will be confirm with in 24hrs");
                        DataMessage dataMessage = new DataMessage();
                        if (response.body().getToken() != null)
                            dataMessage.setTo(response.body().getToken());
                        dataMessage.setData(contentSend);

                        IFCMService ifcmService = Common.getGetFCMService();
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(BookCabActivity.this, "Notification send", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ERROR", new Gson().toJson(response.body()));
                                                Toast.makeText(BookCabActivity.this, "Notification send failed ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(BookCabActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(BookCabActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNotificationToDriver(String phone, final String Way) {
        //get Server Token
        mService.getToken(phone, "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        //when we have Token , just send notification to this token
                        if (response.body() != null) {
                            Map<String, String> contentSend = new HashMap<>();
                            contentSend.put("title", "Cab Request");
                            contentSend.put("message", Common.currentUser.getPhone());
                            contentSend.put("cabType", Way);
                            DataMessage dataMessage = new DataMessage();
                            assert response.body() != null;
                            if (response.body().getToken() != null)
                                dataMessage.setTo(response.body().getToken());
                            else
                                Toast.makeText(BookCabActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            dataMessage.setData(contentSend);

                            IFCMService ifcmService = Common.getGetFCMService();
                            ifcmService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                assert response.body() != null;
                                                if (response.body().success != 1) {
                                                    Log.d("ERROR", new Gson().toJson(response.body()));
                                                    Toast.makeText(BookCabActivity.this, "Notification send failed ", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Toast.makeText(BookCabActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d("ERROR", new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
//                            Log.d("ERROR",t.getMessage());
                        Toast.makeText(BookCabActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
            paymentData.setTxnStatus(inResponse.getString("STATUS"));
            paymentData.setTxnId(inResponse.getString("TXNID"));
            paymentData.setRefundId("0");
            sendData();
        } else {
            Toast.makeText(this, "" + inResponse.getString("STATUS"), Toast.LENGTH_SHORT).show();
            Log.d("ERROR", inResponse.toString());
        }
    }

    @Override
    public void networkNotAvailable() {
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction Cancelled ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Common.WayActivity != null)
            if (Common.WayActivity.equalsIgnoreCase("One Way")) {
                Common.cabOneWayRepository.emptyCabOneWay();
            } else {
                Common.cabRoundWayRepository.emptyCabRoundWay();
            }
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(this, "Transaction Cancelled ", Toast.LENGTH_SHORT).show();
    }

    public class sendUserDetailTOServer extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(BookCabActivity.this);
        String url = "https://myinvented.com/urdriver/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(BookCabActivity.this);
            String param =
                    "MID=" + mid +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custid +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + amount + "&WEBSITE=WEBSTAGING" +
                            "&CALLBACK_URL=" + varifyurl + "&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>", jsonObject.toString());
            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e("CheckSum result >>", CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amount);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL", varifyurl);
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param " + paramMap.toString());
            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(BookCabActivity.this, true, true,
                    BookCabActivity.this);
        }
    }

}
