package com.projects.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 7/3/2015.
 */
public class ElectricityUsageSchedule
{
    private double usagePerDay;
    private ObservableList<TimeSpan> activeTimeSpans;

    public ElectricityUsageSchedule()
    {
        usagePerDay = 0;
        activeTimeSpans = FXCollections.observableArrayList();
    }

    public ElectricityUsageSchedule(ElectricityUsageSchedule schedule)
    {
        usagePerDay = schedule.getUsagePerDay();
        activeTimeSpans = FXCollections.observableArrayList();

        for (TimeSpan span : schedule.getActiveTimeSpans())
        {
            activeTimeSpans.add(new TimeSpan(span.getFrom(), span.getTo()));
        }
    }

    public void addTimeSpanAndRecalculate(TimeSpan span)
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

        for (TimeSpan timeSpan : activeTimeSpans)
        {
            usagePerDay += timeSpan.getTo().toSecondOfDay() - timeSpan.getFrom().toSecondOfDay();
        }
    }

    public void setTimeSpanFromAndRecalculate(int index, long fromSeconds)
    {
        activeTimeSpans.get(index).setFrom(LocalTime.ofSecondOfDay(fromSeconds));
        recalculateUsagePerDay();
    }

    public void setTimeSpanToAndRecalculate(int index, long toSeconds)
    {
        activeTimeSpans.get(index).setTo(LocalTime.ofSecondOfDay(toSeconds));
        recalculateUsagePerDay();
    }

    public double getElectricityUsageDuringSpan(TimeSpan timeSpan)
    {
        List<TimeSpan> overlappingSpans = new ArrayList<TimeSpan>();
        double usageSum = 0;
        long timeSpanFrom = timeSpan.getFrom().toSecondOfDay();
        long timeSpanTo = timeSpan.getTo().toSecondOfDay();


        for (TimeSpan deviceUsage : activeTimeSpans)
        {
            long deviceUsageFrom = deviceUsage.getFrom().toSecondOfDay();
            long deviceUsageTo = deviceUsage.getTo().toSecondOfDay();

            if (timeSpanFrom >= deviceUsageFrom && timeSpanFrom < deviceUsageTo)
            {
                LocalTime start = timeSpan.getFrom();
                LocalTime end = timeSpan.getTo();

                if (timeSpanTo > deviceUsageTo)
                {
                    end = deviceUsage.getTo();
                }

                overlappingSpans.add(new TimeSpan(start, end));
            }
            else if (timeSpanFrom < deviceUsageFrom && timeSpanTo >= deviceUsageFrom)
            {
                LocalTime start = deviceUsage.getFrom();
                LocalTime end = timeSpan.getTo();

                if (timeSpanTo > deviceUsageTo)
                {
                    end = deviceUsage.getTo();
                }

                overlappingSpans.add(new TimeSpan(start, end));
            }
        }

        for (TimeSpan overlappingSpan : overlappingSpans)
        {
            usageSum += overlappingSpan.getTo().toSecondOfDay() - overlappingSpan.getFrom().toSecondOfDay();
        }

        return usageSum;
    }

    public boolean isOnAtTime(long time)
    {
        for (TimeSpan deviceUsage : activeTimeSpans)
        {
            if (deviceUsage.getFrom().toSecondOfDay() <= time && deviceUsage.getTo().toSecondOfDay() >= time)
            {
                return true;
            }
        }

        return false;
    }

    public double getUsagePerDay()
    {
        return usagePerDay;
    }

    public ObservableList<TimeSpan> getActiveTimeSpans()
    {
        return activeTimeSpans;
    }
}
