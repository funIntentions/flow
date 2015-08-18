package com.projects.model;

import com.projects.helper.ImageType;
import com.projects.helper.ProductionState;
import com.projects.helper.StructureUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Dan on 7/30/2015.
 */
public class PowerPlant extends Structure implements Comparable<PowerPlant>
{
    private DoubleProperty emissionRate;
    private DoubleProperty cost;
    private DoubleProperty capacity;
    private DoubleProperty currentOutput;
    private ProductionState productionState = ProductionState.IDLE;

    public PowerPlant(String name, int id, ImageType imageType, double x, double y, double emissionRate, double cost, double capacity)
    {
        super(name, id, x, y, imageType);
        this.emissionRate = new SimpleDoubleProperty(emissionRate);
        this.cost = new SimpleDoubleProperty(cost);
        this.capacity = new SimpleDoubleProperty(capacity);
        this.currentOutput = new SimpleDoubleProperty(0.0);
    }

    public PowerPlant(PowerPlant powerPlant)
    {
        super(powerPlant.getName(), StructureUtil.getNextStructureId(), powerPlant.getAnimatedSprite(), powerPlant.getImage());

        this.emissionRate = new SimpleDoubleProperty(powerPlant.getEmissionRate());
        this.cost = new SimpleDoubleProperty(powerPlant.getCost());
        this.capacity = new SimpleDoubleProperty(powerPlant.getCapacity());
        this.currentOutput = new SimpleDoubleProperty(powerPlant.getCurrentOutput());
    }

    public int compareTo(PowerPlant powerPlant)
    {
        return powerPlant.getCost() < cost.doubleValue()? 1 : powerPlant.getCost() > cost.doubleValue()? -1 : 0;
    }

    public double getEmissionRate()
    {
        return emissionRate.get();
    }

    public DoubleProperty emissionRateProperty()
    {
        return emissionRate;
    }

    public void setEmissionRate(double emissionRate)
    {
        this.emissionRate.set(emissionRate);
    }

    public double getCapacity()
    {
        return capacity.get();
    }

    public DoubleProperty capacityProperty()
    {
        return capacity;
    }

    public void setCapacity(double capacity)
    {
        this.capacity.set(capacity);
    }

    public double getCost()
    {
        return cost.get();
    }

    public DoubleProperty costProperty()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost.set(cost);
    }

    public double getCurrentOutput()
    {
        return currentOutput.get();
    }

    public DoubleProperty currentOutputProperty()
    {
        return currentOutput;
    }

    public void setCurrentOutput(double currentOutput)
    {
        this.currentOutput.set(currentOutput);
    }

    public ProductionState getProductionState()
    {
        return productionState;
    }

    public void setProductionState(ProductionState productionState)
    {
        this.productionState = productionState;
    }
}
