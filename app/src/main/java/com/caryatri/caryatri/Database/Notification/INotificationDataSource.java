package com.caryatri.caryatri.Database.Notification;

import java.util.List;

import io.reactivex.Flowable;

public interface INotificationDataSource {
    Flowable<List<NotificationDB>> getNotificationDB();
    int countNotificationItems(String status);
    void emptyNotification();
    void updateNotification();
    void updateCancelByDriver(String phone);
    void updateCancelCab(String id);
    void updateCompleteByDriver(String phone);
    void insertToNotification(NotificationDB... notificationDBS);
    void updateNotificationDB(NotificationDB... notificationDBS);
    void deleteNotificationDBItem(NotificationDB notificationDB);

}
