package com.caryatri.caryatri.model;

public class cabDetails {
    private String Id,CabBrand,CabModel,CabNumber,CabImage,CabSitting,CabPrice,CabDec,CabDriver;

    public cabDetails(String id, String cabBrand, String cabModel, String cabNumber, String cabImage, String cabSitting, String cabPrice, String cabDec, String cabDriver) {
        Id = id;
        CabBrand = cabBrand;
        CabModel = cabModel;
        CabNumber = cabNumber;
        CabImage = cabImage;
        CabSitting = cabSitting;
        CabPrice = cabPrice;
        CabDec = cabDec;
        CabDriver = cabDriver;
    }

    public String getCabNumber() {
        return CabNumber;
    }

    public void setCabNumber(String cabNumber) {
        CabNumber = cabNumber;
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

    public String getCabSitting() {
        return CabSitting;
    }

    public void setCabSitting(String cabSitting) {
        CabSitting = cabSitting;
    }

    public String getCabPrice() {
        return CabPrice;
    }

    public void setCabPrice(String cabPrice) {
        CabPrice = cabPrice;
    }

    public String getCabDec() {
        return CabDec;
    }

    public void setCabDec(String cabDec) {
        CabDec = cabDec;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public void setCabDriver(String cabDriver) {
        CabDriver = cabDriver;
    }
}
