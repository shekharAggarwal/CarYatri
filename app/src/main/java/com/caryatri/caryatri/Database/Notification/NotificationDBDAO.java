package com.caryatri.caryatri.Database.Notification;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface NotificationDBDAO {

    @Query("SELECT * FROM NotificationDB ORDER BY id DESC")
    Flowable<List<NotificationDB>> getNotificationDB();

    @Query("SELECT COUNT(*) FROM NotificationDB WHERE status=:status")
    int countNotificationItems(String status);

    @Query("DELETE FROM NotificationDB")
    void emptyNotification();

    @Query("UPDATE NotificationDB SET status='ok' WHERE status='incomplete'")
    void updateNotification();

    @Query("UPDATE NotificationDB SET cabCancelStatus='true' WHERE phone=:phone AND cabCancelStatus='false'")
    void updateCancelByDriver(String phone);

    @Query("UPDATE NotificationDB SET cabCancelStatus='true1' WHERE phone=:phone AND cabCancelStatus='false'")
    void updateCompleteByDriver(String phone);

    @Query("UPDATE NotificationDB SET cabCancelStatus='true' WHERE id=:id")
    void updateCancelCab(String id);

    @Insert
    void insertToNotification(NotificationDB... notificationDBS);

    @Update
    void updateNotificationDB(NotificationDB... notificationDBS);

    @Delete
    void deleteNotificationDBItem(NotificationDB notificationDB);
}
