package com.caryatri.caryatri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.model.Cabs;
import com.caryatri.caryatri.model.RattingDriverInfo;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    ICarYatri mService = Common.getAPI();
    TextView amountDis, date_time, distance, tax, totalAmount, txtNightCharges, paidAmount, txtDiscount;
    Button btn_payment;
    float cabTotal;
    String price, pric;
    ConnectivityReceiver connectivityReceiver;
    Trip trip;
    RattingDriverInfo driver;
    int nc = 0;

    private BroadcastReceiver mRideComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btn_payment.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Common.setTop(this);

        amountDis = findViewById(R.id.amount);
        date_time = findViewById(R.id.date_time);
        txtNightCharges = findViewById(R.id.txtNightCharges);
        txtDiscount = findViewById(R.id.txtDiscount);
        distance = findViewById(R.id.distance);
        tax = findViewById(R.id.tax);
        totalAmount = findViewById(R.id.totalAmount);
        paidAmount = findViewById(R.id.paidAmount);
        btn_payment = findViewById(R.id.btn_payment);


        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRideComplete, new IntentFilter(Common.OK));

        mService.getTrip(Common.id).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                trip = response.body();
                assert trip != null;
                date_time.setText(getDate(trip.getStartTrip()) + " " + getResources().getString(R.string.arrow) + " " + getDate(trip.getDropTrip()));
                distance.setText((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter())) + " Km");
                tax.setText(" +Rs " + trip.getTripToll());
                FirebaseDatabase.getInstance().getReference("NightStay")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                nc = dataSnapshot.getValue(Integer.class);
                                if (trip.getDropDate().contains("0000-00-00")) {
                                    pric = String.valueOf(Integer.parseInt(calculateOneWayPrice(String.valueOf((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter()))),
                                            trip.getCabs())) + Integer.parseInt(trip.getTripToll()) + (Integer.parseInt(trip.getNightStay()) * nc));
                                    Double discount = Double.parseDouble(pric) - (Double.parseDouble(pric) * 95) / 100;
                                    pric = String.valueOf(Double.parseDouble(pric) - (discount));
                                    totalAmount.setText("Rs " + pric);
                                    txtNightCharges.setText(" +Rs" + Integer.parseInt(trip.getNightStay()) * nc);
                                    txtDiscount.setText(" -Rs" + (discount));
                                } else {
                                    pric = String.valueOf(Integer.parseInt(calculateRoundWayPrice(String.valueOf((Integer.parseInt(trip.getDropMeter()) - Integer.parseInt(trip.getPickUpMeter()))),
                                            String.valueOf(Common.getCountOfDays(trip.getPickupDate(), trip.getDropDate())),
                                            trip.getCabs())) + Integer.parseInt(trip.getTripToll()) + (Integer.parseInt(trip.getNightStay()) * nc));
                                    Double discount = Double.parseDouble(pric) - (Double.parseDouble(pric) * 95) / 100;
                                    pric = String.valueOf(Double.parseDouble(pric) - (discount));
                                    totalAmount.setText("Rs " + pric);
                                    txtNightCharges.setText(" +Rs" + Integer.parseInt(trip.getNightStay()) * nc);
                                    txtDiscount.setText(" -Rs" + (discount));
                                }
                                paidAmount.setText(" -Rs " + trip.getCabFare());
                                try {
                                    String amount1 = Float.toString(Float.parseFloat(pric) - Float.parseFloat(trip.getCabFare()));
                                    amountDis.setText(" Rs " + amount1);
                                } catch (NumberFormatException ex) { // handle your exception
                                    Log.d("ERROR", ex.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ERROR", databaseError.getMessage());
                            }
                        });
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_payment.setEnabled(false);
                Common.currentDriverRepository.deleteCompleteCurrentDriverDB(trip.getCabDriver());
                mService.getDriverInfo(trip.getCabDriver()).enqueue(new Callback<RattingDriverInfo>() {
                    @Override
                    public void onResponse(Call<RattingDriverInfo> call, Response<RattingDriverInfo> response) {
                        driver = response.body();
                        showRatingDialog();
                    }

                    @Override
                    public void onFailure(Call<RattingDriverInfo> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        btn_payment.setEnabled(true);
                    }
                });
            }
        });
    }

    private void showRatingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(InvoiceActivity.this).inflate(R.layout.ratting_dialog_layout, null);

        ImageView imgDriver = view.findViewById(R.id.imgDriver);
        TextView txtName = view.findViewById(R.id.txtName);
        final RatingBar rattingBar = view.findViewById(R.id.rattingBar);
        final TextView txtRatting = view.findViewById(R.id.txtRatting);
        final TextView txtRate = view.findViewById(R.id.txtRate);
        final EditText txtReview = view.findViewById(R.id.txtReview);
        if (!driver.getDriverImage().equalsIgnoreCase("http")) {
            Picasso.get().load(driver.getDriverImage()).error(getResources().getDrawable(R.drawable.map_round)).into(imgDriver);
        }
        txtName.setText(driver.getName());
        rattingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                txtRatting.setText(String.valueOf(ratingBar.getRating()));
                if (ratingBar.getRating() <= 2.5) {
                    txtRate.setText("Bad");
                } else if (ratingBar.getRating() <= 3.5) {
                    txtRate.setText("Good");
                } else {
                    txtRate.setText("Excellent");
                }
            }
        });

        final Button btnSubmit = view.findViewById(R.id.btnSubmit);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setEnabled(false);
                mService.insertRating(trip.getCabDriver(),
                        Common.currentUser.getPhone(),
                        Common.currentUser.getUsername(),
                        "",
                        String.valueOf(rattingBar.getRating()),
                        txtReview.getText().toString()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        dialog.dismiss();
                        Toast.makeText(InvoiceActivity.this, "" + response.body(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(InvoiceActivity.this, HomeActivity.class));
                        btnSubmit.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        btn_payment.setEnabled(true);
                        btnSubmit.setEnabled(true);
                    }
                });
            }
        });

        dialog.show();
    }

    private String calculateOneWayPrice(String Distance, String cabs) {
        float totalDistance;
        Cabs cab = new Gson().fromJson(cabs, Cabs.class);

        totalDistance = Float.parseFloat(Distance);

        cabTotal = (Float.parseFloat(String.valueOf(cab.cabPrice)) * totalDistance);
        return String.valueOf(Math.round(cabTotal));
    }

    private String calculateRoundWayPrice(String Distance, String Day, String cabs) {

        float totalDistance;
        //total distance
        totalDistance = (250 * Integer.parseInt(Day));

        Cabs cab = new Gson().fromJson(cabs, Cabs.class);

        if (totalDistance < (Integer.parseInt(Distance)))
            totalDistance = Float.parseFloat(Distance);

        cabTotal = (Float.parseFloat(String.valueOf(cab.cabPrice)) * totalDistance);
        return String.valueOf(Math.round(cabTotal));

    }

    @Override
    public void onBackPressed() {

    }

    private String getDate(String date) {
        String[] arr = date.split(" ", 2);
        return arr[0] + "/" + arr[1];
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

}