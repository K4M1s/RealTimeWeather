package me.k4m1s.realtimeweather;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Class WeatherAPI for containing functions related to OpenWeatherMap.
 */
public class WeatherAPI {

    /**
     * City name from config.
     */
    String city;

    /**
     * Country ISO code from config.
     */
    String country;

    /**
     * API Key from config.
     */
    String key;

    /**
     * Function to set city code for download purposes.
     *
     * @param city
     *   City name.
     * @param country
     *   Country ISO code.
     */
    public void setCityCode(String city, String country) {
        this.city = city;
        this.country = country;
    }

    /**
     * Function to set api key for download purposes.
     *
     * @param key
     *   API Key from OpenWeatherMap.org.
     */
    public void setAPIKey(String key) {
        this.key = key;
    }

    /**
     * Get Weather data for given city.
     *
     * @return JsonObject
     *   Parsed json object.
     */
    public JsonObject getWeatherData() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + country + "&appid=" + key + "&units=metric").openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            return new JsonParser().parse(new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine()).getAsJsonObject();
        } catch (Exception e) {
            System.out.println("[RTW] Error: " + e.getMessage());
            return null;
        }
    }

}