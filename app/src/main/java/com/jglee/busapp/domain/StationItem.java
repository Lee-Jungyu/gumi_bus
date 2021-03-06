package com.jglee.busapp.domain;

public class StationItem {
    private String stationId;
    private String stationName;
    private String stationNumber;
    private double stationLat;
    private double stationLon;

    public StationItem(String stationId, String stationName, String stationNumber, double stationLat, double stationLon) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationNumber= stationNumber;
        this.stationLat = stationLat;
        this.stationLon = stationLon;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationNumber() { return stationNumber; }

    public double getStationLat() {
        return stationLat;
    }

    public double getStationLon() {
        return stationLon;
    }
}