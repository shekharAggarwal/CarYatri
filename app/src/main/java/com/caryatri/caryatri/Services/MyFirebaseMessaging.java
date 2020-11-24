package com.caryatri.caryatri.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDataSource;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayDataSource;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayRepository;
import com.caryatri.caryatri.Database.Local.SNRoomDatabase;
import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.Database.Notification.NotificationDataSource;
import com.caryatri.caryatri.Database.Notification.NotificationRepository;
import com.caryatri.caryatri.Database.OneWay.CabOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.CabOneWayRepository;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayDataSource;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayRepository;
import com.caryatri.caryatri.InvoiceActivity;
import com.caryatri.caryatri.MainActivity;
import com.caryatri.caryatri.MapDriver;
import com.caryatri.caryatri.R;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if (Common.currentUser != null)
            updateTokenToServer(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            initDB();
            Map<String, String> data = remoteMessage.getData();
            Log.d("ERRORRR", new Gson().toJson(data));
            if (!data.get("title").equals("Ride Status ok")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    sendNotificationAPI26(remoteMessage);
                else
                    sendNotification(remoteMessage);
            } else {
                Common.notificationRepository.updateCompleteByDriver(remoteMessage.getData().get("Phone1"));
                LocalBroadcastManager.getInstance(MyFirebaseMessaging.this).sendBroadcast(new Intent(Common.OK));
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        String code = data.get("code");
        String MapStatus = data.get("MapStatus");
        String Phone = data.get("Phone");
        String Phone1 = data.get("Phone1");
        String Phone2 = data.get("Phone2");
        final String Phone3 = data.get("Phone3");

        assert title != null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
        r.play();
        NotificationCompat.Builder builder;
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 1, new Intent(getBaseContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setFullScreenIntent(fullScreenPendingIntent, true);


        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert noti != null;
        noti.notify(new Random().nextInt(), builder.build());

        NotificationDB notificationDB = new NotificationDB();
        notificationDB.code = "0";
        notificationDB.notificationData = message;
        notificationDB.phone = Phone;
        notificationDB.status = "incomplete";
        notificationDB.cabCancelStatus = "false";
        Common.notificationRepository.insertToNotification(notificationDB);

        if (Phone != null) {
            CurrentDriverDB currentDriverDB = new CurrentDriverDB();
            currentDriverDB.DriverPhone = Phone;
            currentDriverDB.TripStatus = "Accepted";
            currentDriverDB.CabDetails = null;
            Common.currentDriverRepository.insertToCurrentDriver(currentDriverDB);

        }

        if (code != null && code.equals("invoice")) {
            String id = data.get("id");
            Common.id = id;
            Log.d("ERROR", id);
            Common.currentDriverRepository.updateInvoiceCurrentDriverDB(Phone3);
            Common.getAPI().getTrip(id).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    Common.currentDriverRepository.updateCabDetail(new Gson().toJson(response.body()), Phone3);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });
            Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (MapStatus != null) {
            Common.currentDriverRepository.updateCurrentDriverDB(Phone2);
            startActivity(new Intent(getBaseContext(), MapDriver.class));
        }

        if (Phone1 != null) {
            if (title.equalsIgnoreCase("Trip Cancelled By Driver")) {
                Common.currentDriverRepository.deleteCurrentDriverDB(Phone1);
                Common.notificationRepository.updateCancelByDriver(Phone1);
            }
        }
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        String code = data.get("code");
        String MapStatus = data.get("MapStatus");
        String Phone = data.get("Phone");
        String Phone1 = data.get("Phone1");
        String Phone2 = data.get("Phone2");
        final String Phone3 = data.get("Phone3");

        NotificationHelper helper;
        Notification.Builder builder;

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 1, new Intent(getBaseContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
        r.play();
        helper = new NotificationHelper(this);
        builder = helper.getCarYatriNotificationWith(title, message, defaultSoundUri, fullScreenPendingIntent);
        helper.getManager().notify(new Random().nextInt(), builder.build());

        NotificationDB notificationDB = new NotificationDB();
        notificationDB.code = "0";
        notificationDB.notificationData = message;
        notificationDB.phone = Phone;
        notificationDB.status = "incomplete";
        notificationDB.cabCancelStatus = "false";

        Common.notificationRepository.insertToNotification(notificationDB);

        if (Phone != null) {
            CurrentDriverDB currentDriverDB = new CurrentDriverDB();
            currentDriverDB.DriverPhone = Phone;
            currentDriverDB.TripStatus = "Accepted";
            currentDriverDB.CabDetails = null;
            Common.currentDriverRepository.insertToCurrentDriver(currentDriverDB);
        }

        if (code != null && code.equals("invoice")) {
            String id = data.get("id");
            Common.id = id;
            Common.currentDriverRepository.updateInvoiceCurrentDriverDB(Phone3);
            Common.getAPI().getTrip(id).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    Common.currentDriverRepository.updateCabDetail(new Gson().toJson(response.body()), Phone3);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });
            Intent intent = new Intent(getBaseContext(), InvoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (MapStatus != null) {
            Common.currentDriverRepository.updateCurrentDriverDB(Phone2);
            startActivity(new Intent(getBaseContext(), MapDriver.class));
        }

        if (Phone1 != null) {
            if (title.equalsIgnoreCase("Trip Cancelled By Driver")) {
                Common.currentDriverRepository.deleteCurrentDriverDB(Phone1);
                Common.notificationRepository.updateCancelByDriver(Phone1);
            }
        }
    }

    private void updateTokenToServer(String token) {
        ICarYatri mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(),
                token,
                "0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("DEBUG", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("DEBUG", t.getMessage());

                    }
                });

    }

    private void initDB() {
        Common.snRoomDatabase = SNRoomDatabase.getInstance(this);
        Common.cabOneWayRepository = CabOneWayRepository.getInstance(CabOneWayDataSource.getInstance(Common.snRoomDatabase.cabOneWayDAO()));
        Common.cabRoundWayRepository = CabRoundWayRepository.getInstance(CabRoundWayDataSource.getInstance(Common.snRoomDatabase.cabRoundWayDAO()));
        Common.notificationRepository = NotificationRepository.getInstance(NotificationDataSource.getInstance(Common.snRoomDatabase.notificationDAO()));
        Common.userDataOneWayRepository = UserDataOneWayRepository.getInstance(UserDataOneWayDataSource.getInstance(Common.snRoomDatabase.userDataOneWayDAO()));
        Common.userDataRoundWayRepository = UserDataRoundWayRepository.getInstance(UserDataRoundWayDataSource.getInstance(Common.snRoomDatabase.userDataRoundWayDAO()));
        Common.currentDriverRepository = CurrentDriverRepository.getInstance(CurrentDriverDataSource.getInstance(Common.snRoomDatabase.currentDriverDBDAO()));
    }

}
