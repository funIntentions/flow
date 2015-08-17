package com.projects.model;

import com.projects.helper.Constants;
import com.projects.helper.ImageType;
import com.projects.helper.Utils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.luaj.vm2.ast.Str;

/**
 * Created by Dan on 7/27/2015.
 */
public class Structure
{
    protected StringProperty name;
    protected IntegerProperty id;
    protected ImageType image;
    protected Sprite sprite;

    public Structure(String name, int id, Sprite sprite, ImageType image)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.image = image;
        this.sprite = new Sprite(sprite);
    }

    public Structure(String name, int id, int x, int y, ImageType image)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.image = image;

        if (image == ImageType.HOUSE_IMAGE)
        {
            this.sprite = new Sprite(new Image(Constants.HOUSE_IMAGE_PATH), x, y);
        }
        else
        {
            this.sprite = new Sprite(new Image(Constants.POWER_PLANT_IMAGE_PATH), x, y);
        }
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

    public ImageType getImage()
    {
        return image;
    }

    public void setImage(ImageType image)
    {
        this.image = image;
    }

    public Sprite getSprite()
    {
        return sprite;
    }

    public void setSprite(Sprite sprite)
    {
        this.sprite = sprite;
    }

    @Override
    public String toString()
    {
        return name.get();
    }
}
