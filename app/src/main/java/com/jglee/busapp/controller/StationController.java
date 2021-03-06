package com.jglee.busapp.controller;

import android.content.Context;

import com.jglee.busapp.R;
import com.jglee.busapp.domain.ArrivalData;
import com.jglee.busapp.domain.BusItem;
import com.jglee.busapp.domain.StationItem;
import com.jglee.busapp.util.HTMLTranslator;
import com.jglee.busapp.util.XMLTranslator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class StationController {

    static List<StationItem> allStationList;

    public static void loadStationList(Context context) {
        allStationList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getSttnNoList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + context.getString(R.string.tago_api_key)); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode(context.getString(R.string.gumi_city_code), "UTF-8")); /*도시코드*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("5000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String xml = sb.toString();
            Document xmlDoc = XMLTranslator.getXMLDocument(xml);

            NodeList itemList = xmlDoc.getElementsByTagName("item");

            for(int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                Document item = XMLTranslator.nodeToDocument(itemNode);
                String stationName = XMLTranslator.getStringValue(item, "nodenm");
                String stationId = XMLTranslator.getStringValue(item, "nodeid");
                String stationNumber = XMLTranslator.getStringValue(item, "nodeno");
                double lat = Double.parseDouble(XMLTranslator.getStringValue(item, "gpslati"));
                double lon = Double.parseDouble(XMLTranslator.getStringValue(item, "gpslong"));

                allStationList.add(new StationItem(stationId, stationName, stationNumber, lat, lon));
            }

            allStationList.sort(new Comparator<StationItem>() {
                @Override
                public int compare(StationItem station1, StationItem station2) {
                    return station1.getStationName().compareTo(station2.getStationName());
                }
            });

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static StationItem getStationItem(String stationId) {
        for(int i = 0; i < allStationList.size(); i++) {
            if(allStationList.get(i).getStationId().equals(stationId)) {
                return allStationList.get(i);
            }
        }
        return null;
    }

    public static List<StationItem> getStationList(String searchString) {
        List<StationItem> list = new ArrayList<>();

        if(searchString.equals("")) {
            return allStationList;
        }

        for(int i = 0; i < allStationList.size(); i++) {
            if(allStationList.get(i).getStationName().contains(searchString)) {
                list.add(allStationList.get(i));
            }
            else if(allStationList.get(i).getStationNumber().contains(searchString)) {
                list.add(allStationList.get(i));
            }
        }

        return list;
    }

    public static List<ArrivalData> loadArrivalList(String stationId) {
        List<ArrivalData> list = new ArrayList<>();

        String tmpStationId = stationId.substring(3, stationId.length());
        try {
            URL arrivalDataUrl = new URL("http://bis.gumi.go.kr/moMap/mBusStopResult.do?station_id=" + tmpStationId);
            org.jsoup.nodes.Document doc = HTMLTranslator.getDocumentByUrl(arrivalDataUrl);
            org.jsoup.select.Elements elements = HTMLTranslator.getElementsByDocsQuery(doc, ".stops_list01 ul li");
            for(org.jsoup.nodes.Element element : elements) {
                org.jsoup.nodes.Element con_view01 = HTMLTranslator.getElementByElementSelect(element, ".con_view01");
                org.jsoup.nodes.Element con_view02 = HTMLTranslator.getElementByElementSelect(element, ".con_view02");
                org.jsoup.nodes.Element con_view03 = HTMLTranslator.getElementByElementSelect(element, ".con_view03");

                String busId = "";
                int prevStationCnt = 10000;
                int remainingTime = 10000;

                busId = con_view01.attr("id");
                busId = busId.substring(0, busId.length() / 2);
                busId = busId.replaceAll("[^0-9|-]",""); //busid에서 숫자와 - 제외하고 없애기
                busId = "GMB" + busId;

                if(con_view02.select("strong").size() != 0) {
                    prevStationCnt = Integer.parseInt(con_view02.select("strong").text());
                }
                if(con_view03.select("strong").size() != 0) {
                    remainingTime = Integer.parseInt(con_view03.select("strong").text());
                }

                BusItem bus = null;
                List<BusItem> busList = BusController.getBusList("");
                for(int i = 0; i < busList.size(); i++) {
                    if(busList.get(i).getBusId().equals(busId)) {
                        bus = busList.get(i);
                        break;
                    }
                }

                ArrivalData data = new ArrivalData(bus, prevStationCnt, remainingTime);
                list.add(data);

                list.sort(new Comparator<ArrivalData>() {
                    @Override
                    public int compare(ArrivalData o1, ArrivalData o2) {
                        Integer i1 = o1.getRemainingTime();
                        Integer i2 = o2.getRemainingTime();

                        return i1.compareTo(i2);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }

}
