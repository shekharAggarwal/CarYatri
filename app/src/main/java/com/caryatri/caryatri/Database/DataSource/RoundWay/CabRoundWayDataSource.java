package com.caryatri.caryatri.Database.DataSource.RoundWay;

import com.caryatri.caryatri.Database.OneWay.CabOneWay;

import java.util.List;

import io.reactivex.Flowable;

public class CabRoundWayDataSource implements ICabRoundWayDataSource {

    private CabRoundWayDAO cabRoundWayDAO;
    private static CabRoundWayDataSource instance;

    public CabRoundWayDataSource(CabRoundWayDAO cabRoundWayDAO) {
        this.cabRoundWayDAO = cabRoundWayDAO;
    }

    public static CabRoundWayDataSource getInstance(CabRoundWayDAO cabRoundWayDAO) {
        if (instance == null)
            instance = new CabRoundWayDataSource(cabRoundWayDAO);
        return instance;
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItem() {
        return cabRoundWayDAO.getCabRoundWayItem();
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItemById(int CabRoundWayItemId) {
        return cabRoundWayDAO.getCabRoundWayItemById(CabRoundWayItemId);
    }

    @Override
    public int countCabRoundWayItems() {
        return cabRoundWayDAO.countCabRoundWayItems();
    }

    @Override
    public void emptyCabRoundWay() {
        cabRoundWayDAO.emptyCabRoundWay();
    }

    @Override
    public void insertToCabRoundWay(CabRoundWay... cabRoundWays) {
        cabRoundWayDAO.insertToCabRoundWay(cabRoundWays);
    }

    @Override
    public void updateCabRoundWay(CabRoundWay... cabRoundWays) {
        cabRoundWayDAO.updateCabRoundWay(cabRoundWays);
    }

    @Override
    public void deleteCabRoundWayItem(CabRoundWay cabRoundWay) {
        cabRoundWayDAO.deleteCabRoundWayItem(cabRoundWay);
    }

    @Override
    public int CountCabRoundWay(String status) {
        return cabRoundWayDAO.CountCabRoundWay(status);
    }

    @Override
    public Flowable<List<CabRoundWay>> getCabRoundWayItemByStatus(String status) {
        return cabRoundWayDAO.getCabRoundWayItemByStatus(status);
    }

    @Override
    public void updateInComplete() {
        cabRoundWayDAO.updateInComplete();
    }

    @Override
    public void deleteInComplete() {
        cabRoundWayDAO.deleteInComplete();
    }

    @Override
    public void UpdateCabRoundWay(String price, int id) {
        cabRoundWayDAO.UpdateCabRoundWay(price,id);
    }
}
