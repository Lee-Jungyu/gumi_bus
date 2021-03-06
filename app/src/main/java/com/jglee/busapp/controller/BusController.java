package com.jglee.busapp.controller;

import android.content.Context;

import com.jglee.busapp.R;
import com.jglee.busapp.domain.BusItem;
import com.jglee.busapp.domain.BusRouteData;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

public class BusController {

    static List<BusItem> allBusList;

    public static void loadBusList(Context context) {
        allBusList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteNoList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + context.getString(R.string.tago_api_key)); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("cityCode","UTF-8") + "=" + URLEncoder.encode(context.getString(R.string.gumi_city_code))); /*도시코드*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
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
                System.out.println(line);
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
                String busNumber = XMLTranslator.getStringValue(item, "routeno");
                String busId = XMLTranslator.getStringValue(item, "routeid");
                String busEndStationName = XMLTranslator.getStringValue(item, "endnodenm");
                String busStartStationName = XMLTranslator.getStringValue(item, "startnodenm");
                String busType = XMLTranslator.getStringValue(item, "routetp");
                allBusList.add(new BusItem(busId, busNumber, busType, busStartStationName, busEndStationName));
            }

            allBusList.sort(new Comparator<BusItem>() {
                @Override
                public int compare(BusItem bus1, BusItem bus2) {
                    return bus1.getBusNumber().compareTo(bus2.getBusNumber());
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

    public static BusItem getBusItem(String busId) {
        for(int i = 0; i < allBusList.size(); i++) {
            if(allBusList.get(i).getBusId().equals(busId)) {
                return allBusList.get(i);
            }
        }
        return null;
    }

    public static List<BusItem> getBusList(String searchString) {
        List<BusItem> list = new ArrayList<>();

        if(searchString.equals("")) {
            return allBusList;
        }

        for(int i = 0; i < allBusList.size(); i++) {
            if(allBusList.get(i).getBusNumber().contains(searchString)) {
                list.add(allBusList.get(i));
            }
        }

        return list;
    }

    public static List<BusRouteData> loadBusRouteList(Context context, String busId) {
        List<BusRouteData> list = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        String tmpBusId = busId.substring(3, busId.length());
        try {

            URL busRouteDataUrl = new URL("http://m.bis.gumi.go.kr/moMap/mBusRouteResult.do?route_id=" + tmpBusId);
            org.jsoup.nodes.Document doc = HTMLTranslator.getDocumentByUrl(busRouteDataUrl);

            org.jsoup.select.Elements elements = HTMLTranslator.getElementsByDocsQuery(doc, ".stop_list li");
            for(org.jsoup.nodes.Element element : elements) {
                org.jsoup.nodes.Element r_stop = HTMLTranslator.getElementByElementSelect(element, ".r_stop");

                String stationNumber = "";
                boolean currentBusPose = false;
                stationNumber = r_stop.select(".stop_no").text();
                stationNumber = stationNumber.replaceAll("[^0-9]", ""); //숫자 제외 지우기

                StationItem item = null;
                List<StationItem> stationList = StationController.getStationList("");
                for(int i = 0; i < stationList.size(); i++) {
                    if(stationList.get(i).getStationNumber().equals(stationNumber)) {
                        item = stationList.get(i);
                        break;
                    }
                }

                BusRouteData data = new BusRouteData(item, currentBusPose);
                list.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/BusLcInfoInqireService/getRouteAcctoBusLcList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + context.getString(R.string.tago_api_key)); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("cityCode","UTF-8") + "=" + URLEncoder.encode(context.getString(R.string.gumi_city_code))); /*도시코드*/
            urlBuilder.append("&" + URLEncoder.encode("routeId","UTF-8") + "=" + busId);/*노선ID*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
            System.out.println(sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
                String nodeord = XMLTranslator.getStringValue(item, "nodeord");

                list.get(Integer.parseInt(nodeord) - 1).setCurrentBusPose(true);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return list;
    }

}
