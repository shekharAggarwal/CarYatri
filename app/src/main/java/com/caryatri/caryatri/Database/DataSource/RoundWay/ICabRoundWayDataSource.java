package com.caryatri.caryatri.Database.DataSource.RoundWay;

import androidx.room.Query;

import com.caryatri.caryatri.Database.OneWay.CabOneWay;

import java.util.List;

import io.reactivex.Flowable;

public interface ICabRoundWayDataSource {
    Flowable<List<CabRoundWay>> getCabRoundWayItem();
    Flowable<List<CabRoundWay>> getCabRoundWayItemById(int CabRoundWayItemId);
    int countCabRoundWayItems();
    void emptyCabRoundWay();
    void insertToCabRoundWay(CabRoundWay... cabRoundWays);
    void updateCabRoundWay(CabRoundWay... cabRoundWays);
    void deleteCabRoundWayItem(CabRoundWay cabRoundWay);
    int CountCabRoundWay(String status);
    Flowable<List<CabRoundWay>> getCabRoundWayItemByStatus(String status);
    void updateInComplete();
    void deleteInComplete();
    void UpdateCabRoundWay(String price ,int id);

}
