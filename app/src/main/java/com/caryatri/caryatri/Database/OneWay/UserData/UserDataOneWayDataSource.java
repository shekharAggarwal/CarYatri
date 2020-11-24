package com.caryatri.caryatri.Database.OneWay.UserData;

import java.util.List;

import io.reactivex.Flowable;

public class UserDataOneWayDataSource implements IUserDatOneWayDataSource {

    private UserDataOneWayDAO userDataOneWayDAO;
    private static UserDataOneWayDataSource instance;

    public UserDataOneWayDataSource(UserDataOneWayDAO userDataOneWayDAO) {
        this.userDataOneWayDAO = userDataOneWayDAO;
    }

    public static UserDataOneWayDataSource getInstance(UserDataOneWayDAO userDataOneWayDAO) {
        if (instance == null)
            instance = new UserDataOneWayDataSource(userDataOneWayDAO);
        return instance;
    }


    @Override
    public Flowable<List<UserDataOneWay>> getUserDataOneWayItem() {
        return userDataOneWayDAO.getUserDataOneWayItem();
    }

    @Override
    public Flowable<List<UserDataOneWay>> getUserDataOneWayItemById(int UserDataOneWayItemId) {
        return userDataOneWayDAO.getUserDataOneWayItemById(UserDataOneWayItemId);
    }

    @Override
    public int countCabUserDataOneWay() {
        return userDataOneWayDAO.countCabUserDataOneWay();
    }

    @Override
    public void emptyUserDataOneWay() {
        userDataOneWayDAO.emptyUserDataOneWay();
    }

    @Override
    public void insertToUserDataOneWay(UserDataOneWay... userDataOneWays) {
        userDataOneWayDAO.insertToUserDataOneWay(userDataOneWays);
    }

    @Override
    public void updateUserDataOneWay(UserDataOneWay... userDataOneWays) {
        userDataOneWayDAO.updateUserDataOneWay(userDataOneWays);
    }

    @Override
    public void deleteUserDataOneWayItem(UserDataOneWay userDataOneWay) {
        userDataOneWayDAO.deleteUserDataOneWayItem(userDataOneWay);
    }
}
