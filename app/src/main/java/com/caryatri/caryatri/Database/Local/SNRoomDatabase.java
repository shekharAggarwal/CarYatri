package com.caryatri.caryatri.Database.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDB;
import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverDBDAO;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWay;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayDAO;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWay;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayDAO;
import com.caryatri.caryatri.Database.Notification.NotificationDB;
import com.caryatri.caryatri.Database.Notification.NotificationDBDAO;
import com.caryatri.caryatri.Database.OneWay.CabOneWay;
import com.caryatri.caryatri.Database.OneWay.CabOneWayDAO;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWay;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayDAO;

@Database(entities = {UserDataOneWay.class, CabOneWay.class, UserDataRoundWay.class, CabRoundWay.class, NotificationDB.class, CurrentDriverDB.class}, version = 2, exportSchema = false)
public abstract class SNRoomDatabase extends RoomDatabase {

    public abstract UserDataOneWayDAO userDataOneWayDAO();

    public abstract CabOneWayDAO cabOneWayDAO();

    public abstract UserDataRoundWayDAO userDataRoundWayDAO();

    public abstract CabRoundWayDAO cabRoundWayDAO();

    public abstract NotificationDBDAO notificationDAO();

    public abstract CurrentDriverDBDAO currentDriverDBDAO();

    private static SNRoomDatabase instance;

    public static SNRoomDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, SNRoomDatabase.class, "CarYatriData")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }


}
