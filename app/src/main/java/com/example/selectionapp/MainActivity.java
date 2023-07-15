package com.example.selectionapp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button mainImage;
    InputStream badAvg, goodAvg;
    List<Double> goodRange = new ArrayList<>();
    List<Double> badRange = new ArrayList<>();
    String[] goodRage;
    String[] badRage;
    double goodMin, goodMax, badMin, badMax;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainImage = findViewById(R.id.button);


        Intent serviceIntent = new Intent(this, BrainWave.class);
        startService(serviceIntent);

        goodAvg = getResources().openRawResource(R.raw.good);
        BufferedReader reader = new BufferedReader(new InputStreamReader(goodAvg));
        try {
            String csvline;
            while ((csvline = reader.readLine()) != null) {
                goodRage = csvline.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodRage).mapToDouble(Double::parseDouble).toArray();
                    goodRange.add(Double.valueOf(goodRage[0] + ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (goodRange.size() > 0) {
                Collections.sort(goodRange);
                goodMin = goodRange.get(0);
                goodMax = goodRange.get(goodRange.size() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        badAvg = getResources().openRawResource(R.raw.bad);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(badAvg));
        try {
            String csvline1;
            while ((csvline1 = reader1.readLine()) != null) {
                badRage = csvline1.split(",");
                try {
                    //double[] doubleArray = Arrays.stream(goodRage).mapToDouble(Double::parseDouble).toArray();
                    badRange.add(Double.valueOf(badRage[0] + ""));
                } catch (Exception e) {
                    Log.e("TAG2", "error : " + e);
                    e.printStackTrace();
                }
            }

            if (badRange.size() > 0) {
                Collections.sort(badRange);
                badMin = badRange.get(0);
                badMax = badRange.get(badRange.size() - 1);
            }


        } catch (Exception e) {
            Log.e("TAG2", "error 2 : " + e);
            e.printStackTrace();
        }


        mainImage.setOnClickListener(view -> {
            /*Intent intent = new Intent(MainActivity.this, OptionActivity.class);

            startActivity(intent);
*/
            Log.e("TAG", "goodMin : " + goodMin);
            Log.e("TAG", "goodMax : " + goodMax);
            Log.e("TAG", "badMin : " + badMin);
            Log.e("TAG", "badMax : " + badMax);

        });

    }


}