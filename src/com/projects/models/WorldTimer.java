package com.projects.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by Dan on 6/26/2015.
 */
public class WorldTimer
{
    public enum UpdateRate
    {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS,
    }

    private double timeLimit;
    private boolean timeLimitReached;
    private double totalTimeInSeconds;
    private double modifiedTimeElapsedInSeconds;
    private double hour;
    private int hourOfDay;
    private int minutesOfHour;
    private int secondsOfMinute;
    private int day;
    private int week;
    private int month;
    private int year;
    private UpdateRate updateRate;
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = startDate.plusDays(1);

    public static final double SECONDS_IN_MINUTE = 60;
    public static final double SECONDS_IN_HOUR = 3600;
    public static final double SECONDS_IN_DAY = 86400;
    public static final int HOURS_IN_DAY = 24;
    public static final double DAYS_IN_WEEK = 7;
    public static final double AVERAGE_NUMBER_OF_DAYS_IN_MONTH = 28;
    public static final double MONTHS_IN_YEAR = 12;


    public WorldTimer()
    {
        timeLimitReached = false;
        updateRate = UpdateRate.SECONDS;
        timeLimit = Double.MAX_VALUE;
        reset();
    }

    public void tick(double deltaTime)
    {
        modifiedTimeElapsedInSeconds = modifyWithRate(deltaTime);

        if (totalTimeInSeconds + modifiedTimeElapsedInSeconds > timeLimit)
        {
            modifiedTimeElapsedInSeconds = timeLimit - totalTimeInSeconds;
            timeLimitReached = true;
        }

        totalTimeInSeconds += modifiedTimeElapsedInSeconds;

        hour = (totalTimeInSeconds / SECONDS_IN_HOUR);
        day = (int)(totalTimeInSeconds / SECONDS_IN_DAY);
        week = (int)(day / DAYS_IN_WEEK);
        month = (int)(day / AVERAGE_NUMBER_OF_DAYS_IN_MONTH);
        year = (int)(month / MONTHS_IN_YEAR);
        hourOfDay =  (int)Math.floor(hour % HOURS_IN_DAY);
        minutesOfHour = (int)Math.floor((totalTimeInSeconds % SECONDS_IN_HOUR) / 60);
        secondsOfMinute = (int)Math.floor((totalTimeInSeconds % SECONDS_IN_MINUTE));
    }

    private double modifyWithRate(double deltaTime)
    {
        double time = deltaTime;

        switch (updateRate)
        {
            case SECONDS:
            {
                // it's in seconds by default so do nothing here
            } break;
            case MINUTES:
            {
                time *= SECONDS_IN_MINUTE;
            } break;
            case HOURS:
            {
                time *= SECONDS_IN_HOUR;
            } break;
            case DAYS:
            {
                time *= SECONDS_IN_DAY;
            } break;
            case WEEKS:
            {
                time *= (DAYS_IN_WEEK * SECONDS_IN_DAY);
            } break;
            case MONTHS:
            {
                time *= (AVERAGE_NUMBER_OF_DAYS_IN_MONTH * SECONDS_IN_DAY);
            } break;
            case YEARS:
            {
                time *= (MONTHS_IN_YEAR * AVERAGE_NUMBER_OF_DAYS_IN_MONTH * SECONDS_IN_DAY);
            }

        }

        return time;
    }

    public void reset()
    {
        timeLimitReached = false;
        hourOfDay = 0;
        minutesOfHour = 0;
        secondsOfMinute = 0;
        totalTimeInSeconds = 0;
        modifiedTimeElapsedInSeconds = 0;
        hour = 0;
        day = 0;
        week = 0;
        month = 0;
        year = 0;
    }

    public void setUpdateRate(UpdateRate updateRate)
    {
        this.updateRate = updateRate;
    }

    public UpdateRate getUpdateRate()
    {
        return updateRate;
    }

    public int getHourOfDay()
    {
        return hourOfDay;
    }

    public int getMinutesOfHour()
    {
        return minutesOfHour;
    }

    public int getSecondsOfMinute()
    {
        return secondsOfMinute;
    }

    public double getTotalTimeInSeconds()
    {
        return totalTimeInSeconds;
    }

    public double getModifiedTimeElapsedInSeconds() {
        return modifiedTimeElapsedInSeconds;
    }

    public double getHour()
    {
        return hour;
    }

    public int getDay()
    {
        return day;
    }

    public int getWeek()
    {
        return week;
    }

    public int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
        timeLimit = ChronoUnit.DAYS.between(startDate, endDate) * SECONDS_IN_DAY; // / TODO: set this in its own method
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        timeLimit = ChronoUnit.DAYS.between(startDate, endDate) * SECONDS_IN_DAY; // TODO: set this in its own method
    }

    public Boolean isTimeLimitReached()
    {
        return timeLimitReached;
    }
}
