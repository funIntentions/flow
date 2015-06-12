package com.projects.models;

import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Created by Dan on 5/29/2015.
 */
public class PropertyModel <T>
{
    private String name;
    private T value;

    public PropertyModel(String n, T val)
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

    public PropertyModel (PropertyModel model)
    {
        name = model.getName();
        //TODO: check this cast;
        value = (T)model.getValue();
    }

}
