package com.projects.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalTime;

/**
 * Represents a span of time on a particular day(s) during the week.
 */
public class TimeSpan {
    protected ObjectProperty<LocalTime> from;
    protected ObjectProperty<LocalTime> to;
    protected BooleanProperty monday;
    protected BooleanProperty tuesday;
    protected BooleanProperty wednesday;
    protected BooleanProperty thursday;
    protected BooleanProperty friday;
    protected BooleanProperty saturday;
    protected BooleanProperty sunday;

    /**
     * TimeSpan constructor.
     *
     * @param from beginning of the time span
     * @param to   end of the time span
     */
    public TimeSpan(LocalTime from, LocalTime to) {
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

    /**
     * TimeSpan copy constructor
     *
     * @param timeSpan TimeSpan to be copied
     */
    public TimeSpan(TimeSpan timeSpan) {
        this.from = new SimpleObjectProperty<>(timeSpan.getFrom());
        this.to = new SimpleObjectProperty<>(timeSpan.getTo());
        monday = new SimpleBooleanProperty(timeSpan.getMonday());
        tuesday = new SimpleBooleanProperty(timeSpan.getThursday());
        wednesday = new SimpleBooleanProperty(timeSpan.getWednesday());
        thursday = new SimpleBooleanProperty(timeSpan.getThursday());
        friday = new SimpleBooleanProperty(timeSpan.getFriday());
        saturday = new SimpleBooleanProperty(timeSpan.getSaturday());
        sunday = new SimpleBooleanProperty(timeSpan.getSunday());
    }

    /**
     * Checks whether this time span will be active for a particular day of the week.
     *
     * @param day day of the week
     * @return true if the time span does apply for that day, false otherwise
     */
    public boolean isActiveForDay(int day) {
        switch (day) {
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

    public LocalTime getFrom() {
        return from.get();
    }

    public void setFrom(LocalTime from) {
        this.from.set(from);
    }

    public ObjectProperty<LocalTime> fromProperty() {
        return from;
    }

    public LocalTime getTo() {
        return to.get();
    }

    public void setTo(LocalTime to) {
        this.to.set(to);
    }

    public ObjectProperty<LocalTime> toProperty() {
        return to;
    }

    public boolean getMonday() {
        return monday.get();
    }

    public BooleanProperty mondayProperty() {
        return monday;
    }

    public boolean getTuesday() {
        return tuesday.get();
    }

    public BooleanProperty tuesdayProperty() {
        return tuesday;
    }

    public boolean getWednesday() {
        return wednesday.get();
    }

    public BooleanProperty wednesdayProperty() {
        return wednesday;
    }

    public boolean getThursday() {
        return thursday.get();
    }

    public BooleanProperty thursdayProperty() {
        return thursday;
    }

    public boolean getFriday() {
        return friday.get();
    }

    public BooleanProperty fridayProperty() {
        return friday;
    }

    public boolean getSaturday() {
        return saturday.get();
    }

    public BooleanProperty saturdayProperty() {
        return saturday;
    }

    public boolean getSunday() {
        return sunday.get();
    }

    public BooleanProperty sundayProperty() {
        return sunday;
    }
}
