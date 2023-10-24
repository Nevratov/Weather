package com.demo.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeatherShowActivity extends AppCompatActivity {

    private JSONObject jsonObject;
    private ArrayList<JSONObject> jsonObjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_show);
        getJsonObject();
        setJSONObjectList();

        setCityOnTop();
        setWeatherOnCurrentTime();
        setTimeForDay();
        setDegreeForDay();
        setIcoForDay();
    }

    private void setCityOnTop() {
        TextView textViewCity = findViewById(R.id.textViewCity);
        try {
            String city = jsonObject.getJSONObject("city").getString("name");
            textViewCity.setText(city);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setWeatherOnCurrentTime() {
        setDegreeOnCurrentTime();
        setSignOnCurrentTime();
        setFeelingTheTemperatureOnCurrentTime();
        setDescriptionOnCurrentTime();
        setIcoOnCurrentTime();

    }

     private void setDegreeOnCurrentTime() {
         TextView textViewDegreesMain = findViewById(R.id.textViewDegreesMain);
         String degree = getDegrees(jsonObjectList.get(0)) + "°";
         textViewDegreesMain.setText(degree);
     }

     private void setSignOnCurrentTime() {
         TextView textViewSignMain = findViewById(R.id.textViewSignMain);
         setSign(textViewSignMain, jsonObjectList.get(0));
     }

     private void setFeelingTheTemperatureOnCurrentTime() {
         TextView textViewFeelingTheTemperature = findViewById(R.id.textViewFeelingTheTemperature);
         setFeelingDegrees(textViewFeelingTheTemperature, jsonObjectList.get(0));
     }

     private void setDescriptionOnCurrentTime() {
         TextView textViewDescriptionWeather = findViewById(R.id.textViewDescriptionWeather);
         setDescriptionWeather(textViewDescriptionWeather, jsonObjectList.get(0));
     }

     private void setIcoOnCurrentTime() {
         ImageView imageViewIcoCurrentWeather = findViewById(R.id.imageViewIcoCurrentWeather);
         setIcoCurrentWeather(imageViewIcoCurrentWeather, jsonObjectList.get(0));
     }

    private void setTimeForDay() {
        int cnt = getCnt();
        for (int i = 0; i < cnt; i++) {
            JSONObject jsonObjectNumber = jsonObjectList.get(i);

            String textViewTimeName = "textViewTime" + i;
            int textViewTimeRes = getResources().getIdentifier(textViewTimeName, "id", getPackageName());
            TextView textViewTime = findViewById(textViewTimeRes);
            setTime(textViewTime, jsonObjectNumber);
        }
    }

    private void setDegreeForDay() {
        int cnt = getCnt();
        for (int i = 0; i < cnt; i++) {
            JSONObject jsonObjectNumber = jsonObjectList.get(i);

            String textViewDegreesName = "textViewDegrees" + i;
            int textViewDegreesRes = getResources().getIdentifier(textViewDegreesName, "id", getPackageName());
            TextView textViewDegrees = findViewById(textViewDegreesRes);
            setDegrees(textViewDegrees, jsonObjectNumber);
        }
    }

    private void setIcoForDay() {
        int cnt = getCnt();
        for (int i = 0; i < cnt; i++) {
            JSONObject jsonObjectNumber = jsonObjectList.get(i);

            String imageViewIcoName = "imageViewIco" + i;
            int imageViewIcoRes = getResources().getIdentifier(imageViewIcoName, "id", getPackageName());
            ImageView imageView = findViewById(imageViewIcoRes);
            setIcoCurrentWeather(imageView, jsonObjectNumber);
        }
    }

    void setJSONObjectList() {
        jsonObjectList = new ArrayList<>();
        int cnt = getCnt();
        for (int i = 0; i < cnt; i++ ) {
            try {
                jsonObjectList.add(jsonObject.getJSONArray("list").getJSONObject(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getCnt() {
        try {
            return jsonObject.getInt("cnt");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getJsonObject() {
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("json");
        try {
            if (jsonString != null)
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDegrees(TextView textView, JSONObject jsonObjectNumber) {
        int degreesInt = getDegrees(jsonObjectNumber);
        String sign = getSign(jsonObjectNumber);
        String degreesString = sign + degreesInt + "°";
        textView.setText(degreesString);
    }

    private int getDegrees(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getJSONObject("main").getInt("temp");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFeelingDegrees(TextView textView, JSONObject jsonObjectNumber) {
        int feelingDegreesInt = getFeelingTheTemperature(jsonObjectNumber);
        String sign = getSign(jsonObjectNumber);
        String feelingDegreesString = getString(R.string.feeling) + sign + feelingDegreesInt + "°";
        textView.setText(feelingDegreesString);
    }

    private int getFeelingTheTemperature(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getJSONObject("main").getInt("feels_like");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSign(TextView textView, JSONObject jsonObjectNumber) {
        String sign = getSign(jsonObjectNumber);
        textView.setText(sign);
    }

    private String getSign(JSONObject jsonObjectNumber) {
        String sigh = "";
        int degrees = getDegrees(jsonObjectNumber);
        if (degrees > 0)
            sigh = "+";
        return sigh;
    }

    private void setDescriptionWeather(TextView textView, JSONObject jsonObjectNumber) {
            String description = getDescriptionWeather(jsonObjectNumber);
            String descriptionFirstUpperCase = description.substring(0, 1).toUpperCase() + description.substring(1);
            textView.setText(descriptionFirstUpperCase);
    }

    private String getDescriptionWeather(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getJSONArray("weather").getJSONObject(0).getString("description");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setIcoCurrentWeather(ImageView imageView, JSONObject jsonObjectNumber) {
        String weather = getWeatherMain(jsonObjectNumber).toLowerCase();

        int imgResources = getResources().getIdentifier(weather, "drawable", getPackageName());
        imageView.setImageResource(imgResources);

    }

    private String getWeatherMain(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getJSONArray("weather").getJSONObject(0).getString("main");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTime(TextView textView, JSONObject jsonObjectNumber) {
        long timeUnix = getTimeUnix(jsonObjectNumber);
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timeUnix, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH.mm");
        String time = dateTime.format(formatter);
        textView.setText(time);
    }

    private long getTimeUnix(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getLong("dt");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}