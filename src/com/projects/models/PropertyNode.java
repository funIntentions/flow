package com.projects.models;

import com.hp.hpl.jena.ontology.OntProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/11/2015.
 */
public class PropertyNode
{
    private int id;
    private String name;
    private List<PropertyNode> children;
    private OntProperty ontProperty;

    public PropertyNode(int propId, OntProperty property)
    {
        id = propId;
        name = property.getLocalName();
        ontProperty = property;
        children = new ArrayList<PropertyNode>();
    }

    public void addChild(PropertyNode child)
    {
        children.add(child);
    }

    public List<PropertyNode> getChildren()
    {
        return children;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public OntProperty getOntProperty()
    {
        return ontProperty;
    }

    public String toString()
    {
        return getName();
    }
}
