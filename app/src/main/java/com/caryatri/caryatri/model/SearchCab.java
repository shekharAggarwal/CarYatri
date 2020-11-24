package com.caryatri.caryatri.model;

public class SearchCab {
    private String StateName, CityName;

    public SearchCab(String stateName, String cityName) {
        StateName = stateName;
        CityName = cityName;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }
}
