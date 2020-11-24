package com.caryatri.caryatri.Database.OneWay.UserData;

import java.util.List;

import io.reactivex.Flowable;

public interface IUserDatOneWayDataSource {
    Flowable<List<UserDataOneWay>> getUserDataOneWayItem();
    Flowable<List<UserDataOneWay>> getUserDataOneWayItemById(int UserDataOneWayItemId);
    int countCabUserDataOneWay();
    void emptyUserDataOneWay();
    void insertToUserDataOneWay(UserDataOneWay... userDataOneWays);
    void updateUserDataOneWay(UserDataOneWay... userDataOneWays);
    void deleteUserDataOneWayItem(UserDataOneWay userDataOneWay);

}
