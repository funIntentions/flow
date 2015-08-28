package com.projects.model;

import com.projects.helper.ProductionState;
import com.projects.helper.StructureUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A structure that represents a potential producer of electricity
 */
public class PowerPlant extends Structure implements Comparable<PowerPlant> {
    private DoubleProperty emissionRate;
    private DoubleProperty cost;
    private DoubleProperty capacity;
    private DoubleProperty currentOutput;
    private ProductionState productionState = ProductionState.IDLE;

    /**
     * PowerPlant constructor.
     * @param name power plant's name
     * @param id unique identifier for structure
     * @param x x coordinate for world position
     * @param y y coordinate for world position
     * @param animatedSprite defines the building's appearance and any animation
     * @param emissionRate the rate at which this power plant produces green house gasses in g/kWh
     * @param cost the price of electricity produced by this power plant in $
     * @param capacity the maximum amount of electricity this plant can produce at any time in watts
     */
    public PowerPlant(String name, int id, double x, double y, AnimatedSprite animatedSprite, double emissionRate, double cost, double capacity) {
        super(name, id, x, y, animatedSprite);
        this.emissionRate = new SimpleDoubleProperty(emissionRate);
        this.cost = new SimpleDoubleProperty(cost);
        this.capacity = new SimpleDoubleProperty(capacity);
        this.currentOutput = new SimpleDoubleProperty(0.0);
    }

    /**
     * PowerPlant copy constructor.
     * @param powerPlant the power plant to copy
     */
    public PowerPlant(PowerPlant powerPlant) {
        super(powerPlant.getName(), StructureUtil.getNextStructureId(), powerPlant.getAnimatedSprite());

        this.emissionRate = new SimpleDoubleProperty(powerPlant.getEmissionRate());
        this.cost = new SimpleDoubleProperty(powerPlant.getCost());
        this.capacity = new SimpleDoubleProperty(powerPlant.getCapacity());
        this.currentOutput = new SimpleDoubleProperty(powerPlant.getCurrentOutput());
    }

    /**
     * Compares the power plants based on how much they cost. This is must
     * @param powerPlant power plant to compare prices with
     * @return 1 if this power plant is more expensive, -1 if this one is cheaper and 0 if the two are equally expensive
     */
    public int compareTo(PowerPlant powerPlant) {
        return powerPlant.getCost() < cost.doubleValue() ? 1 : powerPlant.getCost() > cost.doubleValue() ? -1 : 0;
    }

    public double getEmissionRate() {
        return emissionRate.get();
    }

    public void setEmissionRate(double emissionRate) {
        this.emissionRate.set(emissionRate);
    }

    public DoubleProperty emissionRateProperty() {
        return emissionRate;
    }

    public double getCapacity() {
        return capacity.get();
    }

    public void setCapacity(double capacity) {
        this.capacity.set(capacity);
    }

    public DoubleProperty capacityProperty() {
        return capacity;
    }

    public double getCost() {
        return cost.get();
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public DoubleProperty costProperty() {
        return cost;
    }

    public double getCurrentOutput() {
        return currentOutput.get();
    }

    public void setCurrentOutput(double currentOutput) {
        this.currentOutput.set(currentOutput);
    }

    public DoubleProperty currentOutputProperty() {
        return currentOutput;
    }

    public ProductionState getProductionState() {
        return productionState;
    }

    public void setProductionState(ProductionState productionState) {
        this.productionState = productionState;
    }
}
