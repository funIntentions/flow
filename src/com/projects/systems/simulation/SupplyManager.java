package com.projects.systems.simulation;

import com.projects.models.PowerPlant;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/29/2015.
 */
public class SupplyManager
{
    private List<PowerPlant> powerPlants;
    private List<Structure> structures;
    private double emissions;
    private double price;

    public SupplyManager()
    {
        structures = new ArrayList<Structure>();
        powerPlants = new ArrayList<PowerPlant>();
        reset();
    }

    public void calculateSupply(double demand)
    {
        double watts = demand;
        price = 0;
        emissions = 0;

        for (PowerPlant powerPlant : powerPlants) // TODO: sort base on price?
        {
            double capacity = powerPlant.getCapacity();

            if (demand <= capacity)
            {
                price += demand * powerPlant.getProductionCost();
                emissions += demand * powerPlant.getEmissionRate();
                demand -= demand;
            }
            else
            {
                price +=  capacity * powerPlant.getProductionCost();
                emissions += capacity * powerPlant.getEmissionRate();
                demand -= capacity;
            }
        }

        if (watts > 0)
        {
            price /= watts; // $ per kWh
            emissions /= watts; // g per kWh
        }

        if (demand > 0)
        {
            System.out.println("Not enough to supply to meet demand.");
        }
    }

    public void reset()
    {
        emissions = 0;
        price = 0;
    }

    public void removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == structureToRemove.getId())
            {
                structures.remove(structure);
                return;
            }
        }

        for (PowerPlant powerPlant : powerPlants)
        {
            if (powerPlant.getId() == structureToRemove.getId())
            {
                powerPlants.remove(powerPlant);
                return;
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
                if (changedStructure.getId() == powerPlants.get(i).getId())
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
                if (changedStructure.getId() == structures.get(i).getId())
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

    public double getPrice()
    {
        return price;
    }

    public double getEmissions()
    {
        return emissions;
    }
}
