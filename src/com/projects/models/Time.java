package com.projects.models;

/**
 * Created by Dan on 6/26/2015.
 */
public class Time
{
    enum UpdateRate
    {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS,
    }

    private boolean isAM;
    private double totalTime;
    private double timeOfDay;
    private int day;
    private int week;
    private int month;
    private int year;
    private UpdateRate updateRate;

    private static final double SECONDS_IN_MINUTE = 60;
    private static final double SECONDS_IN_HOUR = 3600;
    private static final double SECONDS_IN_DAY = 86400;
    private static final double DAYS_IN_WEEK = 7;
    private static final double AVERAGE_NUMBER_OF_DAYS_IN_MONTH = 30.42;
    private static final double MONTHS_IN_YEAR = 12;
    private static final double SECONDS_IN_MONTH = SECONDS_IN_DAY * AVERAGE_NUMBER_OF_DAYS_IN_MONTH;

    public Time()
    {
        updateRate = UpdateRate.MONTHS;
        isAM = true;
        totalTime = 0;
        timeOfDay = 0;
        day = 0;
        week = 0;
        month = 0;
        year = 0;
    }

    public void tick(double deltaTime)
    {
        totalTime += modifyWithRate(deltaTime);

        day = (int)(totalTime / SECONDS_IN_DAY);
        week = (int)(day / DAYS_IN_WEEK);
        month = (int)(day / AVERAGE_NUMBER_OF_DAYS_IN_MONTH);
        year = (int)(month / MONTHS_IN_YEAR);
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

    public boolean isAM()
    {
        return isAM;
    }

    public double getTotalTime()
    {
        return totalTime;
    }

    public double getTimeOfDay()
    {
        return timeOfDay;
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
}
