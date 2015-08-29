package com.projects.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Timer for tracking properties of the simulation including duration, update rate and current date and time.
 */
public class WorldTimer {
    public static final double SECONDS_IN_MINUTE = 60;
    public static final double SECONDS_IN_HOUR = 3600;
    public static final double SECONDS_IN_DAY = 86400;
    public static final int HOURS_IN_DAY = 24;
    private double timeLimit = Double.MAX_VALUE;
    private boolean timeLimitReached = false;
    private double totalTimeInSeconds;
    private double modifiedTimeElapsedInSeconds;
    private double hour;
    private int hourOfDay;
    private int minutesOfHour;
    private int secondsOfMinute;
    private int day;
    private UpdateRate updateRate = UpdateRate.DAYS;
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = startDate.plusDays(1);
    private LocalDate currentDate = LocalDate.now();

    public WorldTimer() {
        updateTimeLimit();
        reset();
    }

    /**
     * Move the time in the simulation forward a tick.
     *
     * @param deltaTime time in seconds since the last frame
     */
    public void tick(double deltaTime) {
        modifiedTimeElapsedInSeconds = modifyWithRate(deltaTime);

        if (totalTimeInSeconds + modifiedTimeElapsedInSeconds > timeLimit) {
            modifiedTimeElapsedInSeconds = timeLimit - totalTimeInSeconds;
            timeLimitReached = true;
        }

        totalTimeInSeconds += modifiedTimeElapsedInSeconds;

        hour = (totalTimeInSeconds / SECONDS_IN_HOUR);
        day = (int) (totalTimeInSeconds / SECONDS_IN_DAY);
        hourOfDay = (int) Math.floor(hour % HOURS_IN_DAY);
        minutesOfHour = (int) Math.floor((totalTimeInSeconds % SECONDS_IN_HOUR) / 60);
        secondsOfMinute = (int) Math.floor((totalTimeInSeconds % SECONDS_IN_MINUTE));

        currentDate = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth()).plusDays(day);
    }

    /**
     * Modifies the seconds since the last frame with a multiplier depending on the simulation's update rate.
     *
     * @param deltaTime time in seconds since the last frame
     * @return the modified time that has elapsed
     */
    private double modifyWithRate(double deltaTime) {
        double time = deltaTime;

        switch (updateRate) {
            case SECONDS: {
                // it's in seconds by default so do nothing here
            }
            break;
            case MINUTES: {
                time *= SECONDS_IN_MINUTE;
            }
            break;
            case HOURS: {
                time *= SECONDS_IN_HOUR;
            }
            break;
            case DAYS: {
                time *= SECONDS_IN_DAY;
            }
            break;

        }

        return time;
    }

    /**
     * Resets the timer so that it's ready for the simulation to run again from the start.
     */
    public void reset() {
        timeLimitReached = false;
        hourOfDay = 0;
        minutesOfHour = 0;
        secondsOfMinute = 0;
        totalTimeInSeconds = 0;
        modifiedTimeElapsedInSeconds = 0;
        hour = 0;
        day = 0;
    }

    public UpdateRate getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(UpdateRate updateRate) {
        this.updateRate = updateRate;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinutesOfHour() {
        return minutesOfHour;
    }

    public int getSecondsOfMinute() {
        return secondsOfMinute;
    }

    public double getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public double getModifiedTimeElapsedInSeconds() {
        return modifiedTimeElapsedInSeconds;
    }

    public double getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        updateTimeLimit();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        updateTimeLimit();
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    private void updateTimeLimit() {
        timeLimit = ChronoUnit.DAYS.between(startDate, endDate) * SECONDS_IN_DAY; // TODO: set this in its own method
    }

    public Boolean isTimeLimitReached() {
        return timeLimitReached;
    }

    /**
     * Different update rates for the simulation.
     */
    public enum UpdateRate {
        /**
         * One second in real life is one second in the simulation.
         */
        SECONDS,
        /**
         * One seconds in real life is one minute in the simulation.
         */
        MINUTES,
        /**
         * One second in real life is one hour in the simulation.
         */
        HOURS,
        /**
         * One second in real life is one day in the simulation.
         */
        DAYS,
    }
}
