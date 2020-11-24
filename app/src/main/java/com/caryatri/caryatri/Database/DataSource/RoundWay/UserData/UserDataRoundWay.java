package com.caryatri.caryatri.Database.DataSource.RoundWay.UserData;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserDataRoundWay")
public class UserDataRoundWay {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "source")
    public String source;

    @ColumnInfo(name = "destination")
    public String destination;

//    @ColumnInfo(name = "image")
//    public String image;

    @ColumnInfo(name = "pickupDate")
    public String pickupDate;

    @ColumnInfo(name = "dropDate")
    public String dropDate;

    @ColumnInfo(name = "pickupTime")
    public String pickupTime;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "phoneNumber")
    public int phoneNumber;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "sourceAddress")
    public String sourceAddress;

    @ColumnInfo(name = "destinationAddress")
    public String destinationAddress;
}
