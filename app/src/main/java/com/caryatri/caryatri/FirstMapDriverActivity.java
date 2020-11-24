package com.caryatri.caryatri;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.adapter.TripMapAdapter;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;

import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstMapDriverActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recycler_current_Driver;
    ICarYatri mService = Common.getAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_map_driver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Common.setTop(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recycler_current_Driver = findViewById(R.id.recycler_current_Driver);
        recycler_current_Driver.setLayoutManager(new LinearLayoutManager(this));
        recycler_current_Driver.setHasFixedSize(true);

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);


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
                            if (response.body() != null) {
                                Common.currentUser = response.body();
                                loadList();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    loadList();
                }

            }
        });

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
                            if (response.body() != null) {
                                Common.currentUser = response.body();
                                loadList();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    loadList();
                }
            }
        });
    }

    private void loadList() {
        compositeDisposable.add(Common.currentDriverRepository.getCurrentDriverDB()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<CurrentDriverDB>>() {
                    @Override
                    public void accept(List<CurrentDriverDB> currentDriverDBS) throws Exception {
                        swipeRefreshLayout.setRefreshing(false);
                        TripMapAdapter tripMapAdapter = new TripMapAdapter(FirstMapDriverActivity.this, currentDriverDBS);
                        recycler_current_Driver.setAdapter(tripMapAdapter);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("ERROR", throwable.getMessage());
                    }
                }));
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
