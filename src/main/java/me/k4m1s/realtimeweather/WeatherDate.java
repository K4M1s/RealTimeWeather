package me.k4m1s.realtimeweather;

/**
 * Class WeatherDate for Obj classes.
 */
public class WeatherDate {

    /**
     * Hours number.
     */
    int iHours;

    /**
     * Minutes number.
     */
    int iMinutes;

    /**
     * Constructor.
     *
     * @param iHours
     *   Number of hours.
     * @param iMinutes
     *   Number of minutes.
     */
    public WeatherDate(int iHours, int iMinutes) {
        this.iHours = iHours;
        this.iMinutes = iMinutes;
    }

    /**
     * Function to get hours.
     *
     * @return int
     *   Hours.
     */
    public int getHours() {
        return this.iHours;
    }

    /**
     * Function to get minutes.
     *
     * @return int
     *   Hours.
     */
    public int getMinutes() {
        return this.iHours;
    }

}
