package com.projects.systems.simulation;

import com.projects.helper.StructureType;
import com.projects.models.PowerPlant;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/29/2015.
 */
public class ProductionManager
{
    private List<PowerPlant> powerPlants;
    private List<Structure> structures;
    private double priceOfProduction;
    private double emissions;

    public ProductionManager()
    {
        structures = new ArrayList<Structure>();
        powerPlants = new ArrayList<PowerPlant>();
        reset();
    }

    public void calculateProduction(double kWh)
    {

        // TODO: calculate how much MWh the plant produces based on it's current operating capacity and how many hours have passed. Then use that amount to see if it will satisfy the total energy consumed.

        for (PowerPlant powerPlant : powerPlants)
        {
            double capacity = powerPlant.getCapacity();

            priceOfProduction = kWh * powerPlant.getProductionCost();
            emissions = kWh * powerPlant.getEmissionRate();
            break; // TODO : have it factor in all available plants instead of just the first, at the moment the plant just instantly meets the energy demands.
        }
    }

    public void reset()
    {
        emissions = 0;
        priceOfProduction = 0;
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

        for (PowerPlant powerPlant : powerPlants)
        {
            if (powerPlant.getId().intValue() == structureToRemove.getId().intValue())
            {
                powerPlants.remove(powerPlant);
            }
        }
    }

    public void removeAllStructures()
    {
        powerPlants.clear();
        structures.clear();
    }

    public void syncStructures(Structure changedStructure)
    {
        int structureIndex = -1;

        if (changedStructure instanceof PowerPlant)
        {
            for (int i = 0; i < powerPlants.size(); ++i)
            {
                if (changedStructure.getId().intValue() == powerPlants.get(i).getId().intValue())
                {
                    structureIndex = i;
                }
            }

            PowerPlant powerPlant = (PowerPlant)changedStructure;

            if (structureIndex < 0)
            {
                powerPlants.add(powerPlant);
            }
            else
            {
                powerPlants.set(structureIndex, powerPlant);
            }

        }
        else
        {
            for (int i = 0; i < structures.size(); ++i)
            {
                if (changedStructure.getId().intValue() == structures.get(i).getId().intValue())
                {
                    structureIndex = i;
                }
            }

            int applianceCount = changedStructure.getEnergySources().size();

            if (structureIndex < 0 && (applianceCount > 0))
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
    }

    public double getPriceOfProduction() {
        return priceOfProduction;
    }

    public double getEmissions()
    {
        return emissions;
    }
}
