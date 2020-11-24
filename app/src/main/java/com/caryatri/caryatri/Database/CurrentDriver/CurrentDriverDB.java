package com.caryatri.caryatri.Database.CurrentDriver;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CurrentDriverDB")
public class CurrentDriverDB {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    public int Id;

    @ColumnInfo(name = "DriverPhone")
    public String DriverPhone;

    @ColumnInfo(name = "CabDetails")
    public String CabDetails;

    @ColumnInfo(name = "TripStatus")
    public String TripStatus;
}

