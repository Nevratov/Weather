package com.demo.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InputCityActivity extends AppCompatActivity {

    AutoCompleteTextView editTextInputCity;
    private static JSONObject jsonObject;

    private ArrayList<String> russianCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_city);

        downloadCitiesInArrayFromJson();

        editTextInputCity = findViewById(R.id.autoCompleteTextViewInputCity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, russianCities);
        editTextInputCity.setAdapter(adapter);

    }

    public void downloadCitiesInArrayFromJson() {
        AssetManager manager = getAssets();
        try {
            InputStream inputStream = manager.open("russian_cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArrayCities = new JSONArray(jsonString);

            russianCities = new ArrayList<>();

            for (int i = 0; i < jsonArrayCities.length(); i++) {
                String name = jsonArrayCities.getJSONObject(i).getString("name");
                String subject = jsonArrayCities.getJSONObject(i).getString("subject");
                russianCities.add(name + ", " + subject);
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void onClickShowWeather(View view) {
        createUrlForJSONObject();
        if (jsonObject != null && statusCode()) {
            Intent intent = new Intent(this, WeatherShowActivity.class);
            intent.putExtra("json", jsonObject.toString());
            startActivity(intent);
        } else
            showToastInvalidCity();

    }

    private void createUrlForJSONObject() {
        String city = getCity();
        if (city != null) {
            String urlJSON = String.format(getString(R.string.url_json_template), city);
            createJSONObject(urlJSON);
        }
    }

    private void createJSONObject(String urlJSON) {
        try {
            DownloadJSON task = new DownloadJSON();
            String json = task.execute(urlJSON).get();
        if (json != null)
            jsonObject = new JSONObject(json);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showToastInvalidCity() {
        Toast.makeText(this, R.string.toast_wrong_input_city, Toast.LENGTH_SHORT).show();
    }

    private String getCity() {
        String city = editTextInputCity.getText().toString().trim();
        if (!city.isEmpty())
            return city;
        else
            return null;
    }

    private boolean statusCode() {
        try {
            String cod = jsonObject.getString("cod");
            return cod.equals("200");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private static class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }
}