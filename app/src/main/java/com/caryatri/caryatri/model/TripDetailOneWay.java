package com.caryatri.caryatri.model;

public class TripDetailOneWay {

    private String From, To, PickUpDate, PickUpTime, CabType, CabRate;

    public TripDetailOneWay() {
    }

    public TripDetailOneWay(String from, String to, String pickUpDate, String pickUpTime, String cabType, String cabRate) {
        From = from;
        To = to;
        PickUpDate = pickUpDate;
        PickUpTime = pickUpTime;
        CabType = cabType;
        CabRate = cabRate;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getPickUpDate() {
        return PickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        PickUpDate = pickUpDate;
    }

    public String getPickUpTime() {
        return PickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        PickUpTime = pickUpTime;
    }

    public String getCabType() {
        return CabType;
    }

    public void setCabType(String cabType) {
        CabType = cabType;
    }

    public String getCabRate() {
        return CabRate;
    }

    public void setCabRate(String cabRate) {
        CabRate = cabRate;
    }
}
