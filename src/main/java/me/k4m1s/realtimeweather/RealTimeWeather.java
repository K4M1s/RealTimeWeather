package me.k4m1s.realtimeweather;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Main class for RealTimeWeather plugin.
 */
public final class RealTimeWeather extends JavaPlugin {

    /**
     * RealTimeWeather plugin instance.
     */
    private static RealTimeWeather instance;

    /**
     * Weather API instance.
     */
    private static WeatherAPI weatherAPI;

    /**
     * File configuration - main config.
     */
    private static FileConfiguration config;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        // Save default config.
        this.saveDefaultConfig();

        // Initialize instances.
        instance = this;
        weatherAPI = new WeatherAPI();
        config = getConfig();

        // Prepare config variables.
        HashMap<String, String> hsConfig = new HashMap<>();
        hsConfig.put("apiKey", config.getString("APIKey", "ENTER_YOUR_API_KEY_HERE"));
        hsConfig.put("cityName", config.getString("cityName", "Warsaw"));
        hsConfig.put("countryCode", config.getString("countryCode", "pl"));

        // Check all config variables.
        if (!WeatherManager.isConfigValid(hsConfig)) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Pass data to weather api.
        weatherAPI.setAPIKey(config.getString("APIKey"));
        weatherAPI.setCityCode(config.getString("cityName"), config.getString("countryCode"));

        // Start weather download data timer.
        WeatherManager.startTimer();

        // Disable Day light and Weather Cycle based on config settings.
        for (World world: Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !config.getBoolean("realTime", true));
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, !config.getBoolean("realTimeWeather", true));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        WeatherManager.stopTimer();
    }

    /**
     * Get Main plugin instance for other tasks.
     *
     * @return RealTimeWeather
     *   Main plugin instance.
     */
    public static RealTimeWeather getInstance() {
        return instance;
    }

    /**
     * Get Weather API instance for extra functionality.
     *
     * @return WeatherAPI
     *   Weather API instance.
     */
    public static WeatherAPI getWeatherAPI() {
        return weatherAPI;
    }

    /**
     * Get plugin config instance.
     *
     * @return FileConfiguration
     *   Config file configuration.
     */
    public static FileConfiguration getPluginConfig() {
        return config;
    }

}
