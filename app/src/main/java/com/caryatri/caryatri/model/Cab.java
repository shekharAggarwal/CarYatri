package com.caryatri.caryatri.model;

public class Cab {
    private String Id, CabBrand, CabModel, CabImage, CabYear, CabSitting, CabType, CabPrice, CabDec, CabDriver;

    public Cab() {
    }

    public Cab(String id, String cabBrand, String cabModel, String cabImage, String cabYear, String cabSitting, String cabType, String cabPrice, String cabDec) {
        Id = id;
        CabBrand = cabBrand;
        CabModel = cabModel;
        CabImage = cabImage;
        CabYear = cabYear;
        CabSitting = cabSitting;
        CabType = cabType;
        CabPrice = cabPrice;
        CabDec = cabDec;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public void setCabDriver(String cabDriver) {
        CabDriver = cabDriver;
    }

    public String getCabDec() {
        return CabDec;
    }

    public void setCabDec(String cabDec) {
        CabDec = cabDec;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCabBrand() {
        return CabBrand;
    }

    public void setCabBrand(String cabBrand) {
        CabBrand = cabBrand;
    }

    public String getCabModel() {
        return CabModel;
    }

    public void setCabModel(String cabModel) {
        CabModel = cabModel;
    }

    public String getCabImage() {
        return CabImage;
    }

    public void setCabImage(String cabImage) {
        CabImage = cabImage;
    }

    public String getCabYear() {
        return CabYear;
    }

    public void setCabYear(String cabYear) {
        CabYear = cabYear;
    }

    public String getCabSitting() {
        return CabSitting;
    }

    public void setCabSitting(String cabSitting) {
        CabSitting = cabSitting;
    }

    public String getCabType() {
        return CabType;
    }

    public void setCabType(String cabType) {
        CabType = cabType;
    }

    public String getCabPrice() {
        return CabPrice;
    }

    public void setCabPrice(String cabPrice) {
        CabPrice = cabPrice;
    }
}
