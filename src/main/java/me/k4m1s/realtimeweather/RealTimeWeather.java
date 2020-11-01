package me.k4m1s.realtimeweather;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealTimeWeather extends JavaPlugin {

    private static RealTimeWeather instance;
    private static WeatherAPI weatherAPI;

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;
        weatherAPI = new WeatherAPI();

        String APIKey = config.getString("APIKey");
        String cityName = config.getString("cityName");
        String countryCode = config.getString("countryCode");

        if (APIKey == null || APIKey.isEmpty() || APIKey.equalsIgnoreCase("ENTERYOURAPIKEYHERE")) {
            System.out.println("[RTW] Warning: Disabling plugin because of default or empty API KEY");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (cityName == null || cityName.isEmpty()) {
            System.out.println("[RTW] Warning: Disabling plugin because of empty city name");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (countryCode == null || countryCode.isEmpty()) {
            System.out.println("[RTW] Warning: Disabling plugin because of empty country code");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            weatherAPI.setAPIKey(config.getString("APIKey"));
            weatherAPI.setCityCode(config.getString("cityName") + "," + config.getString("countryCode"));
            System.out.println(weatherAPI.getWeatherData());
        } catch(Exception e) {
            getServer().getPluginManager().disablePlugin(this);
            System.out.println("[RTW] City name or country code is invalid.");
        }
        WeatherManager.startTimer();

        // Disable Day light and Weather Cycle
        for(World world: Bukkit.getWorlds()) {
            if (config.getBoolean("realtime")) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
            if (config.getBoolean("realtimeweather")) {
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }

        }
    }

    @Override
    public void onDisable() {
        WeatherManager.stopTimer();
    }

    public static RealTimeWeather getInstance() {
        return instance;
    }

    public static WeatherAPI getWeatherAPI() {
        return weatherAPI;
    }
}
