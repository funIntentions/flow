package com.projects.models;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeSpan
{
    public double from;
    public double to;

    public TimeSpan(double fromInSeconds, double toInSeconds)
    {
        from = fromInSeconds;
        to = toInSeconds;
    }

    public TimeSpan(TimeSpan span)
    {
        from = span.from;
        to = span.to;
    }
}
