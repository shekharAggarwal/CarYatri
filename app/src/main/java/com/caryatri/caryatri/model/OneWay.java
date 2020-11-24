package com.caryatri.caryatri.model;

import java.sql.Time;

public class OneWay {
    private int Id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String sourceAddress;
    private String destinationAddress;
    private String pickupDate;
    private String pickupTime;
    private String source;
    private String destination;
    private String Cabs;
    private String BookAccount;

    public OneWay() {
    }

    public OneWay(int id, String fullName, String phoneNumber, String email, String sourceAddress, String destinationAddress, String pickupDate, String pickupTime, String source, String destination, String cabs, String bookAccount) {
        Id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.source = source;
        this.destination = destination;
        Cabs = cabs;
        BookAccount = bookAccount;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCabs() {
        return Cabs;
    }

    public void setCabs(String cabs) {
        Cabs = cabs;
    }

    public String getBookAccount() {
        return BookAccount;
    }

    public void setBookAccount(String bookAccount) {
        BookAccount = bookAccount;
    }
}
