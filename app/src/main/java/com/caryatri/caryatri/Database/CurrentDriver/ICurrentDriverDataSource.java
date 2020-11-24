package com.caryatri.caryatri.Database.CurrentDriver;

import java.util.List;

import io.reactivex.Flowable;

public interface ICurrentDriverDataSource {

    Flowable<List<CurrentDriverDB>> getCurrentDriverDB();

    Flowable<List<String>> getPhone(String status);

    void updateCurrentDriverDB(String driverPhone);

    void deleteCurrentDriverDB(String driverPhone);

    int getCurrentDriverDBAccepted();

    int getCurrentDriverDBStart();

    void emptyCurrentDriver();

    void updateInvoiceCurrentDriverDB(String driverPhone);

    Flowable<List<CurrentDriverDB>> getInvoice();

    void deleteCompleteCurrentDriverDB(String driverPhone);

    void updateCabDetail(String cabDetails, String driverPhone);

    void insertToCurrentDriver(CurrentDriverDB... currentDriverDBS);

    void updateCurrentDriverDB(CurrentDriverDB... currentDriverDBS);

    void deleteCurrentDriverDBItem(CurrentDriverDB currentDriverDB);
}
