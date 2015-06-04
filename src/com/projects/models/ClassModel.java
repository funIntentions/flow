package com.projects.models;

import com.hp.hpl.jena.ontology.OntClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/1/2015.
 */
public class ClassModel
{
    private OntClass ontClass;
    private String name;
    private String description;
    private Integer id;
    private List<ClassModel> children;

    public ClassModel(Integer newId, OntClass newOntClass)
    {
        id = newId;
        ontClass = newOntClass;
        name = ontClass.getLocalName();
        description = ontClass.getComment(null);
        children = new ArrayList<ClassModel>();
    }

    public void addChild(ClassModel child)
    {
        children.add(child);
    }

    public List<ClassModel> getChildren()
    {
        return children;
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

    public String toString()
    {
        return getName();
    }
}
