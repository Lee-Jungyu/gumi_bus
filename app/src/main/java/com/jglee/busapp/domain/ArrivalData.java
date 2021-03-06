package com.jglee.busapp.domain;

public class ArrivalData {
    private BusItem bus;
    private int prevStationCnt;
    private int remainingTime;

    public ArrivalData( BusItem bus, int prevStationCnt, int remainingTime) {
        this.bus = bus;
        this.prevStationCnt = prevStationCnt;
        this.remainingTime = remainingTime;
    }

    public BusItem getBus() {
        return bus;
    }

    public int getPrevStationCnt() {
        return prevStationCnt;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
