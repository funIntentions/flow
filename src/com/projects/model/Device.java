package com.projects.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A device that could be found in buildings.
 */
public class Device {
    protected StringProperty name;
    protected IntegerProperty id;

    /**
     * Device constructor.
     * @param name device's name
     * @param id unique identifier for device
     */
    public Device(String name, int id) {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
