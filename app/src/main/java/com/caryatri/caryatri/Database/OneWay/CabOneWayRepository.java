package com.caryatri.caryatri.Database.OneWay;

import java.util.List;

import io.reactivex.Flowable;

public class CabOneWayRepository implements ICabOneWayDataSource {

    private ICabOneWayDataSource iCabOneWayDataSource;
    private static CabOneWayRepository instance;

    public CabOneWayRepository(ICabOneWayDataSource iCabOneWayDataSource) {
        this.iCabOneWayDataSource = iCabOneWayDataSource;
    }

    public static CabOneWayRepository getInstance(ICabOneWayDataSource iCabOneWayDataSource) {
        if (instance == null)
            instance = new CabOneWayRepository(iCabOneWayDataSource);
        return instance;
    }

    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItem() {
        return iCabOneWayDataSource.getCabOneWayItem();
    }

    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItemById(int CabOneWayItemId) {
        return iCabOneWayDataSource.getCabOneWayItemById(countCabOneWayItems());
    }

    @Override
    public int countCabOneWayItems() {
        return iCabOneWayDataSource.countCabOneWayItems();
    }

    @Override
    public void emptyCabOneWay() {
        iCabOneWayDataSource.emptyCabOneWay();
    }

    @Override
    public void insertToCabOneWay(CabOneWay... cabOneWays) {
        iCabOneWayDataSource.insertToCabOneWay(cabOneWays);
    }

    @Override
    public void updateCabOneWay(CabOneWay... cabOneWays) {
        iCabOneWayDataSource.updateCabOneWay(cabOneWays);
    }

    @Override
    public void deleteCabOneWayItem(CabOneWay cabOneWay) {
        iCabOneWayDataSource.deleteCabOneWayItem(cabOneWay);
    }

    @Override
    public int CountCabOneWay(String status) {
        return iCabOneWayDataSource.CountCabOneWay(status);
    }

    @Override
    public void deleteInComplete() {
        iCabOneWayDataSource.deleteInComplete();
    }

    @Override
    public Flowable<List<CabOneWay>> getCabOneWayItemByStatus(String status) {
        return iCabOneWayDataSource.getCabOneWayItemByStatus(status);
    }

    @Override
    public void updateInComplete() {
        iCabOneWayDataSource.updateInComplete();
    }

    @Override
    public void UpdateCabOneWay(String price, int id) {
        iCabOneWayDataSource.UpdateCabOneWay(price,id);
    }

}
