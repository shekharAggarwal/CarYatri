package com.caryatri.caryatri.Database.OneWay;

import java.util.List;

import io.reactivex.Flowable;

public interface ICabOneWayDataSource {
    Flowable<List<CabOneWay>> getCabOneWayItem();
    Flowable<List<CabOneWay>> getCabOneWayItemById(int CabOneWayItemId);
    int countCabOneWayItems();
    void emptyCabOneWay();
    void insertToCabOneWay(CabOneWay... cabOneWays);
    void updateCabOneWay(CabOneWay... cabOneWays);
    void deleteCabOneWayItem(CabOneWay cabOneWay);
    int CountCabOneWay(String status);
    void deleteInComplete();
    Flowable<List<CabOneWay>> getCabOneWayItemByStatus(String status);
    void updateInComplete();
    void UpdateCabOneWay(String price,int id);
}
