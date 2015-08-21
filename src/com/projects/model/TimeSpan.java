package com.projects.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalTime;

/**
 * Created by Dan on 7/3/2015.
 */
public class TimeSpan
{
    protected ObjectProperty<LocalTime> from;
    protected ObjectProperty<LocalTime> to;
    protected BooleanProperty monday;
    protected BooleanProperty tuesday;
    protected BooleanProperty wednesday;
    protected BooleanProperty thursday;
    protected BooleanProperty friday;
    protected BooleanProperty saturday;
    protected BooleanProperty sunday;

    public TimeSpan(LocalTime from, LocalTime to)
    {
        this.from = new SimpleObjectProperty<>(from);
        this.to = new SimpleObjectProperty<>(to);
        monday = new SimpleBooleanProperty(true);
        tuesday = new SimpleBooleanProperty(true);
        wednesday = new SimpleBooleanProperty(true);
        thursday = new SimpleBooleanProperty(true);
        friday = new SimpleBooleanProperty(true);
        saturday = new SimpleBooleanProperty(true);
        sunday = new SimpleBooleanProperty(true);
    }

    public boolean isActiveForDay(int day)
    {
        switch(day)
        {
            case 0:
                return monday.get();
            case 1:
                return tuesday.get();
            case 2:
                return wednesday.get();
            case 3:
                return thursday.get();
            case 4:
                return friday.get();
            case 5:
                return saturday.get();
            case 6:
                return sunday.get();
            default:
                return false;
        }
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

    public boolean getMonday()
    {
        return monday.get();
    }

    public BooleanProperty mondayProperty()
    {
        return monday;
    }

    public boolean getTuesday()
    {
        return tuesday.get();
    }

    public BooleanProperty tuesdayProperty()
    {
        return tuesday;
    }

    public boolean getWednesday()
    {
        return wednesday.get();
    }

    public BooleanProperty wednesdayProperty()
    {
        return wednesday;
    }

    public boolean getThursday()
    {
        return thursday.get();
    }

    public BooleanProperty thursdayProperty()
    {
        return thursday;
    }

    public boolean getFriday()
    {
        return friday.get();
    }

    public BooleanProperty fridayProperty()
    {
        return friday;
    }

    public boolean getSaturday()
    {
        return saturday.get();
    }

    public BooleanProperty saturdayProperty()
    {
        return saturday;
    }

    public boolean getSunday()
    {
        return sunday.get();
    }

    public BooleanProperty sundayProperty()
    {
        return sunday;
    }
}
