package com.caryatri.caryatri.Database.OneWay;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CabOneWay")
public class CabOneWay {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "cabStatus")
    public String cabStatus;

    @ColumnInfo(name = "cabBrand")
    public String cabBrand;

    @ColumnInfo(name = "cabModel")
    public String cabModel;

    @ColumnInfo(name = "cabSitting")
    public int cabSitting;

    @ColumnInfo(name = "cabImage")
    public String cabImage;

    @ColumnInfo(name = "CabType")
    public String cabType;

    @ColumnInfo(name = "cabPrice")
    public double cabPrice;

    @ColumnInfo(name = "cabFare")
    public double cabFare;
}
