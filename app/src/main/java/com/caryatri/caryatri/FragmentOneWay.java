package com.caryatri.caryatri;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.entities.FontTextView;
import com.caryatri.caryatri.model.TripDetailOneWay;
import com.caryatri.caryatri.retrofit.IGoogleAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentOneWay extends Fragment {

    Animation animation, loadAnimation3, loadAnimation4, loadAnimation2, loadAnimation;
    private AutoCompleteTextView et_fromCity;
    private AutoCompleteTextView et_toCity;
    private FontTextView tvTripDay, tvTripDate, tvPickTime, tvTripMonth;
    private LinearLayout linlyt_swapCity, llPickUpTime, linlyt_DateSelector;
    private DatePickerDialog.OnDateSetListener pickUp;
    private TimePickerDialog timePickerDialog;
    private IGoogleAPI mGoogle = Common.getGoogleAPI();
    private Button btn_search_cab;

    List<String> suggestList = new ArrayList<>();

    private ImageView iv_mini, iv_sedan, iv_suv;

    private LinearLayout ll_mini, ll_sedan, ll_suv;

    private boolean isCheckCab = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_way, null);

        readID(view);
        settingAnimation();
        settingClickListener();

        pickUp = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                m = m + 1;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, y);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.DAY_OF_MONTH, d);
                Common.Date = y + "-" + m + "-" + d;
                tvTripDate.setText(getMonthForInt(m - 1));
                tvTripDay.setText(String.valueOf(d));
                tvTripMonth.setText("," + y);
            }
        };


        if (Common.SearchedTextTo != null)
            et_toCity.setText(Common.SearchedTextTo);
        if (Common.SearchedTextFrom != null)
            et_fromCity.setText(Common.SearchedTextFrom);


        return view;
    }

    private void settingClickListener() {
        linlyt_swapCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });


        linlyt_DateSelector.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });

        llPickUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());

            }
        });

        et_fromCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    settClickListener(view.getId());
            }
        });

        et_toCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    settClickListener(view.getId());
            }
        });

        btn_search_cab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });

        ll_mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });
        ll_sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });
        ll_suv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settClickListener(view.getId());
            }
        });

    }

    private void settClickListener(int id) {
        switch (id) {
            case R.id.et_fromCity:
                if (suggestList.size() == 0)
                    FirebaseDatabase.getInstance().getReference("CityList")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    suggestList = new ArrayList<>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String list = postSnapshot.getValue(String.class);
                                        suggestList.add(list);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                            android.R.layout.simple_dropdown_item_1line, suggestList);
                                    et_fromCity.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERRORDATA", databaseError.getMessage());
                                }
                            });
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, suggestList);
                    et_fromCity.setAdapter(adapter);
                }
                return;

            case R.id.et_toCity:
                if (suggestList.size() == 0)
                    FirebaseDatabase.getInstance().getReference("CityList")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    suggestList = new ArrayList<>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String list = postSnapshot.getValue(String.class);
                                        suggestList.add(list);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                            android.R.layout.simple_dropdown_item_1line, suggestList);
                                    et_toCity.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("ERRORDATA", databaseError.getMessage());
                                }
                            });
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, suggestList);
                    et_toCity.setAdapter(adapter);
                }
                return;

            case R.id.linlyt_swapCity:
                if (et_fromCity.getText().toString().isEmpty() || et_toCity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill Source & Destination", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    linlyt_swapCity.startAnimation(animation);
                    et_fromCity.startAnimation(loadAnimation);
                    final String temp = et_fromCity.getText().toString();
                    loadAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            et_fromCity.startAnimation(loadAnimation3);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    et_toCity.startAnimation(loadAnimation2);
                    loadAnimation2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            et_toCity.startAnimation(loadAnimation4);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    loadAnimation3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            et_fromCity.setText(et_toCity.getText().toString());
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    loadAnimation4.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            et_toCity.setText(temp);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    return;
                }

            case R.id.linlyt_DateSelector:
                if (et_fromCity.getText().toString().isEmpty() || et_toCity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill Source & Destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT < 24) {
                    Calendar instance = Calendar.getInstance(Locale.ENGLISH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            R.style.DatePickerTheme,
                            pickUp, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMinDate(instance.getTimeInMillis());
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    datePickerDialog.show();
                    return;
                }
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                        R.style.DatePickerTheme,
                        pickUp,
                        year, month, day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
                return;
            case R.id.llPickUpTime:
                if (et_fromCity.getText().toString().isEmpty() || et_toCity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill Source & Destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tvTripMonth.getText().toString().isEmpty() || tvTripDay.getText().toString().isEmpty() || tvTripDate.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill PickUp Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY) + 3;
                int currentMinute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(getContext(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        int hour = hourOfDay;
                        int minutes = minute;
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
                        Common.Time = hourOfDay + ":" + min;
                        String aTime = new StringBuilder().append(hour).append(':')
                                .append(min).append(" ").append(timeSet).toString();

                        tvPickTime.setText(aTime);
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
                return;
            case R.id.ll_mini:
                iv_mini.setImageResource(R.drawable.ic_cab_selection_mini_selected);
                iv_sedan.setImageResource(R.drawable.ic_cab_selection_sedan_pressed);
                iv_suv.setImageResource(R.drawable.ic_cab_prime_pressed);
                isCheckCab = true;
                Common.CabType = "MINI";
                return;
            case R.id.ll_sedan:
                iv_mini.setImageResource(R.drawable.ic_cab_selection_mini_pressed);
                iv_sedan.setImageResource(R.drawable.ic_cab_selection_sedan_selected);
                iv_suv.setImageResource(R.drawable.ic_cab_prime_pressed);
                isCheckCab = true;
                Common.CabType = "SEDAN";
                return;
            case R.id.ll_suv:
                iv_mini.setImageResource(R.drawable.ic_cab_selection_mini_pressed);
                iv_sedan.setImageResource(R.drawable.ic_cab_selection_sedan_pressed);
                iv_suv.setImageResource(R.drawable.ic_cab_prime_selected);
                isCheckCab = true;
                Common.CabType = "SUV";
                return;
            case R.id.btn_search_cab:
                if (et_fromCity.getText().toString().isEmpty() || et_toCity.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill Source & Destination", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean a = true;
                for (int i = 0; i < suggestList.size(); i++) {
                    if (et_fromCity.getText().toString().equalsIgnoreCase(suggestList.get(i))) {
                        a = false;
                        break;
                    }
                }
                if (a) {
                    et_fromCity.setError("Source place not available");
                    return;
                }
                a = true;
                for (int i = 0; i < suggestList.size(); i++) {
                    if (et_toCity.getText().toString().equalsIgnoreCase(suggestList.get(i))) {
                        a = false;
                        break;
                    }
                }
                if (a) {
                    et_toCity.setError("Destination place not available");
                    return;
                }
                if (tvTripMonth.getText().toString().isEmpty() || tvTripDay.getText().toString().isEmpty() || tvTripDate.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill PickUp Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tvPickTime.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Fill PickUp Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isCheckCab) {
                    Toast.makeText(getContext(), "Select Cab Type!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar todayCal = Calendar.getInstance();
                int todayYear = todayCal.get(Calendar.YEAR);
                int today = todayCal.get(Calendar.MONTH);
                int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);

                /*if (Common.getCountOfDays(todayYear + "-" + today + "-" + todayDay, Common.Date) == 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                    String str = sdf.format(new Date());
                    if (Common.calTime(str, Common.Time) < 3) {
                        Toast.makeText(getContext(), "Time Should be more then 3hrs", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }*/
                settingPayment();
                break;
            default:
                return;
        }
    }

    private void settingAnimation() {
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_around_center_point);
        loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_with_fade_out);
        loadAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_with_fade_out);
        loadAnimation3 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_with_fade_in);
        loadAnimation4 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_with_fade_in);
    }

    private void readID(View view) {
        et_fromCity = view.findViewById(R.id.et_fromCity);
        et_toCity = view.findViewById(R.id.et_toCity);
        tvTripDay = view.findViewById(R.id.tvTripDay);
        tvTripDate = view.findViewById(R.id.tvTripDate);
        tvTripMonth = view.findViewById(R.id.tvTripMonth);
        linlyt_DateSelector = view.findViewById(R.id.linlyt_DateSelector);
        llPickUpTime = view.findViewById(R.id.llPickUpTime);
        tvPickTime = view.findViewById(R.id.tvPickTime);
        linlyt_swapCity = view.findViewById(R.id.linlyt_swapCity);
        btn_search_cab = view.findViewById(R.id.btn_search_cab);

        iv_mini = view.findViewById(R.id.iv_mini);
        iv_sedan = view.findViewById(R.id.iv_sedan);
        iv_suv = view.findViewById(R.id.iv_suv);

        ll_mini = view.findViewById(R.id.ll_mini);
        ll_sedan = view.findViewById(R.id.ll_sedan);
        ll_suv = view.findViewById(R.id.ll_suv);
    }

    private void settingPayment() {
        //getting distance
        final ProgressDialog mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Loading....");
        mDialog.setCancelable(false);
        mDialog.show();
        String pickUp = et_fromCity.getText().toString().replaceAll(" ", "+");
        String drop = et_toCity.getText().toString().replaceAll(" ", "+");
        String distanceQuery = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins=" + pickUp +
                "&destinations=" + drop +
                "&departure_time=now" +
                "&key=<Your-Key>";
        mGoogle.getPath(distanceQuery).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                assert response.body() != null;
                Log.d("ERROR", new Gson().toJson(response.body()));
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String status = jsonObject.getString("status");
                    if (!status.isEmpty() && status.equals("OK")) {
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        JSONObject data = rows.getJSONObject(0);
                        JSONArray element = data.getJSONArray("elements");
                        JSONObject check = element.getJSONObject(0);
                        if (check.getString("status").equals("OK")) {
                            JSONObject elements = element.getJSONObject(0);
                            JSONObject distance = elements.getJSONObject("distance");
                            Common.distanceValue = distance.getDouble("value");
                            Common.distanceValue *= 2;
                            Common.distanceValue /= 1000;
                            mDialog.dismiss();
                            if (Common.CabType.equalsIgnoreCase("mini"))
                                CalculateRateMini();
                            else if (Common.CabType.equalsIgnoreCase("sedan"))
                                CalculateRateSedan();
                            else if (Common.CabType.equalsIgnoreCase("suv"))
                                CalculateRateSuv();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "" + check.getString("status"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(getContext(), "" + status, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    mDialog.dismiss();
                    Log.d("Error1", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                mDialog.dismiss();
                Log.d("ERRORF", t.getLocalizedMessage());

            }
        });
    }

    private void CalculateRateMini() {
        FirebaseDatabase.getInstance().getReference("CabRate").child("Mini")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (!postSnapshot.getKey().equalsIgnoreCase("above")) {
                                if ((Common.distanceValue) <= Double.parseDouble(postSnapshot.getKey())) {
                                    Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                            et_toCity.getText().toString(),
                                            Common.Date,
                                            Common.Time,
                                            Common.CabType,
                                            "" + postSnapshot.getValue(Double.class));
                                    Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                                    Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                                    Common.WayActivity = "One Way";
                                    startActivity(intent2);
                                    break;
                                }
                            } else {
                                Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                        et_toCity.getText().toString(),
                                        Common.Date,
                                        Common.Time,
                                        Common.CabType,
                                        "" + postSnapshot.getValue(Double.class));
                                Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                                Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                                Common.WayActivity = "One Way";
                                startActivity(intent2);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ERROR", databaseError.getMessage());
                    }
                });
    }

    private void CalculateRateSedan() {
        FirebaseDatabase.getInstance().getReference("CabRate").child("Sedan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (!postSnapshot.getKey().equalsIgnoreCase("above")) {
                        if ((Common.distanceValue) <= Double.parseDouble(postSnapshot.getKey())) {
                            Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                    et_toCity.getText().toString(),
                                    Common.Date,
                                    Common.Time,
                                    Common.CabType,
                                    "" + postSnapshot.getValue(Double.class));
                            Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                            Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                            Common.WayActivity = "One Way";
//                        Common.BookNow = null;
                            startActivity(intent2);
                            break;
                        }
                    } else {
                        Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                et_toCity.getText().toString(),
                                Common.Date,
                                Common.Time,
                                Common.CabType,
                                "" + postSnapshot.getValue(Double.class));
                        Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                        Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                        Common.WayActivity = "One Way";
                        startActivity(intent2);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
    }

    private void CalculateRateSuv() {
        FirebaseDatabase.getInstance().getReference("CabRate").child("Suv").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (!postSnapshot.getKey().equalsIgnoreCase("above")) {
                        if ((Common.distanceValue) <= Double.parseDouble(postSnapshot.getKey())) {
                            Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                    et_toCity.getText().toString(),
                                    Common.Date,
                                    Common.Time,
                                    Common.CabType,
                                    "" + postSnapshot.getValue(Double.class));
                            Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                            Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                            Common.WayActivity = "One Way";
//                        Common.BookNow = null;
                            startActivity(intent2);
                            break;
                        }
                    } else {
                        Common.tripDetailOneWay = new TripDetailOneWay(et_fromCity.getText().toString(),
                                et_toCity.getText().toString(),
                                Common.Date,
                                Common.Time,
                                Common.CabType,
                                "" + postSnapshot.getValue(Double.class));
                        Log.d("ERRORCheck", "" + postSnapshot.getValue(Double.class));
                        Intent intent2 = new Intent(getContext(), SelectCabActivity.class);
                        Common.WayActivity = "One Way";
                        startActivity(intent2);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    String getMonthForInt(int m) {
        String month = "invalid";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (m >= 0 && m <= 11) {
            month = months[m];
        }
        return month;
    }

}
