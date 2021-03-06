package com.jglee.busapp.domain;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RouteItem {
    private Location source;
    private Location dest;
    private JSONObject info;
    private JSONArray subPathList;

    public RouteItem(Location source, Location dest, JSONObject path) {
        try {
            this.source = source;
            this.dest = dest;
            this.info               = path.getJSONObject("info");
            this.subPathList        = path.getJSONArray("subPath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RouteItem(JSONObject info, JSONArray subPathList) {
        this.info = info;
        this.subPathList = subPathList;
    }

    public Location getSource(){
        return source;
    }

    public Location getDest() {
        return dest;
    }

    public JSONObject getInfo() {
        return info;
    }

    public JSONArray getSubPathList() {
        return subPathList;
    }
}
