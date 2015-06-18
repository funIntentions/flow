package com.projects.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/17/2015.
 */
public class Template
{
    List<Device> deviceTemplates;
    List<Structure> structureTemplates;

    public void Template(ArrayList<Device> devices, ArrayList<Structure> structures)
    {
        deviceTemplates = devices;
        structureTemplates = structures;
    }
}
