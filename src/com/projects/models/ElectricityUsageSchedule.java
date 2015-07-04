package com.projects.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 7/3/2015.
 */
public class ElectricityUsageSchedule
{
    private double usagePerDay;
    private List<TimeSpan> activeTimeSpans;

    public ElectricityUsageSchedule()
    {
        usagePerDay = 0;
        activeTimeSpans = new ArrayList<TimeSpan>();
    }

    public ElectricityUsageSchedule(ElectricityUsageSchedule schedule)
    {
        usagePerDay = schedule.getUsagePerDay();
        activeTimeSpans = new ArrayList<TimeSpan>();

        for (TimeSpan span : schedule.getActiveTimeSpans())
        {
            activeTimeSpans.add(new TimeSpan(span));
        }
    }

    public void addTimeSpanAndRecalcualte(TimeSpan span)
    {
        activeTimeSpans.add(span);

        recalculateUsagePerDay();
    }

    public void removeTimeSpanAndRecalculate(int index)
    {
        activeTimeSpans.remove(index);

        recalculateUsagePerDay();
    }

    private void recalculateUsagePerDay()
    {
        usagePerDay = 0;

        for (TimeSpan overlappingSpan : activeTimeSpans)
        {
            usagePerDay += overlappingSpan.to - overlappingSpan.from;
        }
    }

    public void setActiveTimeSpanFrom(int index, double fromSeconds)
    {
        activeTimeSpans.get(index).from = fromSeconds;
    }

    public void setActiveTimeSpanTo(int index, double toSeconds)
    {
        activeTimeSpans.get(index).to = toSeconds;
    }

    public double getElectricityUsageDuringSpan(TimeSpan timeSpan)
    {
        List<TimeSpan> overlappingSpans = new ArrayList<TimeSpan>();
        double usageSum = 0;

        for (TimeSpan deviceUsage : activeTimeSpans)
        {
            if (timeSpan.from >= deviceUsage.from && timeSpan.from < deviceUsage.to)
            {
                double start = timeSpan.from;
                double end = timeSpan.to;

                if (timeSpan.to > deviceUsage.to)
                {
                    end = deviceUsage.to;
                }

                overlappingSpans.add(new TimeSpan(start, end));
            }
        }

        for (TimeSpan overlappingSpan : overlappingSpans)
        {
            usageSum += overlappingSpan.to - overlappingSpan.from;
        }

        return usageSum;
    }

    public double getUsagePerDay()
    {
        return usagePerDay;
    }

    public List<TimeSpan> getActiveTimeSpans()
    {
        return activeTimeSpans;
    }
}
