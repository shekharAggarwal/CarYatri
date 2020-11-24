package com.caryatri.caryatri;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Common.RecyclerItemTouchHelper;
import com.caryatri.caryatri.Common.RecyclerItemTouchHelperListener;
import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.Network.ConnectivityReceiver;
import com.caryatri.caryatri.Network.MyApplication;
import com.caryatri.caryatri.adapter.NotificationAdapter;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.android.material.snackbar.Snackbar;

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

public class NotificationActivity extends CrashActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        RecyclerItemTouchHelperListener {

    ConnectivityReceiver connectivityReceiver;
    CompositeDisposable compositeDisposable;
    Toolbar toolbar;
    RecyclerView recycler_notification;
    LinearLayout ln2, ln1;
    TextView clearAll;
    LinearLayout root_layout;
    List<NotificationDB> list = new ArrayList<>();
    NotificationAdapter notificationAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ICarYatri mService = Common.getAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Common.setTop(this);

        compositeDisposable = new CompositeDisposable();
        recycler_notification = findViewById(R.id.recycler_notification);
        recycler_notification.setLayoutManager(new LinearLayoutManager(this));
        recycler_notification.setHasFixedSize(true);
        ln1 = findViewById(R.id.ln1);
        ln2 = findViewById(R.id.ln2);
        root_layout = findViewById(R.id.root_layout);
        clearAll = findViewById(R.id.clear_all);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
                            compositeDisposable.add(Common.notificationRepository.getNotificationDB()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<NotificationDB>>() {
                                        @Override
                                        public void accept(List<NotificationDB> notificationDBS) throws Exception {
                                            if (notificationDBS.size() == 0) {
                                                ln1.setVisibility(View.GONE);
                                                ln2.setVisibility(View.VISIBLE);
                                                swipeRefreshLayout.setRefreshing(false);
                                            } else {
                                                ln1.setVisibility(View.VISIBLE);
                                                ln2.setVisibility(View.GONE);
                                                list = notificationDBS;
                                                loadNotification(notificationDBS);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            ln1.setVisibility(View.VISIBLE);
                                            ln2.setVisibility(View.GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            Log.d("ERROR", throwable.getMessage());
                                        }
                                    }));

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    compositeDisposable.add(Common.notificationRepository.getNotificationDB()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<List<NotificationDB>>() {
                                @Override
                                public void accept(List<NotificationDB> notificationDBS) throws Exception {
                                    if (notificationDBS.size() == 0) {
                                        ln1.setVisibility(View.GONE);
                                        ln2.setVisibility(View.VISIBLE);
                                        swipeRefreshLayout.setRefreshing(false);
                                    } else {
                                        ln1.setVisibility(View.VISIBLE);
                                        ln2.setVisibility(View.GONE);
                                        list = notificationDBS;
                                        loadNotification(notificationDBS);
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    ln1.setVisibility(View.VISIBLE);
                                    ln2.setVisibility(View.GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    Log.d("ERROR", throwable.getMessage());
                                }
                            }));

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
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            compositeDisposable.add(Common.notificationRepository.getNotificationDB()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<NotificationDB>>() {
                                        @Override
                                        public void accept(List<NotificationDB> notificationDBS) throws Exception {
                                            if (notificationDBS.size() == 0) {
                                                ln1.setVisibility(View.GONE);
                                                ln2.setVisibility(View.VISIBLE);
                                                swipeRefreshLayout.setRefreshing(false);
                                            } else {
                                                ln1.setVisibility(View.VISIBLE);
                                                ln2.setVisibility(View.GONE);
                                                list = notificationDBS;
                                                loadNotification(notificationDBS);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            ln1.setVisibility(View.VISIBLE);
                                            ln2.setVisibility(View.GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            Log.d("ERROR", throwable.getMessage());
                                        }
                                    }));
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("ERROR", t.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    compositeDisposable.add(Common.notificationRepository.getNotificationDB()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<List<NotificationDB>>() {
                                @Override
                                public void accept(List<NotificationDB> notificationDBS) throws Exception {
                                    if (notificationDBS.size() == 0) {
                                        ln1.setVisibility(View.GONE);
                                        ln2.setVisibility(View.VISIBLE);
                                        swipeRefreshLayout.setRefreshing(false);
                                    } else {
                                        ln1.setVisibility(View.VISIBLE);
                                        ln2.setVisibility(View.GONE);
                                        list = notificationDBS;
                                        swipeRefreshLayout.setRefreshing(false);
                                        loadNotification(notificationDBS);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    ln1.setVisibility(View.VISIBLE);
                                    ln2.setVisibility(View.GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    Log.d("ERROR", throwable.getMessage());
                                }
                            }));
                }

            }
        });

        if (Common.notificationRepository != null)
            Common.notificationRepository.updateNotification();
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_notification);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.notificationRepository.countNotificationItems("ok") != 0) {
                    Common.notificationRepository.emptyNotification();
                }
            }
        });

    }

    private void loadNotification(List<NotificationDB> notificationDBS) {
        notificationAdapter = new NotificationAdapter(this, notificationDBS, getSupportFragmentManager());
        recycler_notification.setAdapter(notificationAdapter);

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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotificationAdapter.ViewHolder) {
            String name = list.get(viewHolder.getAdapterPosition()).notificationData;

            final NotificationDB deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            //deleteItem
            notificationAdapter.removeItem(deleteIndex);
            Common.notificationRepository.deleteNotificationDBItem(deletedItem);

            Snackbar snackbar = Snackbar.make(root_layout, new StringBuilder(name).append("Remove from favorite list").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationAdapter.restoreItem(deletedItem, deleteIndex);
                    Common.notificationRepository.insertToNotification(deletedItem);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
