package com.caryatri.caryatri.Database.DataSource.RoundWay.UserData;

import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWay;

import java.util.List;

import io.reactivex.Flowable;

public interface IUserDatRoundWayDataSource {
    Flowable<List<UserDataRoundWay>> getUserDataRoundWayItem();
    Flowable<List<UserDataRoundWay>> getUserDataRoundWayItemById(int UserDataRoundWayItemId);
    int countUserDataRoundWay();
    void emptyUserDataRoundWay();
    void insertToUserDataRoundWay(UserDataRoundWay... userDataRoundWays);
    void updateUserDataRoundWay(UserDataRoundWay... userDataRoundWays);
    void deleteUserDataRoundWay(UserDataRoundWay userDataRoundWay);


}
