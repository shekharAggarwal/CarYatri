package com.caryatri.caryatri.Database.OneWay.UserData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDataOneWayDAO {

    @Query("SELECT * FROM UserDataOneWay")
    Flowable<List<UserDataOneWay>> getUserDataOneWayItem();

    @Query("SELECT * FROM UserDataOneWay WHERE id=:UserDataOneWayItemId")
    Flowable<List<UserDataOneWay>> getUserDataOneWayItemById(int UserDataOneWayItemId);

    @Query("SELECT COUNT(*) FROM UserDataOneWay")
    int countCabUserDataOneWay();

//    @Query("SELECT SUM(Price) FROM UserData")
//    float sumPrice();

    @Query("DELETE FROM UserDataOneWay")
    void emptyUserDataOneWay();

    @Insert
    void insertToUserDataOneWay(UserDataOneWay... userDataOneWays);

    @Update
    void updateUserDataOneWay(UserDataOneWay... userDataOneWays);

    @Delete
    void deleteUserDataOneWayItem(UserDataOneWay userDataOneWay);
}
