package com.example.weatherapp01;

import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


public class RemoteFetch {
    private static final String fireZoneURL =
            "https://api.weather.gov/points/%s,%s";

    private static final String alertsURL =
            "https://api.weather.gov/alerts/active?area=%s";


    public static ArrayList<HashMap<String, String>> getAlert(double longitude, double latitude) {
        System.out.println("First one!!!!!!!!!!!!!!!!!!!");
        // Get the FireZone from the points API
        HashMap property_dict = getFireZone(longitude,latitude);
        return getAlert(true, property_dict);
    }

    // getAlerts takes in two doubles that represent the latitude and longitude of the location of the user and returns
    // a HashMap containing the area description and any active alerts for the area.
    // The method returns an empty HashMap when there are no active alerts for the area.
    public static ArrayList<HashMap<String, String>> getAlert(boolean haveLocation, HashMap<String, String>property_dict) {
        try {
            // Now get the alerts from the alerts API.
            java.net.URL url = new java.net.URL(String.format(alertsURL, property_dict.get("State")));
            System.out.println("I'm the URL: " + url);
            java.net.HttpURLConnection alertsConnection =
                    (java.net.HttpURLConnection) url.openConnection(); //Make connection to the URL
            alertsConnection.setRequestProperty("User-Agent", "Test");

            BufferedReader alertsReader = new BufferedReader(
                    new InputStreamReader(alertsConnection.getInputStream()));

            StringBuffer jsonString = new StringBuffer(1024);
            String line = "";
            while ((line = alertsReader.readLine()) != null)
                jsonString.append(line).append("\n");
            alertsReader.close();

            // Extract the headline, areaDesc, and description from the returned data.
            ArrayList<HashMap<String, String>>resultList = new ArrayList<HashMap<String, String>>();
            JSONObject data = new JSONObject(jsonString.toString());
            JSONArray features = (JSONArray) data.get("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject obj = (JSONObject) features.get(i);
                JSONObject properties = obj.getJSONObject("properties");
                JSONArray affectedZones = (JSONArray) properties.get("affectedZones");

                int numAffectedZones = 0;
                for (int j = 0; j < affectedZones.length(); j++) {
                    HashMap<String, String> alert_dict = new HashMap<String, String>();
                    numAffectedZones++;
                    // If we have the "Fire Weather Zone" use it to filter alerts, else return
                    // all alerts for the state.
                    if (!property_dict.containsKey("Fire Weather Zone") ||
                       affectedZones.get(j).equals(property_dict.get("Fire Weather Zone"))) {
                        JSONObject fireProperties = (JSONObject) obj.get("properties");
                        alert_dict.put("Area Description", (String) fireProperties.get("areaDesc"));
                        alert_dict.put("Alert", (String) fireProperties.get("headline"));
                        alert_dict.put("Alert Description", (String) fireProperties.get("description"));
                        resultList.add(alert_dict);
                        if (!haveLocation && numAffectedZones == 1) {
                            break;
                        }
                        if (property_dict.containsKey("Fire Weather Zone")) {
                            break;
                        }
                    }
                }
            }
            return resultList;
        } catch (Exception e) {
            System.out.println("Exception from the alerts API: " + e.toString());
            return null;
        }
    }

    public static HashMap<String, String> getFireZone(double longitude, double latitude) {
        final String pointsURL =
                "https://api.weather.gov/points/%s,%s";
        try {
            java.net.URL url = new java.net.URL(String.format(pointsURL, latitude, longitude)); //Constructing the URL
            System.out.println("I'm the URL: " + url);
            java.net.HttpURLConnection connection =
                    (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Test");


            // Create a BufferedReader to hold the response from the API
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            // Get each line from BufferedReader and append to the StringBulder.
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Extract needed information from the response.
            JSONObject json = new JSONObject(sb.toString());
            JSONObject properties = json.getJSONObject("properties");
            String fireWeatherZone = properties.getString("forecastZone");
            JSONObject relativeLocation = properties.getJSONObject("relativeLocation");
            JSONObject propertiesSecond = relativeLocation.getJSONObject("properties");
            String city = propertiesSecond.getString("city");
            String state = propertiesSecond.getString("state");
            HashMap<String, String> property_dict = new HashMap<String, String>();
            property_dict.put("City", city);
            property_dict.put("State", state);
            property_dict.put("Fire Weather Zone", fireWeatherZone);

            return property_dict;

        } catch (Exception e) {
            System.out.println("Exception from the points API: " + e.toString());
            return null;
        }
    }
}


