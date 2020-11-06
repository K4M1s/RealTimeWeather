package me.k4m1s.realtimeweather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Class WeatherManager for extra functionality.
 */
public class WeatherManager {

    /**
     * BukkitTask timer.
     */
    private static BukkitTask bkTimer;

    /**
     * Global timer which in case of real time & weather will change it in minecraft worlds.
     */
    public static void startTimer() {
        boolean bRealTimeWeather = RealTimeWeather.getPluginConfig().getBoolean("realTimeWeather", true);
        boolean bRealTime = RealTimeWeather.getPluginConfig().getBoolean("realTime", true);
        int iRefreshTime = RealTimeWeather.getPluginConfig().getInt("refreshTime", 3);

        bkTimer = new BukkitRunnable() {
            public void run() {
                if (!bRealTimeWeather && !bRealTime) {
                    return;
                }

                JsonObject jsonString = RealTimeWeather.getWeatherAPI().getWeatherData();

                if (jsonString == null) {
                    System.out.println("[RTW] Error: JSON data from API is invalid");
                    return;
                }

                JsonArray weatherArr = jsonString.get("weather").getAsJsonArray();

                if(weatherArr.size() < 0) {
                    System.out.println("[RTW] Error: JSON data from API is invalid");
                    return;
                }

                for (World world: Bukkit.getWorlds()) {
                    if (bRealTimeWeather) {
                        switch (weatherArr.get(0).getAsJsonObject().get("main").getAsString()) {
                            case "Rain":
                                world.setStorm(true);
                                world.setWeatherDuration(500);
                                break;
                            case "Thunder":
                                world.setThundering(true);
                                world.setWeatherDuration(500);
                                break;
                            default:
                                world.setStorm(false);
                                world.setThundering(false);
                                break;
                        }
                    }

                    if (bRealTime) {
                        world.setTime(WeatherManager.getMinecraftRealTime(WeatherManager.getTimeZoneTimeByOffset(jsonString.get("timezone").getAsInt())));
                    }
                }
            }
        }.runTaskTimer(RealTimeWeather.getInstance(), 0L, iRefreshTime * 20L);
    }

    /**
     * Function to stop timer.
     */
    public static void stopTimer() {
        if (bkTimer != null) bkTimer.cancel();
    }

    /**
     * Function to converts real time to minecraft ticks.
     *
     * @param date
     *   WeatherDate object containing current time.
     *
     * @return int
     *   Minecraft ticks representing real time.
     */
    public static int getMinecraftRealTime(WeatherDate date) {
        // Minecraft one minute in ticks.
        double dMinute = 16.666666666666666666666666666667;

        // Tick results.
        int result = (int) Math.ceil((((date.getHours() * 60) + date.getMinutes()) * dMinute) - 6000);

        // Adjust time in case of negative number.
        return result < 0 ? 24000 + result : result;
    }

    /**
     * Converts Timezone offset in seconds to time in that timezone.
     *
     * @param offset
     *   Timezone offset in seconds.
     *
     * @return WeatherDate
     *   Object representing time in given timezone.
     */
    public static WeatherDate getTimeZoneTimeByOffset(int offset) {
        String[] availableIDs = TimeZone.getAvailableIDs(offset*1000);

        if (availableIDs.length > 0 && availableIDs[0] != null) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone(availableIDs[0]));

            String[] timeFormat = dateFormat.format(new Date()).split(":");

            return new WeatherDate(Integer.parseInt(timeFormat[0]), Integer.parseInt(timeFormat[1]));
        }

        return new WeatherDate(0, 0);
    }

    /**
     * Checks if there is misconfiguration in config.
     *
     * @param hsConfig
     *   HashMap with apiKey, cityName and countryCode.
     *
     * @return boolean
     *   True if config is valid, false otherwise.
     */
    public static boolean isConfigValid(HashMap<String, String> hsConfig) {
        if (hsConfig.get("apiKey").isEmpty() || hsConfig.get("apiKey").equalsIgnoreCase("ENTER_YOUR_API_KEY_HERE")) {
            System.out.println("[RTW] Warning: Disabling plugin because of default or empty API KEY");
            return false;
        }

        if (hsConfig.get("cityName").isEmpty()) {
            System.out.println("[RTW] Warning: Disabling plugin because of empty city name");
            return false;
        }

        if (hsConfig.get("countryCode").isEmpty()) {
            System.out.println("[RTW] Warning: Disabling plugin because of empty country code");
            return false;
        }

        return true;
    }

}
