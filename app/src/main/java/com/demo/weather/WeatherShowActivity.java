package com.demo.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class WeatherShowActivity extends AppCompatActivity {

    private JSONObject jsonObject;

    private ArrayList<JSONObject> jsonObjectList;
    private ArrayList<TextView> textViewsDegreeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_show);
        getJsonObject();


        setJSONObjectList();
        setTextViewList();

        setWeatherOnCurrentTime();

        setDescriptionWeather();
        setIcoCurrentWeather();
    }

    private void setWeatherOnCurrentTime() {
        for (int i = 0; i < getCnt(); i++) {
            setDegrees(textViewsDegreeList.get(i), jsonObjectList.get(i));
//            setPlusOrMinusSign(textViewsDegreeList.get(i), jsonObjectList.get(i));
        }

    }

    private void setTextViewList() {
        textViewsDegreeList = new ArrayList<>();
        for (int i = 0; i < getCnt(); i++) {
            String nameId = String.format(Locale.getDefault(), "textViewDegrees%d", i);
            int id = getResources().getIdentifier(nameId, "id", getPackageName());
            TextView textView = findViewById(id);
            textViewsDegreeList.add(textView);
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
        String degreesString = String.valueOf(degreesInt);
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
        String feelingDegreesString = String.valueOf(feelingDegreesInt);
        textView.setText(feelingDegreesString);
    }

    private int getFeelingTheTemperature(JSONObject jsonObjectNumber) {
        try {
            return jsonObjectNumber.getJSONObject("main").getInt("feels_like");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPlusOrMinusSign(TextView textView, JSONObject jsonObjectNumber) {
        String sign = getPlusOrMinusSign(jsonObjectNumber);
        textView.setText(sign);
    }

    private String getPlusOrMinusSign(JSONObject jsonObjectNumber) {
        String sigh = "";
        int degrees = getDegrees(jsonObjectNumber);
        if (degrees > 0)
            sigh = "+";
        else if (degrees < 0)
            sigh = "-";
        return sigh;
    }

    private void setDescriptionWeather() {
        TextView textViewDescriptionWeather = findViewById(R.id.textViewDescriptionWeather);
        try {
            String description = jsonObjectList.get(0).getJSONArray("weather").getJSONObject(0).getString("description");
            textViewDescriptionWeather.setText(description);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setIcoCurrentWeather() {
        //TODO Сделать зависсимость: Разная погода - разные иконки
        ImageView imageViewIcoCurrentWeather = findViewById(R.id.imageViewIcoCurrentWeather);
        imageViewIcoCurrentWeather.setImageResource(R.drawable.cloud);
    }
}