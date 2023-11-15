package com.example.weatherapp01;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NoLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Spinner sp;
        String states[] = {"AK", "AL", "AR","AZ","CA","CO","CT","DC","DE","FL","GA","HI","IA","ID","IL","KS","KY","LA","MA","MD","ME","MI","MN","MO", "MS","MT","NC",
                "ND","NE","NH","NJ", "NM","NM", "NY", "OH","OK","OR","PA","RI", "SC", "SD","TN","TX","UT","VA","VT","WA","WI","WV","WY", };

        ArrayAdapter<String> adapter;

        EditText num;
        Button submit;
        String state = "";
    ArrayList<HashMap<String, String>> headline = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> fireZone = new HashMap<String, String>() ;
    ArrayList<HashMap<String, String>> shelters = new ArrayList<HashMap<String, String>>();


    @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.no_location);

            sp = (Spinner)findViewById(R.id.state);
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, states);

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(this);

            num = (EditText) findViewById(R.id.zipCode);
            submit = (Button)findViewById(R.id.submit);

            submit.setOnClickListener(new View.OnClickListener(){
                @Override

                public void onClick(View v){
                    String zipCode = "";
                    if(num.getText().toString().length() == 0 || num.getText().toString().length() != 5 ){
                        System.out.println("Not Possible");
                    }
                    else {
                        //int zipCode = Integer.parseInt(num.getText().toString());
                        zipCode = num.getText().toString();
                    }
                    sp.setVisibility(View.INVISIBLE);
                    final String  temp = zipCode;
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            callRemoteFetch(0.0, 0.0, "", temp, state);
                        }
                    });
                }
            });

            //EditText zipCodeInput = (EditText) findViewById(R.id.zipCodeInput);
            //int zipCode = Integer.valueOf(zipCodeInput.getText().toString());
            //zipCodeInput.setText(zipCode + "");
            //System.out.println(zipCode);
            //showToast(String.valueOf(zipCode));
        }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        state = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("NewApi")
    public void callRemoteFetch(double longitude, double latitude, String cityName, String zipCode, String state){
        if (latitude > 0 && longitude > 0) {
            headline = RemoteFetch.getAlert(latitude, longitude);
            fireZone = RemoteFetch.getFireZone(latitude, longitude);
            shelters = RemoteShelter.findShelter(cityName, (String) fireZone.get("State"),
                    zipCode);
        } else {
            String city = "";
            HashMap<String, String>property_dict = new HashMap<String, String>();
            property_dict.put("State", state);
            property_dict.put("City", city);

            headline = RemoteFetch.getAlert(false, property_dict);
            shelters = RemoteShelter.findShelter(city, state, zipCode);
        }


        // Update the UI
        runOnUiThread(new Runnable() {

            public void run() {
                setContentView(R.layout.activity_main);

                TextView headlineText = (TextView) findViewById(R.id.headlineText);
                if (headline.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Found no Active Alerts for your area!");
                    // REMOVE LATER TESTING ONLY

                    //JSONObject json = new JSONObject(sb.toString());
                    for (int i = 0; i < shelters.size(); i++) {
                        String shelterName = shelters.get(i).get("Shelter Name");
                        String evacuationCapacity = shelters.get(i).get("Evacuation Capacity");
                        String shelterStatus = shelters.get(i).get("Shelter Status");

                        sb.append("\n");
                        sb.append("\n         Shelter Info Below:");
                        sb.append("\nShelter Name: " + shelterName + "\n");
                        sb.append("Evacuation Capacity: " + evacuationCapacity + "\n");
                        sb.append("Shelter Status " + shelterStatus + "\n");
                    }
                    headlineText.setText(sb.toString());

                } else {
                    headlineText.setText(headline + "");
                    for (int i = 0; i < shelters.size(); i++) {
                        String shelterName = shelters.get(i).get("Shelter Name");
                        String evacuationCapacity = shelters.get(i).get("Evacuation Capacity");
                        String shelterStatus = shelters.get(i).get("Shelter Status");
                        headlineText.setText("Shelter Name: " + shelterName);
                        headlineText.setText("Evacuation Capacity: " + evacuationCapacity);
                        headlineText.setText("Shelter Status " + shelterStatus);

                    }
                }
            }
        });
    }
}

