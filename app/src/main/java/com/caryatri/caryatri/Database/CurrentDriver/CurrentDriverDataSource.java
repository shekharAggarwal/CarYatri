package com.caryatri.caryatri.Database.CurrentDriver;

import java.util.List;

import io.reactivex.Flowable;

public class CurrentDriverDataSource implements ICurrentDriverDataSource {

    private CurrentDriverDBDAO currentDriverDBDAO;
    private static CurrentDriverDataSource instance;

    public CurrentDriverDataSource(CurrentDriverDBDAO currentDriverDBDAO) {
        this.currentDriverDBDAO = currentDriverDBDAO;
    }

    public static CurrentDriverDataSource getInstance(CurrentDriverDBDAO currentDriverDBDAO) {
        if (instance == null)
            instance = new CurrentDriverDataSource(currentDriverDBDAO);
        return instance;
    }

    @Override
    public Flowable<List<CurrentDriverDB>> getCurrentDriverDB() {
        return currentDriverDBDAO.getCurrentDriverDB();
    }

    @Override
    public Flowable<List<String>> getPhone(String status) {
        return currentDriverDBDAO.getPhone(status);
    }

    @Override
    public void updateCurrentDriverDB(String driverPhone) {
        currentDriverDBDAO.updateCurrentDriverDB(driverPhone);
    }

    @Override
    public void deleteCurrentDriverDB(String driverPhone) {
        currentDriverDBDAO.deleteCurrentDriverDB(driverPhone);
    }

    @Override
    public void deleteCompleteCurrentDriverDB(String driverPhone) {
        currentDriverDBDAO.deleteCompleteCurrentDriverDB(driverPhone);
    }

    @Override
    public int getCurrentDriverDBAccepted() {
        return currentDriverDBDAO.getCurrentDriverDBAccepted();
    }

    @Override
    public int getCurrentDriverDBStart() {
        return currentDriverDBDAO.getCurrentDriverDBStart();
    }

    @Override
    public void emptyCurrentDriver() {
        currentDriverDBDAO.emptyCurrentDriver();
    }

    @Override
    public void updateInvoiceCurrentDriverDB(String driverPhone) {
        currentDriverDBDAO.updateInvoiceCurrentDriverDB(driverPhone);
    }

    @Override
    public Flowable<List<CurrentDriverDB>> getInvoice() {
        return currentDriverDBDAO.getInvoice();
    }

    @Override
    public void updateCabDetail(String cabDetails, String driverPhone) {
        currentDriverDBDAO.updateCabDetail(cabDetails, driverPhone);
    }

    @Override
    public void insertToCurrentDriver(CurrentDriverDB... currentDriverDBS) {
        currentDriverDBDAO.insertToCurrentDriver(currentDriverDBS);
    }

    @Override
    public void updateCurrentDriverDB(CurrentDriverDB... currentDriverDBS) {
        currentDriverDBDAO.updateCurrentDriverDB(currentDriverDBS);
    }

    @Override
    public void deleteCurrentDriverDBItem(CurrentDriverDB currentDriverDB) {
        currentDriverDBDAO.deleteCurrentDriverDBItem(currentDriverDB);
    }
}
