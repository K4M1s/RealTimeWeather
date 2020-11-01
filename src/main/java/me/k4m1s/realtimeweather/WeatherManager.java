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
import java.util.TimeZone;

public class WeatherManager {
    private static BukkitTask bkTimer;

    public static void startTimer() {
        bkTimer = new BukkitRunnable() {
            public void run() {
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
                JsonObject weatherObj = weatherArr.get(0).getAsJsonObject();
                String weather = weatherObj.get("main").getAsString();
                WeatherDate time = WeatherManager.getTimeZoneTimeByOffset(jsonString.get("timezone").getAsInt());
                int ticks = WeatherManager.getMinecraftRealTime(time);
                for(World world: Bukkit.getWorlds()) {
                    if (RealTimeWeather.getInstance().config.getBoolean("realtimeweather")) {
                        switch (weather) {
                            case "Rain":
                                world.setStorm(true);
                                world.setWeatherDuration(500);
                                break;
                            case "Thunder":
                                world.setThundering(true);
                                world.setWeatherDuration(500);
                            default:
                                world.setStorm(false);
                                world.setThundering(false);
                        }
                    }
                    if (RealTimeWeather.getInstance().config.getBoolean("realtime")) {
                        world.setTime(ticks);
                    }
                }
            }
        }.runTaskTimer(RealTimeWeather.getInstance(), 0L, 60L);
    }

    public static void stopTimer() {
        if(bkTimer != null) bkTimer.cancel();
    }

    /**
     * Converts real time to minecraft ticks.
     * @param date WeatherDate object containing current time.
     * @return Minecraft ticks representing real time.
     */
    public static int getMinecraftRealTime(WeatherDate date) {
        int result = (int) Math.ceil((((date.hours * 60) + date.minutes) * 16.666666666666666666666666666667) - 6000);
        return result < 0 ? 24000 + result : result;
    }

    /**
     * Converts Timezone offset in seconds to time in that timezone.
     * @param offset Timezone offset in seconds.
     * @return WeatherDate object representing time in given timezone.
     */
    public static WeatherDate getTimeZoneTimeByOffset(int offset) {
        String[] availableIDs = TimeZone.getAvailableIDs(offset*1000);

        if (availableIDs.length > 0 && availableIDs[0] != null) {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("HH:mm");

            df.setTimeZone(TimeZone.getTimeZone(availableIDs[0]));
            String[] timeThing = df.format(date).split(":");
            return new WeatherDate(Integer.parseInt(timeThing[0]), Integer.parseInt(timeThing[1]));
        }
        return new WeatherDate(0, 0);
    }
}
