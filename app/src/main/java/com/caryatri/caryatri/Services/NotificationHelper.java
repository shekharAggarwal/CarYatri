package com.caryatri.caryatri.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.caryatri.caryatri.R;

public class NotificationHelper extends ContextWrapper {

    private static final String SHOP_CHANNEL_ID = "com.caryatri.caryatri.CarYatri";
    private static final String SHOP_CHANNEL_NAME = "CarYatri";

    private NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel shopChannel = new NotificationChannel(SHOP_CHANNEL_ID, SHOP_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        shopChannel.enableLights(false);
        shopChannel.enableVibration(true);
        shopChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(shopChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;

    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getCarYatriNotificationWithout(String title,
                                                               String message,
                                                               Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), SHOP_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(true);

    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getCarYatriNotificationWith(String title,
                                                            String message,
                                                            Uri soundUri, PendingIntent pendingIntent) {
        return new Notification.Builder(getApplicationContext(), SHOP_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true);

    }
}
