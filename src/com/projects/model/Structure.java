package com.projects.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A structure that can be displayed in the world.
 */
public class Structure {
    protected StringProperty name;
    protected IntegerProperty id;
    protected AnimatedSprite animatedSprite;

    /**
     * Structure constructor.
     * @param name structure's name
     * @param id unique identifier for structure
     * @param animatedSprite visual representation of structure and any animation it has
     */
    public Structure(String name, int id, AnimatedSprite animatedSprite) {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.animatedSprite = new AnimatedSprite(animatedSprite);
    }

    /**
     * Structure constructor.
     * @param name structure's name
     * @param id unique identifier for structure
     * @param x x coordinate for world position
     * @param y y coordinate for world position
     * @param animatedSprite visual representation of structure and any animation it has
     */
    public Structure(String name, int id, double x, double y, AnimatedSprite animatedSprite) {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.animatedSprite = new AnimatedSprite(animatedSprite);
        this.animatedSprite.setXPosition(x);
        this.animatedSprite.setYPosition(y);
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

    public AnimatedSprite getAnimatedSprite() {
        return animatedSprite;
    }

    public void setAnimatedSprite(AnimatedSprite animatedSprite) {
        this.animatedSprite = animatedSprite;
    }

    @Override
    public String toString() {
        return name.get();
    }
}
