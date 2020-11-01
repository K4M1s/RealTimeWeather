package me.k4m1s.realtimeweather;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherAPI {
    String city;
    String country;
    String APIKey;

    public void setCityCode(String cityCode) throws Exception {
        String[] split = cityCode.split(",");
        if (split.length > 1 && split[1] != null) {
            city = URLEncoder.encode(split[0], StandardCharsets.UTF_8.toString());
            country = split[1];
        } else if(split.length == 1) {
            city = URLEncoder.encode(split[0], StandardCharsets.UTF_8.toString());
            country = null;
        } else {
            throw new Exception("City code is invalid");
        }
    }

    public void setAPIKey(String key) {
        APIKey = key;
    }

    public JsonObject getWeatherData() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&appid="+APIKey+"&units=metric").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            if (con.getResponseCode() != 200) {
                return null;
            }
            String responseString = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            return new JsonParser().parse(responseString).getAsJsonObject();
        } catch (Exception ex) {
            System.out.println("[RTW] Error: " + ex.getMessage());
            return null;
        }
    }



}