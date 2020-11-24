package com.caryatri.caryatri.Database.DataSource.RoundWay.UserData;

import java.util.List;

import io.reactivex.Flowable;

public class UserDataRoundWayDataSource implements IUserDatRoundWayDataSource {

    private UserDataRoundWayDAO userDataRoundWayDAO;
    private static UserDataRoundWayDataSource instance;

    public UserDataRoundWayDataSource(UserDataRoundWayDAO userDataRoundWayDAO) {
        this.userDataRoundWayDAO = userDataRoundWayDAO;
    }

    public static UserDataRoundWayDataSource getInstance(UserDataRoundWayDAO userDataRoundWayDAO) {
        if (instance == null)
            instance = new UserDataRoundWayDataSource(userDataRoundWayDAO);
        return instance;
    }


    @Override
    public Flowable<List<UserDataRoundWay>> getUserDataRoundWayItem() {
        return userDataRoundWayDAO.getUserDataRoundWayItem();
    }

    @Override
    public Flowable<List<UserDataRoundWay>> getUserDataRoundWayItemById(int UserDataRoundWayItemId) {
        return userDataRoundWayDAO.getUserDataRoundWayItemById(UserDataRoundWayItemId);
    }

    @Override
    public int countUserDataRoundWay() {
        return userDataRoundWayDAO.countUserDataRoundWay();
    }

    @Override
    public void emptyUserDataRoundWay() {
        userDataRoundWayDAO.emptyUserDataRoundWay();
    }

    @Override
    public void insertToUserDataRoundWay(UserDataRoundWay... userDataRoundWays) {
        userDataRoundWayDAO.insertToUserDataRoundWay(userDataRoundWays);
    }

    @Override
    public void updateUserDataRoundWay(UserDataRoundWay... userDataRoundWays) {
        userDataRoundWayDAO.updateUserDataRoundWay(userDataRoundWays);
    }

    @Override
    public void deleteUserDataRoundWay(UserDataRoundWay userDataRoundWay) {
        userDataRoundWayDAO.deleteUserDataRoundWay(userDataRoundWay);
    }


}
