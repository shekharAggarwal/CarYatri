package com.caryatri.caryatri;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.model.RatingCab;
import com.caryatri.caryatri.model.RattingDriverInfo;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.model.cabDetails;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AfterRide extends BottomSheetDialogFragment {

    CircleImageView profilePic;

    TextView Name, txtRatting, RatedBy, cabNumber, cabSeating, cabModel, startTime,
            endTime, txtFrom, txtTo, paidAmount, toolbar_title, toolbar_sub_title;

    ICarYatri mService = Common.getAPI();

    Toolbar toolbar;

    RattingDriverInfo rattingDriverInfo;

    cabDetails CabDetails;

    RatingCab ratingCab;

    String Phone = null;

    Trip trip;

    public AfterRide(String phone, Trip trip) {
        Phone = phone;
        this.trip = trip;
    }

    public AfterRide() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_after_ride, container, false);

        Name = view.findViewById(R.id.Name);
        profilePic = view.findViewById(R.id.profilePic);
        txtRatting = view.findViewById(R.id.txtRatting);
        RatedBy = view.findViewById(R.id.RatedBy);
        cabNumber = view.findViewById(R.id.cabNumber);
        cabSeating = view.findViewById(R.id.cabSeating);
        toolbar = view.findViewById(R.id.toolbar);
        cabModel = view.findViewById(R.id.cabModel);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        txtFrom = view.findViewById(R.id.txtFrom);
        toolbar_title = view.findViewById(R.id.toolbar_title);
        toolbar_sub_title = view.findViewById(R.id.toolbar_sub_title);
        txtTo = view.findViewById(R.id.txtTo);
        paidAmount = view.findViewById(R.id.paidAmount);
        Paper.init(getContext());

        //loading data into views
        if (Phone != null) {
            //fetching data of driver from server
            mService.getDriverInfo(Phone).enqueue(new Callback<RattingDriverInfo>() {
                @Override
                public void onResponse(Call<RattingDriverInfo> call, Response<RattingDriverInfo> response) {
                    rattingDriverInfo = response.body();
                    if (rattingDriverInfo.getError_msg() == null) {
                        if (!response.body().getDriverImage().equalsIgnoreCase("http")) {
                            Picasso.get().load(response.body().getDriverImage()).error(getResources().getDrawable(R.drawable.map_round)).into(profilePic);
                        }
                        Name.setText(response.body().getName());
                    } else {
                        Toast.makeText(getContext(), "" + rattingDriverInfo.getError_msg(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RattingDriverInfo> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });

            //fetching data of cab from server
            mService.getcabbydriver(Phone).enqueue(new Callback<cabDetails>() {
                @Override
                public void onResponse(Call<cabDetails> call, Response<cabDetails> response) {
                    CabDetails = response.body();
                    if (CabDetails != null) {
                        cabNumber.setText(CabDetails.getCabNumber());
                        cabSeating.setText(CabDetails.getCabSitting());
                        cabModel.setText(CabDetails.getCabBrand() + " " + CabDetails.getCabModel());
                    }
                }

                @Override
                public void onFailure(Call<cabDetails> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });

            //fetching data of rating of cab driver from server
            mService.getRatingCab(Phone).enqueue(new Callback<RatingCab>() {
                @Override
                public void onResponse(Call<RatingCab> call, Response<RatingCab> response) {
                    ratingCab = response.body();
                    if (response.body() != null) {
                        DecimalFormat df = new DecimalFormat("#.#");
                        df.setRoundingMode(RoundingMode.CEILING);
                        if (response.body().getRate() != null) {
                            String s = Float.parseFloat(response.body().getRate()) > 0.0 ? "" + df.format(Float.parseFloat(response.body().getRate())) : "0.0";
                            txtRatting.setText(s);
                        } else
                            txtRatting.setText("0.0");
                        RatedBy.setText("Rated by " + ratingCab.getCountRate() + " people");
                    }
                }

                @Override
                public void onFailure(Call<RatingCab> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });

            if (trip.getDropDate().equals("0000-00-00"))
                toolbar_title.setText(getDate(trip.getDropTrip()) + " | One Way");
            else
                toolbar_title.setText(getDate(trip.getDropTrip()) + " | Round Way");

            toolbar_sub_title.setText("#" + trip.getId());

            txtFrom.setText(trip.getSourceAddress() + "," + trip.getSource());
            txtTo.setText(trip.getDestinationAddress() + "," + trip.getDestination());
            paidAmount.setText("₹" + trip.getCabFare());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = formatter.parse(trip.getStartTrip());
                startTime.setText(getAMPM(date.getHours(), date.getMinutes()));
                date = formatter.parse(trip.getDropTrip());
                endTime.setText(getAMPM(date.getHours(), date.getMinutes()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    //converting 24 hr into 12 hrs
    private String getAMPM(int hours, int mintus) {
        int hour = hours;
        int minutes = mintus;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = String.valueOf(minutes);
        }
        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();

        return aTime;
    }

    //splitting date and time
    private String getDate(String date) {
        String[] arr = date.split(" ", 2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1 != null)
            return arr[0] + "/" + getAMPM(date1.getHours(), date1.getMinutes());
        else
            return arr[0] + "/" + arr[1];
    }
}
