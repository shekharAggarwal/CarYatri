package com.caryatri.caryatri.Database.DataSource.RoundWay.UserData;

import java.util.List;

import io.reactivex.Flowable;

public class UserDataRoundWayRepository implements IUserDatRoundWayDataSource {

    private IUserDatRoundWayDataSource iUserDatRoundWayDataSource;
    private static UserDataRoundWayRepository instance;

    public UserDataRoundWayRepository(IUserDatRoundWayDataSource iUserDatRoundWayDataSource) {
        this.iUserDatRoundWayDataSource = iUserDatRoundWayDataSource;
    }

    public static UserDataRoundWayRepository getInstance(IUserDatRoundWayDataSource iUserDatRoundWayDataSource) {
        if (instance == null)
            instance = new UserDataRoundWayRepository(iUserDatRoundWayDataSource);
        return instance;
    }


    @Override
    public Flowable<List<UserDataRoundWay>> getUserDataRoundWayItem() {
        return iUserDatRoundWayDataSource.getUserDataRoundWayItem();
    }

    @Override
    public Flowable<List<UserDataRoundWay>> getUserDataRoundWayItemById(int UserDataRoundWayItemId) {
        return iUserDatRoundWayDataSource.getUserDataRoundWayItemById(UserDataRoundWayItemId);
    }

    @Override
    public int countUserDataRoundWay() {
        return iUserDatRoundWayDataSource.countUserDataRoundWay();
    }

    @Override
    public void emptyUserDataRoundWay() {
        iUserDatRoundWayDataSource.emptyUserDataRoundWay();
    }

    @Override
    public void insertToUserDataRoundWay(UserDataRoundWay... userDataRoundWays) {
        iUserDatRoundWayDataSource.insertToUserDataRoundWay(userDataRoundWays);
    }

    @Override
    public void updateUserDataRoundWay(UserDataRoundWay... userDataRoundWays) {
        iUserDatRoundWayDataSource.updateUserDataRoundWay(userDataRoundWays);
    }

    @Override
    public void deleteUserDataRoundWay(UserDataRoundWay userDataRoundWay) {
        iUserDatRoundWayDataSource.deleteUserDataRoundWay(userDataRoundWay);
    }
}
