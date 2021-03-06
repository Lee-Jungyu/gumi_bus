package com.jglee.busapp.domain;

public class BusItem {
    private String busId;
    private String busNumber;
    private String busType;
    private String busStartStationName;
    private String busEndStationName;

    public BusItem(String busId, String busNumber, String busType, String busStartStationName, String busEndStationName) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.busType = busType;
        this.busStartStationName = busStartStationName;
        this.busEndStationName = busEndStationName;
    }

    public String getBusId() {
        return busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public String getBusStartStationName() { return busStartStationName; }

    public String getBusEndStationName() { return busEndStationName; }
}
