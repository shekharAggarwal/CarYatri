package com.caryatri.caryatri.Database.CurrentDriver;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CurrentDriverDBDAO {

    @Query("SELECT * FROM CurrentDriverDB ORDER BY id DESC")
    Flowable<List<CurrentDriverDB>> getCurrentDriverDB();

    @Query("SELECT * FROM CurrentDriverDB WHERE TripStatus='Invoice'")
    Flowable<List<CurrentDriverDB>> getInvoice();

    @Query("SELECT DriverPhone FROM CurrentDriverDB WHERE TripStatus=:status")
    Flowable<List<String>> getPhone(String status);

    @Query("UPDATE CurrentDriverDB SET TripStatus='Start' WHERE TripStatus='Accepted' AND DriverPhone=:driverPhone")
    void updateCurrentDriverDB(String driverPhone);

    @Query("UPDATE CurrentDriverDB SET TripStatus='Invoice' WHERE TripStatus='Start' AND DriverPhone=:driverPhone")
    void updateInvoiceCurrentDriverDB(String driverPhone);

    @Query("DELETE FROM CurrentDriverDB WHERE TripStatus='Accepted' AND DriverPhone=:driverPhone")
    void deleteCurrentDriverDB(String driverPhone);

    @Query("DELETE FROM CurrentDriverDB WHERE TripStatus='Invoice' AND DriverPhone=:driverPhone")
    void deleteCompleteCurrentDriverDB(String driverPhone);

    @Query("DELETE FROM CurrentDriverDB")
    void emptyCurrentDriver();

    @Query("SELECT COUNT(*) FROM CurrentDriverDB WHERE TripStatus='Accepted'")
    int getCurrentDriverDBAccepted();

    @Query("SELECT COUNT(*) FROM CurrentDriverDB WHERE TripStatus='Start'")
    int getCurrentDriverDBStart();

    @Query("UPDATE CurrentDriverDB SET CabDetails=:cabDetails WHERE DriverPhone=:driverPhone")
    void updateCabDetail(String cabDetails, String driverPhone);

    @Insert
    void insertToCurrentDriver(CurrentDriverDB... currentDriverDBS);

    @Update
    void updateCurrentDriverDB(CurrentDriverDB... currentDriverDBS);

    @Delete
    void deleteCurrentDriverDBItem(CurrentDriverDB currentDriverDB);
}
