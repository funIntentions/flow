package com.projects.models;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Dan on 6/3/2015.
 */
public class Prefab
{
    private String name;
    private List<IndividualModel> members;
    private Integer id;

    public Prefab(Integer newId, String uniqueName, List<IndividualModel> memberIndividuals)
    {
        id = newId;
        name = uniqueName;
        members = memberIndividuals;
    }

    public Iterator<IndividualModel> listMembers()
    {
        return members.iterator();
    }

    public List<IndividualModel> getMembers()
    {
        return members;
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
