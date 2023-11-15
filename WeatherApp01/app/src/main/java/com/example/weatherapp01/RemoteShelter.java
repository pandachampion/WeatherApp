package com.example.weatherapp01;



import org.json.JSONObject;
import org.json.JSONArray;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


public class RemoteShelter {
    private static final String shelterURL =
            "https://gis.fema.gov/arcgis/rest/services/NSS/FEMA_NSS/FeatureServer/5/query?where=CITY='%s'&ZIP=%s&outFields=*&outSR=4326&f=json";

    public static ArrayList<HashMap<String,String>> findShelter(String cityName, String stateName, String zipCode) {
        try {
            java.net.URL url = new java.net.URL(String.format(shelterURL, cityName, zipCode)); //Constructing the URL
            System.out.println("I'm the Shelters URL: " + url);
            java.net.HttpURLConnection connection =
                    (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Test");


            // Create a BufferedReader to hold the response from the API
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            // Get each line from BufferedReader and append to the StringBuilder.
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());
            JSONArray features = json.getJSONArray("features");

            System.out.println(features.length());

            ArrayList resultList = new ArrayList<HashMap<String,String>>();
            for(int i = 0; i < features.length(); i ++){
                JSONObject jsonObject = (JSONObject)features.get(i);
                JSONObject attributes = (JSONObject)jsonObject.get("attributes");
                System.out.println("attributes: " + attributes);
                HashMap<String, String> shelter_dict = new HashMap<String, String>();
                String shelterName = attributes.getString("SHELTER_NAME");
                String evacuationCapacity = attributes.getString("EVACUATION_CAPACITY");
                String shelterStatus = attributes.getString("SHELTER_STATUS_CODE");

                shelter_dict.put("Shelter Name", shelterName);
                shelter_dict.put("Evacuation Capacity", evacuationCapacity);
                shelter_dict.put("Shelter Status", shelterStatus);
                resultList.add(shelter_dict);
                //System.out.println("Shelter Name: " + shelterName + " Evacuation Capacity: " + evacuationCapacity + " Shelter Status: " + shelterStatus);
            }

            System.out.println("Size of the RemoteShelter output : " + resultList.size());
            return resultList;
        }
            catch(Exception e){
                    System.out.println("Exception from the FEMA Shelters API: " + e.toString());
                    return null;
            }

    }
}

