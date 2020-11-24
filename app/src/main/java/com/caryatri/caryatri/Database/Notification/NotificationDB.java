package com.caryatri.caryatri.Database.Notification;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotificationDB")
public class NotificationDB {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "cabCancelStatus")
    public String cabCancelStatus;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "notificationData")
    public String notificationData;
}
