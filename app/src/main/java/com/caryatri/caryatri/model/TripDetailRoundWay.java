package com.caryatri.caryatri.model;

public class TripDetailRoundWay {
    private String From, To, PickUpDate, DropDate, PickUpTime, cabType, CabRate;

    public TripDetailRoundWay() {
    }

    public TripDetailRoundWay(String from, String to, String pickUpDate, String dropDate, String pickUpTime, String cabType, String cabRate) {
        From = from;
        To = to;
        PickUpDate = pickUpDate;
        DropDate = dropDate;
        PickUpTime = pickUpTime;
        this.cabType = cabType;
        CabRate = cabRate;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }

    public String getCabRate() {
        return CabRate;
    }

    public void setCabRate(String cabRate) {
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

    public String getDropDate() {
        return DropDate;
    }

    public void setDropDate(String dropDate) {
        DropDate = dropDate;
    }

    public String getPickUpTime() {
        return PickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        PickUpTime = pickUpTime;
    }
}
