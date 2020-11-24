package com.caryatri.caryatri.Database.Notification;

import java.util.List;

import io.reactivex.Flowable;

public class NotificationDataSource implements INotificationDataSource {


    private NotificationDBDAO notificationDBDAO;
    private static NotificationDataSource instance;

    public NotificationDataSource(NotificationDBDAO notificationDBDAO) {
        this.notificationDBDAO = notificationDBDAO;
    }

    public static NotificationDataSource getInstance(NotificationDBDAO notificationDBDAO) {
        if (instance == null)
            instance = new NotificationDataSource(notificationDBDAO);
        return instance;
    }

    @Override
    public Flowable<List<NotificationDB>> getNotificationDB() {
        return notificationDBDAO.getNotificationDB();
    }

    @Override
    public int countNotificationItems(String status) {
        return notificationDBDAO.countNotificationItems(status);
    }

    @Override
    public void emptyNotification() {
        notificationDBDAO.emptyNotification();
    }

    @Override
    public void updateNotification() {
        notificationDBDAO.updateNotification();
    }

    @Override
    public void updateCancelByDriver(String phone) {
        notificationDBDAO.updateCancelByDriver(phone);
    }

    @Override
    public void updateCancelCab(String id) {
        notificationDBDAO.updateCancelCab(id);
    }

    @Override
    public void updateCompleteByDriver(String phone) {
        notificationDBDAO.updateCompleteByDriver(phone);
    }

    @Override
    public void insertToNotification(NotificationDB... notificationDBS) {
        notificationDBDAO.insertToNotification(notificationDBS);
    }

    @Override
    public void updateNotificationDB(NotificationDB... notificationDBS) {
        notificationDBDAO.updateNotificationDB(notificationDBS);
    }

    @Override
    public void deleteNotificationDBItem(NotificationDB notificationDB) {
        notificationDBDAO.deleteNotificationDBItem(notificationDB);
    }
}
