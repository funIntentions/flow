package com.projects.models;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dan on 5/27/2015.
 */
public class IndividualModel
{
    private Individual instance;
    private String name;
    private String className;
    private ArrayList<PropertyModel> instanceProperties;
    private String description;
    private Integer id;

    public IndividualModel(Integer newId, Individual individual)
    {
        id = newId;
        instance = individual;
        name = individual.getLocalName();
        className = individual.getOntClass().getLocalName();
        description = individual.getComment(null);

        instanceProperties = new ArrayList<PropertyModel>();

        StmtIterator i = instance.listProperties();
        while (i.hasNext())
        {
            Statement s = i.next();
            if (s.getObject().isLiteral())
            {
                String name = s.getPredicate().getLocalName();
                String value = s.getLiteral().getLexicalForm();

                if (value.equals("true") || value.equals("false")) // TODO: change this so the Class can be determined without a hard coded Boolean check
                {
                    PropertyModel<Boolean> property = new PropertyModel<Boolean>(name, Boolean.getBoolean(value));
                    instanceProperties.add(property);
                }
                else
                {
                    PropertyModel<Double> property = new PropertyModel<Double>(name, Double.parseDouble(value));
                    instanceProperties.add(property);
                }
            }
        }
    }

    public IndividualModel(Integer newId, IndividualModel model)
    {
        id = newId;
        instance = model.instance;
        className = instance.getOntClass().getLocalName();
        description = instance.getComment(null);
        name = model.getName();
        instanceProperties = new ArrayList<PropertyModel>();

        for (PropertyModel property : model.instanceProperties)
        {
            instanceProperties.add(new PropertyModel(property));
        }

    }

    public Iterator<PropertyModel> listProperties()
    {
        return instanceProperties.iterator();
    }

    public void changeProperty(int index, Object newValue)
    {
        instanceProperties.get(index).setValue(newValue);
    }

    public String getDescription()
    {
        return description;
    }

    public Individual getInstance()
    {
        return instance;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public Integer getId()
    {
        return id;
    }
    public String getClassName()
    {
        return className;
    }
}

