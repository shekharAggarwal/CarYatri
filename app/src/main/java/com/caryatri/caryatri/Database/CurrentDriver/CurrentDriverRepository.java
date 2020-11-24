package com.caryatri.caryatri.Database.CurrentDriver;

import java.util.List;

import io.reactivex.Flowable;

public class CurrentDriverRepository implements ICurrentDriverDataSource {

    private ICurrentDriverDataSource currentDriverDataSource;
    private static CurrentDriverRepository instance;

    private CurrentDriverRepository(ICurrentDriverDataSource currentDriverDataSource) {
        this.currentDriverDataSource = currentDriverDataSource;
    }

    public static CurrentDriverRepository getInstance(ICurrentDriverDataSource currentDriverDataSource) {
        if (instance == null)
            instance = new CurrentDriverRepository(currentDriverDataSource);
        return instance;
    }

    @Override
    public Flowable<List<CurrentDriverDB>> getCurrentDriverDB() {
        return currentDriverDataSource.getCurrentDriverDB();
    }

    @Override
    public Flowable<List<String>> getPhone(String status) {
        return currentDriverDataSource.getPhone(status);
    }

    @Override
    public void updateCurrentDriverDB(String driverPhone) {
        currentDriverDataSource.updateCurrentDriverDB(driverPhone);
    }

    @Override
    public void deleteCurrentDriverDB(String driverPhone) {
        currentDriverDataSource.deleteCurrentDriverDB(driverPhone);
    }

    @Override
    public void deleteCompleteCurrentDriverDB(String driverPhone) {
        currentDriverDataSource.deleteCompleteCurrentDriverDB(driverPhone);
    }

    @Override
    public int getCurrentDriverDBAccepted() {
        return currentDriverDataSource.getCurrentDriverDBAccepted();
    }

    @Override
    public int getCurrentDriverDBStart() {
        return currentDriverDataSource.getCurrentDriverDBStart();
    }

    @Override
    public void emptyCurrentDriver() {
        currentDriverDataSource.emptyCurrentDriver();
    }

    @Override
    public void updateInvoiceCurrentDriverDB(String driverPhone) {
        currentDriverDataSource.updateInvoiceCurrentDriverDB(driverPhone);
    }

    @Override
    public Flowable<List<CurrentDriverDB>> getInvoice() {
        return currentDriverDataSource.getInvoice();
    }

    @Override
    public void updateCabDetail(String cabDetails, String driverPhone) {
        currentDriverDataSource.updateCabDetail(cabDetails, driverPhone);
    }

    @Override
    public void insertToCurrentDriver(CurrentDriverDB... currentDriverDBS) {
        currentDriverDataSource.insertToCurrentDriver(currentDriverDBS);
    }

    @Override
    public void updateCurrentDriverDB(CurrentDriverDB... currentDriverDBS) {
        currentDriverDataSource.updateCurrentDriverDB(currentDriverDBS);
    }

    @Override
    public void deleteCurrentDriverDBItem(CurrentDriverDB currentDriverDB) {
        currentDriverDataSource.deleteCurrentDriverDBItem(currentDriverDB);
    }
}
