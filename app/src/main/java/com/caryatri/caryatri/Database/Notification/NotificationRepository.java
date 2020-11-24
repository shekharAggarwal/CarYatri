package com.caryatri.caryatri.Database.Notification;

import java.util.List;

import io.reactivex.Flowable;

public class NotificationRepository implements INotificationDataSource {

    private INotificationDataSource notificationDataSource;
    private static NotificationRepository instance;

    private NotificationRepository(INotificationDataSource notificationDataSource) {
        this.notificationDataSource = notificationDataSource;
    }

    public static NotificationRepository getInstance(INotificationDataSource notificationDataSource) {
        if (instance == null)
            instance = new NotificationRepository(notificationDataSource);
        return instance;
    }


    @Override
    public Flowable<List<NotificationDB>> getNotificationDB() {
        return notificationDataSource.getNotificationDB();
    }

    @Override
    public int countNotificationItems(String status) {
        return notificationDataSource.countNotificationItems(status);
    }

    @Override
    public void emptyNotification() {
        notificationDataSource.emptyNotification();
    }

    @Override
    public void updateNotification() {
        notificationDataSource.updateNotification();
    }

    @Override
    public void updateCancelByDriver(String phone) {
        notificationDataSource.updateCancelByDriver(phone);
    }


    @Override
    public void updateCancelCab(String id) {
        notificationDataSource.updateCancelCab(id);
    }

    @Override
    public void updateCompleteByDriver(String phone) {
        notificationDataSource.updateCompleteByDriver(phone);
    }

    @Override
    public void insertToNotification(NotificationDB... notificationDBS) {
        notificationDataSource.insertToNotification(notificationDBS);
    }

    @Override
    public void updateNotificationDB(NotificationDB... notificationDBS) {
        notificationDataSource.updateNotificationDB(notificationDBS);
    }

    @Override
    public void deleteNotificationDBItem(NotificationDB notificationDB) {
        notificationDataSource.deleteNotificationDBItem(notificationDB);
    }
}
