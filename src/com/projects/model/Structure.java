package com.projects.model;

import com.projects.helper.ImageType;
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
    protected IntegerProperty x;
    protected IntegerProperty y;
    protected ImageType image;

    public Structure(String name, int id, int x, int y, ImageType image)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.x = new SimpleIntegerProperty(x);
        this.y = new SimpleIntegerProperty(y);
        this.image = image;
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

    public int getX()
    {
        return x.get();
    }

    public IntegerProperty xProperty()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x.set(x);
    }

    public int getY() {
        return y.get();
    }

    public IntegerProperty yProperty()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y.set(y);
    }

    public ImageType getImage()
    {
        return image;
    }

    public void setImage(ImageType image)
    {
        this.image = image;
    }

    @Override
    public String toString()
    {
        return name.get();
    }
}
