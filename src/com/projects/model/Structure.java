package com.projects.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Dan on 7/27/2015.
 */
public class Structure
{
    protected StringProperty name;
    protected IntegerProperty id;
    protected AnimatedSprite animatedSprite;

    public Structure(String name, int id, AnimatedSprite animatedSprite)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.animatedSprite = new AnimatedSprite(animatedSprite);
    }

    public Structure(String name, int id, double x, double y, AnimatedSprite animatedSprite)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.animatedSprite = new AnimatedSprite(animatedSprite);
        this.animatedSprite.setXPosition(x);
        this.animatedSprite.setYPosition(y);
    }

    public String getName()
    {
        return name.get();
    }

    public StringProperty nameProperty()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public int getId()
    {
        return id.get();
    }

    public IntegerProperty idProperty()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id.set(id);
    }

    public AnimatedSprite getAnimatedSprite()
    {
        return animatedSprite;
    }

    public void setAnimatedSprite(AnimatedSprite animatedSprite)
    {
        this.animatedSprite = animatedSprite;
    }

    @Override
    public String toString()
    {
        return name.get();
    }
}
