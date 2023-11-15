package com.example.weatherapp01;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> finalList;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<HashMap<String, String>> headline, ArrayList<HashMap<String, String>> shelters) {
        this.context = context;
        //this.alertList = new ArrayList<>(headline);
        finalList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < headline.size(); i++) {
            finalList.add(headline.get(i));
        }
        for (int i = 0; i < shelters.size(); i++) {
            finalList.add(shelters.get(i));
        }


        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return finalList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_listview, null);
        TextView alertDesc = (TextView) view.findViewById(R.id.alertDesc);
        TextView areaDesc = (TextView) view.findViewById(R.id.areaDesc);
        TextView desc = (TextView) view.findViewById(R.id.desc);



        if (finalList.get(i).get("Alert Description") != null) {
            alertDesc.setText("ALERT DESCRIPTION: " + finalList.get(i).get("Alert Description") + "\n");
        }
        if (finalList.get(i).get("Area Description") != null) {
            areaDesc.setText("AREA DESCRIPTION: " + finalList.get(i).get("Area Description") + "\n");
        }
        if (finalList.get(i).get("Alert") != null) {
            desc.setText("ALERT: " + finalList.get(i).get("Alert") + "\n");
        }
        if(finalList.get(i).get("Shelter Name") != null){
            desc.setText("SHELTER NAME: " + finalList.get(i).get("Shelter Name") + "\n");

        }

        if(finalList.get(i).get("Shelter Status") != null){
            areaDesc.setText("SHELTER STATUS: " + finalList.get(i).get("Shelter Status") + "\n");
        }
        if(finalList.get(i).get("Evacuation Capacity") != null){
            alertDesc.setText("EVACUATION CAPACITY: " + finalList.get(i).get("Evacuation Capacity") + "\n");
        }
        return view;
    }

}
