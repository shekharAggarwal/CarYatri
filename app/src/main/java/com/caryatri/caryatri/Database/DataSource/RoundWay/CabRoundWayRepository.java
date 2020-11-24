package com.caryatri.caryatri.Database.DataSource.RoundWay;

import java.util.List;

import io.reactivex.Flowable;

public class CabRoundWayRepository implements ICabRoundWayDataSource {

    private ICabRoundWayDataSource iCabRoundWayDataSource;
    private static CabRoundWayRepository instance;

    public CabRoundWayRepository(ICabRoundWayDataSource iCabRoundWayDataSource) {
        this.iCabRoundWayDataSource = iCabRoundWayDataSource;
    }

    public static CabRoundWayRepository getInstance(ICabRoundWayDataSource iCabRoundWayDataSource) {
        if (instance == null)
            instance = new CabRoundWayRepository(iCabRoundWayDataSource);
        return instance;
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItem() {
        return iCabRoundWayDataSource.getCabRoundWayItem();
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItemById(int CabRoundWayItemId) {
        return iCabRoundWayDataSource.getCabRoundWayItemById(CabRoundWayItemId);
    }

    @Override
    public int countCabRoundWayItems() {
        return iCabRoundWayDataSource.countCabRoundWayItems();
    }

    @Override
    public void emptyCabRoundWay() {
        iCabRoundWayDataSource.emptyCabRoundWay();
    }

    @Override
    public void insertToCabRoundWay(CabRoundWay... cabRoundWays) {
        iCabRoundWayDataSource.insertToCabRoundWay(cabRoundWays);
    }

    @Override
    public void updateCabRoundWay(CabRoundWay... cabRoundWays) {
        iCabRoundWayDataSource.updateCabRoundWay(cabRoundWays);
    }

    @Override
    public void deleteCabRoundWayItem(CabRoundWay cabRoundWay) {
        iCabRoundWayDataSource.deleteCabRoundWayItem(cabRoundWay);
    }

    @Override
    public int CountCabRoundWay(String status) {
        return iCabRoundWayDataSource.CountCabRoundWay(status);
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItemByStatus(String status) {
        return iCabRoundWayDataSource.getCabRoundWayItemByStatus(status);
    }

    @Override
    public void updateInComplete() {
        iCabRoundWayDataSource.updateInComplete();
    }

    @Override
    public void deleteInComplete() {
        iCabRoundWayDataSource.deleteInComplete();
    }

    @Override
    public void UpdateCabRoundWay(String price, int id) {
        iCabRoundWayDataSource.UpdateCabRoundWay(price, id);
    }
}
