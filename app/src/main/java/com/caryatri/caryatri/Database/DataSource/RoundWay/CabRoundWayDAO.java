package com.caryatri.caryatri.Database.DataSource.RoundWay;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CabRoundWayDAO {
    @Query("SELECT * FROM CabRoundWay")
    Flowable<List<CabRoundWay>> getCabRoundWayItem();

    @Query("SELECT * FROM CabRoundWay WHERE id=:CabRoundWayItemId")
    Flowable<List<CabRoundWay>> getCabRoundWayItemById(int CabRoundWayItemId);

    @Query("SELECT COUNT(*) FROM CabRoundWay")
    int countCabRoundWayItems();

//    @Query("SELECT SUM(Price) FROM UserData")
//    float sumPrice();

    @Query("DELETE FROM CabRoundWay")
    void emptyCabRoundWay();

    @Query("UPDATE CabRoundWay SET cabFare=:price WHERE id =:id")
    void UpdateCabRoundWay(String price ,int id);

    @Query("DELETE FROM CabRoundWay WHERE cabStatus='incomplete'")
    void deleteInComplete();

    @Query("UPDATE CabRoundWay SET cabStatus='incomplete' WHERE cabStatus='ok'")
    void updateInComplete();

    @Query("SELECT COUNT(*) FROM CabRoundWay WHERE cabStatus=:status")
    int CountCabRoundWay(String status);

    @Query("SELECT * FROM CabRoundWay WHERE cabStatus=:status")
    Flowable<List<CabRoundWay>> getCabRoundWayItemByStatus(String status);

    @Insert
    void insertToCabRoundWay(CabRoundWay... cabRoundWays);

    @Update
    void updateCabRoundWay(CabRoundWay... cabRoundWays);

    @Delete
    void deleteCabRoundWayItem(CabRoundWay cabRoundWay);
}
