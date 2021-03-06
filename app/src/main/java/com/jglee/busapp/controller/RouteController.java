package com.jglee.busapp.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.jglee.busapp.R;
import com.jglee.busapp.domain.RouteItem;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteController {

    public static Location findGeoPose(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName("구미시 " + address, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if(list != null) {
            Location loc = new Location("");
            double lat = list.get(0).getLatitude();
            double lon = list.get(0).getLongitude();
            loc.setLatitude(lat);
            loc.setLongitude(lon);

            return loc;
        }

        return null;
    }

    public static List<RouteItem> getRoute(Context context, Location source, Location dest) {

        List<RouteItem> list = new ArrayList<>();
        final boolean[] checkComplete = {false};

        String odsay_key = context.getResources().getString(R.string.odsay_key);
        ODsayService oDsayService = ODsayService.init(context, odsay_key);
        oDsayService.setReadTimeout(5000);
        oDsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try {
                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        JSONArray pathList = oDsayData.getJson().getJSONObject("result").getJSONArray("path");

                        for (int i = 0; i < pathList.length(); i++) {
                            JSONObject path = (JSONObject) pathList.get(i);
                            RouteItem item = new RouteItem(source, dest, path);
                            list.add(item);
                            System.out.println(path.getJSONObject("info"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s, API api) {

            }
        };

        String slat = String.valueOf(source.getLatitude());
        String slon = String.valueOf(source.getLongitude());
        String dlat = String.valueOf(dest.getLatitude());
        String dlon = String.valueOf(dest.getLongitude());

        oDsayService.requestSearchPubTransPath(slon, slat, dlon, dlat, "0", "0", "0", onResultCallbackListener);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("size: " + list.size());
        return list;

    }

    public static SpannableStringBuilder getRouteRow(RouteItem item, String dest) {
        SpannableStringBuilder routeStr = new SpannableStringBuilder();

        try {
            routeStr.append("예상 시간: " + item.getInfo().getInt("totalTime") + "분");
            routeStr.append("\n예상 요금: " + item.getInfo().getInt("payment") + "원");
            routeStr.append("\n총 거리: " + item.getInfo().getDouble("totalDistance") / 1000 + "km");

            for (int i = 0; i < item.getSubPathList().length(); i++) {
                JSONObject subPath = (JSONObject) item.getSubPathList().get(i);

                if (subPath.getInt("trafficType") == 3) {
                    routeStr.append("\n\n \uD83D\uDC5F 도보 이동\n");
                    if (i != item.getSubPathList().length() - 1) {
                        JSONObject afterSubPath = (JSONObject) item.getSubPathList().get(i + 1);
                        String stationStr = afterSubPath.getString("startName");

                        int spanStartIdx = routeStr.length();
                        routeStr.append(stationStr);
                        int spanEndIdx = routeStr.length();
                        routeStr.append("까지 걷기 ");

                        routeStr.setSpan(new ForegroundColorSpan(Color.RED), spanStartIdx, spanEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        int spanStartIdx = routeStr.length();
                        routeStr.append(dest);
                        int spanEndIdx = routeStr.length();
                        routeStr.append("까지 걷기");

                        routeStr.setSpan(new ForegroundColorSpan(Color.RED), spanStartIdx, spanEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    routeStr.append("\n" + subPath.getDouble("distance") / 1000 + "km 이동");
                } else if (subPath.getInt("trafficType") == 2) {
                    routeStr.append("\n\n" + "\uD83D\uDE8C 버스 탑승");
                    int spanStartIdx = routeStr.length();
                    for (int j = 0; j < subPath.getJSONArray("lane").length(); j++) {
                        JSONObject lane = (JSONObject) subPath.getJSONArray("lane").get(j);
                        routeStr.append("\n" + lane.getString("busNo"));
                    }
                    int spanEndIdx = routeStr.length();
                    routeStr.setSpan(new StyleSpan(Typeface.BOLD), spanStartIdx, spanEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    routeStr.append("\n");
                    spanStartIdx = routeStr.length();
                    routeStr.append(subPath.getString("startName"));
                    spanEndIdx = routeStr.length();
                    routeStr.setSpan(new ForegroundColorSpan(Color.RED), spanStartIdx, spanEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    routeStr.append(" 승차");

                    routeStr.append("\n" + subPath.getDouble("distance") / 1000 + "km 이동\n");

                    spanStartIdx = routeStr.length();
                    routeStr.append(subPath.getString("endName"));
                    spanEndIdx = routeStr.length();
                    routeStr.setSpan(new ForegroundColorSpan(Color.BLUE), spanStartIdx, spanEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    routeStr.append(" 하차");

                }
            }

            routeStr.append("\n");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return routeStr;
    }
}
