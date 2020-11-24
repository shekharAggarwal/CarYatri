package com.caryatri.caryatri.Database.DataSource.RoundWay.UserData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWay;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDataRoundWayDAO {

    @Query("SELECT * FROM UserDataRoundWay")
    Flowable<List<UserDataRoundWay>> getUserDataRoundWayItem();

    @Query("SELECT * FROM UserDataRoundWay WHERE id=:UserDataRoundWayItemId")
    Flowable<List<UserDataRoundWay>> getUserDataRoundWayItemById(int UserDataRoundWayItemId);

    @Query("SELECT COUNT(*) FROM UserDataRoundWay")
    int countUserDataRoundWay();

//    @Query("SELECT SUM(Price) FROM UserData")
//    float sumPrice();

    @Query("DELETE FROM UserDataOneWay")
    void emptyUserDataRoundWay();


    @Insert
    void insertToUserDataRoundWay(UserDataRoundWay... userDataRoundWays);

    @Update
    void updateUserDataRoundWay(UserDataRoundWay... userDataRoundWays);

    @Delete
    void deleteUserDataRoundWay(UserDataRoundWay userDataRoundWay);
}
