package com.projects.systems.simulation;

import com.projects.models.Appliance;
import com.projects.models.Device;
import com.projects.models.Property;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/24/2015.
 */
public class ConsumptionManager
{
    private List<Structure> structures;
    private double totalConsumptionInWatts;
    private double totalKWh;

    public ConsumptionManager()
    {
        structures = new ArrayList<Structure>();
    }

    public double calculateConsumption(double totalHours)
    {
        totalConsumptionInWatts = 0;
        System.out.println("Structures: " + structures.size());

        for (Structure structure : structures)
        {
            List<Appliance> appliances = (List)structure.getAppliances();

            System.out.println("Appliances: " + appliances.size());

            for (Appliance appliance : appliances)
            {
                totalConsumptionInWatts += appliance.getAverageConsumption();
            }
        }

        totalKWh = (totalConsumptionInWatts * totalHours) / 1000;

        return totalConsumptionInWatts;
    }

    public void reset()
    {
        totalConsumptionInWatts = 0;
        totalKWh = 0;
    }

    public void removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId().intValue() == structureToRemove.getId().intValue())
            {
                structures.remove(structure);
                return;
            }
        }
    }

    public void syncStructures(Structure changedStructure)
    {
        int structureIndex = -1;

        for (int i = 0; i < structures.size(); ++i)
        {
            if (changedStructure.getId().intValue() == structures.get(i).getId().intValue())
            {
                structureIndex = i;
            }
        }

        int applianceCount = changedStructure.getAppliances().size();

        if (structureIndex < 0 && applianceCount > 0)
        {
            structures.add(changedStructure);
        }
        else if (structureIndex >=0)
        {
            if (applianceCount > 0)
            {
                structures.set(structureIndex, changedStructure);
            }
            else
            {
                structures.remove(structureIndex);
            }
        }
    }

    public double getTotalKWh() {
        return totalKWh;
    }

    public void setTotalKWh(double totalKWh) {
        this.totalKWh = totalKWh;
    }

    public double getTotalConsumptionInWatts() {
        return totalConsumptionInWatts;
    }

    public void setTotalConsumptionInWatts(double totalConsumptionInWatts) {
        this.totalConsumptionInWatts = totalConsumptionInWatts;
    }
}
