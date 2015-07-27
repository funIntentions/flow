package com.projects.systems.simulation;

import com.projects.models.PowerPlant;
import com.projects.models.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public boolean calculateSupply(double demand)
    {
        price = 0;
        emissions = 0;

        for (PowerPlant powerPlant : powerPlants)
        {
            powerPlant.setCurrentOutput(0);

            if (demand > 0)
            {
                double capacity = powerPlant.getCapacity();
                double productionNeeded = demand/capacity;

                if (productionNeeded > 1)
                    productionNeeded = 1.0;

                double production = productionNeeded * capacity;
                powerPlant.setCurrentOutput(production);
                price += productionNeeded * powerPlant.getProductionCost();
                emissions += productionNeeded * powerPlant.getEmissionRate();
                demand -= Math.round(production);
            }
        }

        if (demand > 0)
        {
            price = 0;
            emissions = 0;
            return false;
        }

        return true;
    }

    public void reset()
    {
        emissions = 0;
        price = 0;
    }

    public boolean removeStructure(Structure structureToRemove)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == structureToRemove.getId())
            {
                structures.remove(structure);
                return true;
            }
        }

        for (PowerPlant powerPlant : powerPlants)
        {
            if (powerPlant.getId() == structureToRemove.getId())
            {
                powerPlants.remove(powerPlant);
                return true;
            }
        }

        return false;
    }

    public void removeAllStructures()
    {
        powerPlants.clear();
        structures.clear();
    }

    public boolean syncStructures(Structure changedStructure)
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

            Collections.sort(powerPlants);
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

            int energySourceCount = changedStructure.getEnergySources().size();

            if (structureIndex < 0 && (energySourceCount > 0))
            {
                structures.add(changedStructure);
            }
            else if (structureIndex >=0)
            {
                if (energySourceCount > 0)
                {
                    structures.set(structureIndex, changedStructure);
                }
                else
                {
                    structures.remove(structureIndex);
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    public List<PowerPlant> getPowerPlants()
    {
        return powerPlants;
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
