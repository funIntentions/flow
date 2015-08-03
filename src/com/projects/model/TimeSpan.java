package com.projects.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalTime;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeSpan
{
    private ObjectProperty<LocalTime> from;
    private ObjectProperty<LocalTime> to;

    public TimeSpan(LocalTime from, LocalTime to)
    {
        this.from = new SimpleObjectProperty<>(from);
        this.to = new SimpleObjectProperty<>(to);
    }

    public LocalTime getFrom()
    {
        return from.get();
    }

    public ObjectProperty<LocalTime> fromProperty()
    {
        return from;
    }

    public void setFrom(LocalTime from)
    {
        this.from.set(from);
    }

    public LocalTime getTo()
    {
        return to.get();
    }

    public ObjectProperty<LocalTime> toProperty()
    {
        return to;
    }

    public void setTo(LocalTime to)
    {
        this.to.set(to);
    }
}
