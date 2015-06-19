package com.projects.models;

import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Template
{
    List<Device> deviceTemplates;
    List<Structure> structureTemplates;

    public Template(List<Device> devices, List<Structure> structures)
    {
        deviceTemplates = devices;
        structureTemplates = structures;
    }
}
