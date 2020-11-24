package com.caryatri.caryatri.Database.OneWay.UserData;

import java.util.List;

import io.reactivex.Flowable;

public class UserDataOneWayRepository implements IUserDatOneWayDataSource {

    private IUserDatOneWayDataSource iUserDatOneWayDataSource;
    private static UserDataOneWayRepository instance;

    public UserDataOneWayRepository(IUserDatOneWayDataSource iUserDatOneWayDataSource) {
        this.iUserDatOneWayDataSource = iUserDatOneWayDataSource;
    }

    public static UserDataOneWayRepository getInstance(IUserDatOneWayDataSource iUserDatOneWayDataSource) {
        if (instance == null)
            instance = new UserDataOneWayRepository(iUserDatOneWayDataSource);
        return instance;
    }


    @Override
    public Flowable<List<UserDataOneWay>> getUserDataOneWayItem() {
        return iUserDatOneWayDataSource.getUserDataOneWayItem();
    }

    @Override
    public Flowable<List<UserDataOneWay>> getUserDataOneWayItemById(int UserDataOneWayItemId) {
        return iUserDatOneWayDataSource.getUserDataOneWayItemById(UserDataOneWayItemId);
    }

    @Override
    public int countCabUserDataOneWay() {
        return iUserDatOneWayDataSource.countCabUserDataOneWay();
    }

    @Override
    public void emptyUserDataOneWay() {
        iUserDatOneWayDataSource.emptyUserDataOneWay();
    }


    @Override
    public void insertToUserDataOneWay(UserDataOneWay... userDataOneWays) {
        iUserDatOneWayDataSource.insertToUserDataOneWay(userDataOneWays);
    }

    @Override
    public void updateUserDataOneWay(UserDataOneWay... userDataOneWays) {
        iUserDatOneWayDataSource.updateUserDataOneWay(userDataOneWays);
    }

    @Override
    public void deleteUserDataOneWayItem(UserDataOneWay userDataOneWay) {
        iUserDatOneWayDataSource.deleteUserDataOneWayItem(userDataOneWay);
    }
}
