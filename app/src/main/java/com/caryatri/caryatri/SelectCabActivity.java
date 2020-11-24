package com.caryatri.caryatri;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.adapter.CabImageAdapter;
import com.caryatri.caryatri.model.Cab;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCabActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    MaterialSpinner actVehicle, actModel;
    TextView cabRate;
    TextView txtSitting;
    Button btnAddCar;
    double price = 0;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SwipeRefreshLayout swipeRefreshLayout;
    ICarYatri mService;
    private ViewPager mSliderViewPager;
    private TextView mDotLayout;
    List<Cab> cabsArr;
    List<String> cabImg = new ArrayList<>();
    ArrayList<String> VehicleList = new ArrayList<>(), ModelList = new ArrayList<>();
    ConnectivityReceiver connectivityReceiver;
    Toolbar toolbar;
    RelativeLayout viewImage;
    LinearLayout viewRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Common.setTop(this);
        mService = Common.getAPI();
        findId();
        settingClick();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentUser == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");
                    mService.getUserInfo(email, password).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Common.currentUser = response.body();
                            VehicleList = new ArrayList<>();
                            VehicleList.add("SELECT CAB BRAND");
                            actVehicle.setItems(VehicleList);
                            ModelList = new ArrayList<>();
                            ModelList.add("SELECT CAB BRAND");
                            actModel.setItems(ModelList);
                            txtSitting.setText("");
                            viewRate.setVisibility(View.GONE);
                            mSliderViewPager.removeAllViews();
                            mDotLayout.setText("");
                            getCab();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            VehicleList = new ArrayList<>();
                            VehicleList.add("SELECT CAB BRAND");
                            actVehicle.setItems(VehicleList);
                            ModelList = new ArrayList<>();
                            ModelList.add("SELECT CAB BRAND");
                            actModel.setItems(ModelList);
                            txtSitting.setText("");
                            viewRate.setVisibility(View.GONE);
                            mSliderViewPager.removeAllViews();
                            mDotLayout.setText("");
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(SelectCabActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    VehicleList = new ArrayList<>();
                    VehicleList.add("SELECT CAB BRAND");
                    actVehicle.setItems(VehicleList);
                    ModelList = new ArrayList<>();
                    ModelList.add("SELECT CAB BRAND");
                    actModel.setItems(ModelList);
                    txtSitting.setText("");
                    mSliderViewPager.removeAllViews();
                    viewRate.setVisibility(View.GONE);
                    mDotLayout.setText("");
                    getCab();

                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if (Common.currentUser == null) {
                    String email = Paper.book().read("email");
                    String password = Paper.book().read("password");

                    mService.getUserInfo(email, password).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            VehicleList = new ArrayList<>();
                            VehicleList.add("SELECT CAB BRAND");
                            actVehicle.setItems(VehicleList);
                            ModelList = new ArrayList<>();
                            ModelList.add("SELECT CAB BRAND");
                            actModel.setItems(ModelList);
                            viewRate.setVisibility(View.GONE);
                            txtSitting.setText("");
                            mSliderViewPager.removeAllViews();
                            mDotLayout.setText("");
                            getCab();

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                            VehicleList = new ArrayList<>();
                            VehicleList.add("SELECT CAB BRAND");
                            actVehicle.setItems(VehicleList);
                            ModelList = new ArrayList<>();
                            viewRate.setVisibility(View.GONE);
                            ModelList.add("SELECT CAB BRAND");
                            actModel.setItems(ModelList);
                            txtSitting.setText("");
                            mSliderViewPager.removeAllViews();
                            mDotLayout.setText("");
                            Toast.makeText(SelectCabActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    VehicleList = new ArrayList<>();
                    VehicleList.add("SELECT CAB BRAND");
                    actVehicle.setItems(VehicleList);
                    ModelList = new ArrayList<>();
                    ModelList.add("SELECT CAB BRAND");
                    actModel.setItems(ModelList);
                    viewRate.setVisibility(View.GONE);
                    txtSitting.setText("");
                    mSliderViewPager.removeAllViews();
                    mDotLayout.setText("");
                    getCab();

                }
            }
        });

        actModel.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (!actModel.getText().toString().isEmpty() && !actModel.getText().toString().equals("SELECT CAB MODEL")) {
                    if (cabsArr != null && cabsArr.size() != 0)
                        for (int i3 = 0; i3 < cabsArr.size(); i3++)
                            if (cabsArr.get(i3).getCabModel().equalsIgnoreCase(actModel.getText().toString()) &&
                                    cabsArr.get(i3).getCabBrand().equalsIgnoreCase(actVehicle.getText().toString())) {
                                txtSitting.setText(cabsArr.get(i3).getCabSitting());
                                if (Common.WayActivity.equals("One Way"))
                                    price = Double.parseDouble(Common.tripDetailOneWay.getCabRate());
                                else
                                    price = Double.parseDouble(Common.tripDetailRoundWay.getCabRate());
                                viewRate.setVisibility(View.VISIBLE);
                                cabRate.setText("" + price);
                                cabImg = new Gson().fromJson(cabsArr.get(i3).getCabImage(),
                                        new TypeToken<List<String>>() {
                                        }.getType());
                                if (cabImg != null) {
                                    CabImageAdapter cabImageAdapter = new CabImageAdapter(SelectCabActivity.this, cabImg);
                                    mSliderViewPager.setAdapter(cabImageAdapter);
                                    addDotsIndicator(0);
                                    mSliderViewPager.addOnPageChangeListener(viewListener);
                                }
                                break;
                            }
                } else {
                    txtSitting.setText("");
                    price = 0;
                    mDotLayout.setText("");
                    viewRate.setVisibility(View.GONE);
                    mSliderViewPager.removeAllViews();
                }
            }
        });


        actVehicle.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (actVehicle.getText().toString().equalsIgnoreCase("SELECT CAB BRAND")) {
                    ModelList = new ArrayList<>();
                    actModel.setItems("");
                    txtSitting.setText("");
                    price = 0;
                    cabRate.setText("");
                    viewRate.setVisibility(View.GONE);
                    mDotLayout.setText("");
                    mSliderViewPager.removeAllViews();
                } else {
                    ModelList = new ArrayList<>();
                    actModel.setItems("");
                    txtSitting.setText("");
                    price = 0;
                    viewRate.setVisibility(View.GONE);
                    mDotLayout.setText("");
                    mSliderViewPager.removeAllViews();
                    Log.d("Error", actVehicle.getText().toString());
                    ModelList.add("SELECT CAB MODEL");
                    if (cabsArr != null && cabsArr.size() != 0) {
                        for (int i = 0; i < cabsArr.size(); i++) {
                            boolean isChecked = false;
                            if (cabsArr.get(i).getCabBrand().equalsIgnoreCase(actVehicle.getText().toString())) {
                                isChecked = true;
                            } else {
                                isChecked = false;
                            }
                            if (isChecked)
                                ModelList.add(cabsArr.get(i).getCabModel());
                        }
                        Log.d("ERROR", new Gson().toJson(ModelList));
                        ArrayList<String> check = new ArrayList<>();
                        for (int s = 0; s < ModelList.size(); s++) {
                            boolean isChecked = false;
                            if (check.size() != 0) {
                                for (int p = 0; p < check.size(); p++) {
                                    if (check.get(p).contains(ModelList.get(s))) {
                                        isChecked = false;
//                                    break;
                                    } else {
                                        isChecked = true;
                                    }
                                }
                                if (isChecked)
                                    check.add(ModelList.get(s));
                            } else
                                check.add(ModelList.get(s));
                        }
                        ModelList = check;
                        actModel.setItems(ModelList);
                    }
                }
            }
        });

    }

    private void settingClick() {

        actModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    setClickListener(view.getId());
            }
        });
        txtSitting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickListener(view.getId());
            }
        });
        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickListener(view.getId());
            }
        });

    }

    private void findId() {
        actVehicle = findViewById(R.id.actVehicle);
        cabRate = findViewById(R.id.cabRate);
        actModel = findViewById(R.id.actModel);
        viewRate = findViewById(R.id.viewRate);
        txtSitting = findViewById(R.id.txtSitting);
        btnAddCar = findViewById(R.id.btnAddCar);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        viewImage = findViewById(R.id.viewImage);
        mDotLayout = findViewById(R.id.linearLayout);
        mSliderViewPager = findViewById(R.id.slideViewPage);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setClickListener(int id) {
        switch (id) {
            case R.id.actModel:
                if (actVehicle.getText().toString().isEmpty() || actVehicle.getText().toString().equals("SELECT CAB BRAND")) {
                    Toast.makeText(this, "Enter Brand", Toast.LENGTH_SHORT).show();
                    actVehicle.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(actVehicle, InputMethodManager.SHOW_IMPLICIT);
                    return;
                }
                return;
            case R.id.btnAddCar:
                if (actVehicle.getText().toString().isEmpty()) {
                    actVehicle.setError("Fill Brand");
                    actVehicle.requestFocus();
                    return;
                }

                boolean check = false;

                for (int i = 0; i < cabsArr.size(); i++) {
                    if (cabsArr.get(i).getCabBrand().equalsIgnoreCase(actVehicle.getText().toString())) {
                        check = false;
                        break;
                    } else
                        check = true;
                }

                if (check) {
                    actVehicle.setError("Brand Not Exists");
                    actVehicle.requestFocus();
                    return;
                }

                if (actModel.getText().toString().isEmpty() || actModel.getText().toString().equals("SELECT CAB MODEL")) {
                    actModel.setError("Fill Model");
                    actModel.requestFocus();
                    return;
                }

                boolean checkModel = false;

                for (int i = 0; i < cabsArr.size(); i++) {
                    if (cabsArr.get(i).getCabModel().equalsIgnoreCase(actModel.getText().toString()) &&
                            cabsArr.get(i).getCabBrand().equalsIgnoreCase(actVehicle.getText().toString())) {
                        checkModel = false;
                        break;
                    } else
                        checkModel = true;
                }

                if (checkModel) {
                    actModel.setError("Model Not Exists");
                    actModel.requestFocus();
                    return;
                }

                if (Common.WayActivity.equals("One Way")) {
                    CabOneWay cab = new CabOneWay();
                    cab.cabStatus = "ok";
                    cab.cabBrand = actVehicle.getText().toString();
                    cab.cabModel = actModel.getText().toString();
                    cab.cabImage = new Gson().toJson(cabImg);
                    cab.cabPrice = price;
                    cab.cabType = Common.CabType;
                    cab.cabSitting = Integer.parseInt(txtSitting.getText().toString());
                    Common.cabOneWayRepository.insertToCabOneWay(cab);
                } else if (Common.WayActivity.equals("Round Way")) {
                    CabRoundWay cab = new CabRoundWay();
                    cab.cabStatus = "ok";
                    cab.cabBrand = actVehicle.getText().toString();
                    cab.cabModel = actModel.getText().toString();
                    cab.cabPrice = price;
                    cab.cabType = Common.CabType;
                    cab.cabImage = new Gson().toJson(cabImg);
                    cab.cabSitting = Integer.parseInt(txtSitting.getText().toString());
                    Common.cabRoundWayRepository.insertToCabRoundWay(cab);
                }
                startActivity(new Intent(SelectCabActivity.this, BookCabActivity.class));
                break;
            default:
                return;
        }
    }

    private void getCab() {
        compositeDisposable.add(mService.getCab(Common.CabType, Common.tripDetailOneWay.getFrom().toUpperCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Cab>>() {
                    @Override
                    public void accept(List<Cab> cabs) throws Exception {
                        Log.d("ERROR", new Gson().toJson(cabs));
                        if (cabs != null)
                            if (cabs.size() != 0) {
                                cabsArr = cabs;
                                VehicleList = new ArrayList<>();
                                VehicleList.add("SELECT CAB BRAND");
                                for (int i = 0; i < cabs.size(); i++) {
                                    boolean isChecked = false;
                                    if (VehicleList.size() != 0) {
                                        for (int p = 0; p < VehicleList.size(); p++) {
                                            if (VehicleList.get(p).equals(cabs.get(i).getCabBrand())) {
                                                isChecked = false;
                                                break;
                                            } else
                                                isChecked = true;
                                        }
                                        if (isChecked)
                                            VehicleList.add(cabs.get(i).getCabBrand());
                                    } else
                                        VehicleList.add(cabs.get(i).getCabBrand());
                                }
                                actVehicle.setItems(VehicleList);
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(SelectCabActivity.this, "No cab available for your source place!", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        else {
                            Toast.makeText(SelectCabActivity.this, "Try again to book cab", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        VehicleList = new ArrayList<>();
                        VehicleList.add("SELECT CAB BRAND");
                        actVehicle.setItems(VehicleList);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("ERROR", throwable.getMessage());
                    }
                }));
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
        mDotLayout.setVisibility(View.VISIBLE);
        mDotLayout.setText("" + (position + 1) + "/" + cabImg.size());
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
