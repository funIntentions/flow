package com.projects.models;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassModel
{
    private OntClass ontClass;
    private String name;
    private String description;
    private Integer id;

    public ClassModel(Integer newId, OntClass newOntClass)
    {
        id = newId;
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

    public Integer getId()
    {
        return id;
    }
}
