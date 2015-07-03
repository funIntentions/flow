package com.projects.models;

import com.projects.helper.ImageType;
import com.projects.helper.StructureType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 6/29/2015.
 */
public class PowerPlant extends Structure
{
    private double capitalCost;
    private double monthlyExpense;
    private double efficiency;
    private double emissionRate;
    private double productionCost;
    private double capacity;

    public PowerPlant(String structure)
    {
        this(structure, -1, 1, new ArrayList<Property>(), new ArrayList<Device>(), new ArrayList<Device>() , new ArrayList<Device>());
    }

    public PowerPlant(String structure, List<Property> propertyList)
    {
        this(structure, -1, 1, propertyList, new ArrayList<Device>(), new ArrayList<Device>(), new ArrayList<Device>());
    }

    public PowerPlant(Structure structure)
    {
        this(structure.getName(),
                structure.getId(),
                structure.getNumberOfUnits(),
                structure.getProperties(),
                structure.getAppliances(),
                structure.getEnergySources(),
                structure.getEnergyStorageDevices());
    }

    public PowerPlant(String structure,
                     Integer id,
                     Integer numberOfUnits,
                     List<Property> propertyList,
                     List<Device> applianceList,
                     List<Device> energySourceList,
                     List<Device> energyStorageList)
    {
        super(structure, id, StructureType.POWER_PLANT, ImageType.POWER_PLANT_IMAGE, numberOfUnits, propertyList, applianceList, energySourceList, energyStorageList);
    }


    public void changePropertyValue(int index, Object value)
    {
        super.changePropertyValue(index, value);
        Property property = properties.get(index);
        property.setValue(value);

        changePropertyVariableValue(property);
    }

    public void changePropertyVariableValue(Property property)
    {
        Object value = property.getValue();

        if (property.getName().equals("CapitalCost"))
        {
            capitalCost = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("MonthlyExpense"))
        {
            monthlyExpense = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("EmissionRate"))
        {
            emissionRate = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("ProductionCost"))
        {
            productionCost = Double.valueOf(value.toString());
        }
        else if (property.getName().equals("Capacity"))
        {
            capacity = Double.valueOf(value.toString());
        }
    }

    public double getProductionCost() {
        return productionCost;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public void setProductionCost(double productionCost) {
        this.productionCost = productionCost;
    }

    public double getEmissionRate() {
        return emissionRate;
    }

    public void setEmissionRate(double emissionRate) {
        this.emissionRate = emissionRate;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public double getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(double monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    public double getCapitalCost() {
        return capitalCost;
    }

    public void setCapitalCost(double capitalCost) {
        this.capitalCost = capitalCost;
    }
}
