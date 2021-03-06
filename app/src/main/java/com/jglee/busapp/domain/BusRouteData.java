package com.jglee.busapp.domain;

public class BusRouteData {
    private StationItem station;
    private boolean currentBusPose;

    public BusRouteData(StationItem station, boolean currentBusPose) {
        this.station = station;
        this.currentBusPose = currentBusPose;
    }

    public StationItem getStation() {
        return station;
    }

    public boolean isCurrentBusPose() {
        return currentBusPose;
    }

    public void setCurrentBusPose(boolean currentBusPose) {
        this.currentBusPose = currentBusPose;
    }
}
