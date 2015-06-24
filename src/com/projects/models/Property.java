package com.projects.models;

/**
 * Created by Dan on 5/29/2015.
 */
public class Property<T>
{
    private String name;
    private T value;

    public Property(String n, T val)
    {
        name = n;
        value = val;
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

    public Property(Property model)
    {
        name = model.getName();
        //TODO: check this cast;
        value = (T)model.getValue();
    }

}
