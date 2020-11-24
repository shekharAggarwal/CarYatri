package com.caryatri.caryatri.Database.OneWay;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CabOneWayDAO {
    @Query("SELECT * FROM CabOneWay")
    Flowable<List<CabOneWay>> getCabOneWayItem();

    @Query("SELECT * FROM CabOneWay WHERE id=:CabOneWayItemId")
    Flowable<List<CabOneWay>> getCabOneWayItemById(int CabOneWayItemId);

    @Query("SELECT COUNT(*) FROM CabOneWay")
    int countCabOneWayItems();

    @Query("DELETE FROM CabOneWay")
    void emptyCabOneWay();

    @Query("UPDATE CabOneWay SET cabFare=:price WHERE id =:id")
    void UpdateCabOneWay(String price ,int id);

    @Query("DELETE FROM CabOneWay WHERE cabStatus='incomplete'")
    void deleteInComplete();

    @Query("UPDATE CabOneWay SET cabStatus='incomplete' WHERE cabStatus='ok'")
    void updateInComplete();

    @Query("SELECT COUNT(*) FROM CabOneWay WHERE cabStatus=:status")
    int CountCabOneWay(String status);

    @Query("SELECT * FROM CabOneWay WHERE cabStatus=:status")
    Flowable<List<CabOneWay>> getCabOneWayItemByStatus(String status);

    @Insert
    void insertToCabOneWay(CabOneWay... cabOneWays);

    @Update
    void updateCabOneWay(CabOneWay... cabOneWays);

    @Delete
    void deleteCabOneWayItem(CabOneWay cabOneWay);
}
