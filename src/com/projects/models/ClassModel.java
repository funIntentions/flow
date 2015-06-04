package com.projects.models;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassModel
{
    // TODO: make these private, same with IndividualModel
    private OntClass ontClass;
    private String name;
    private String description;

    public ClassModel(OntClass newOntClass)
    {
        ontClass = newOntClass;
        name = ontClass.getLocalName();
        description = ontClass.getComment(null);
    }

    public String getDescription()
    {
        return description;
    }

    public OntClass getOntClass()
    {
        return ontClass;
    }

    public String getName()
    {
        return name;
    }
}
