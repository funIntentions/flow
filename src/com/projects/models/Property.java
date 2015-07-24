package com.projects.models;

/**
 * Created by Dan on 5/29/2015.
 */
public class Property<T>
{
    private String name;
    private T value;
    private String units;

    public Property(String name, T value, String units)
    {
        this.name = name;
        this.value = value;
        this.units = units;
    }

    public String getName()
    {
        return name;
    }

    public void setValue(Object newValue)
    {
        value = (T)newValue;
    }

    public T getValue()
    {
        return value;
    }

    public String getUnits()
    {
        return units;
    }

    public Property(Property model)
    {
        this.name = model.getName();
        //TODO: check this cast;
        this.value = (T)model.getValue();
        this.units = model.getUnits();
    }

}
