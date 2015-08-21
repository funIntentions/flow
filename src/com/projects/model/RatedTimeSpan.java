package com.projects.model;

import java.time.LocalTime;

/**
 * Created by Dan on 8/21/2015.
 */
public class RatedTimeSpan extends TimeSpan
{
    private float consumptionRate = 0;

    public RatedTimeSpan(float consumptionRate, LocalTime from, LocalTime to)
    {
        super(from, to);

        this.consumptionRate = consumptionRate;
    }

    public float getConsumptionRate()
    {
        return consumptionRate;
    }

    public void setConsumptionRate(float consumptionRate)
    {
        this.consumptionRate = consumptionRate;
    }
}
