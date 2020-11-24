package com.caryatri.caryatri.Database.OneWay;

import java.util.List;

import io.reactivex.Flowable;

public class CabOneWayDataSource implements ICabOneWayDataSource {

    private CabOneWayDAO cabOneWayDAO;
    private static CabOneWayDataSource instance;

    public CabOneWayDataSource(CabOneWayDAO cabOneWayDAO) {
        this.cabOneWayDAO = cabOneWayDAO;
    }

    public static CabOneWayDataSource getInstance(CabOneWayDAO cabOneWayDAO) {
        if (instance == null)
            instance = new CabOneWayDataSource(cabOneWayDAO);
        return instance;
    }


    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItem() {
        return cabOneWayDAO.getCabOneWayItem();
    }

    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItemById(int CabOneWayItemId) {
        return cabOneWayDAO.getCabOneWayItemById(CabOneWayItemId);
    }

    @Override
    public int countCabOneWayItems() {
        return cabOneWayDAO.countCabOneWayItems();
    }

    @Override
    public void emptyCabOneWay() {
        cabOneWayDAO.emptyCabOneWay();
    }

    @Override
    public void insertToCabOneWay(CabOneWay... cabOneWays) {
        cabOneWayDAO.insertToCabOneWay(cabOneWays);
    }

    @Override
    public void updateCabOneWay(CabOneWay... cabOneWays) {
        cabOneWayDAO.updateCabOneWay(cabOneWays);
    }

    @Override
    public void deleteCabOneWayItem(CabOneWay cabOneWay) {
        cabOneWayDAO.deleteCabOneWayItem(cabOneWay);
    }

    @Override
    public int CountCabOneWay(String status) {
        return cabOneWayDAO.CountCabOneWay(status);
    }

    @Override
    public void deleteInComplete() {
        cabOneWayDAO.deleteInComplete();
    }

    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItemByStatus(String status) {
        return cabOneWayDAO.getCabOneWayItemByStatus(status);
    }

    @Override
    public void updateInComplete() {
        cabOneWayDAO.updateInComplete();
    }

    @Override
    public void UpdateCabOneWay(String price, int id) {
        cabOneWayDAO.UpdateCabOneWay(price, id);
    }
}
