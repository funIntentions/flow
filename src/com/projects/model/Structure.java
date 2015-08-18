package com.projects.model;

import com.projects.helper.Constants;
import com.projects.helper.ImageType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 7/27/2015.
 */
public class Structure
{
    protected StringProperty name;
    protected IntegerProperty id;
    protected ImageType image;
    protected AnimatedSprite animatedSprite;

    public Structure(String name, int id, AnimatedSprite animatedSprite, ImageType image)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.image = image;
        this.animatedSprite = new AnimatedSprite(animatedSprite);
    }

    public Structure(String name, int id, double x, double y, ImageType image)
    {
        this.name = new SimpleStringProperty(name);
        this.id = new SimpleIntegerProperty(id);
        this.image = image;

        List<Image> images = new ArrayList<>();

        if (image == ImageType.HOUSE_IMAGE)
        {
            for (int i = 0; i < 4; ++i)
            {
                images.add(new Image("/images/House_" + i + ".png"));
            }

            this.animatedSprite = new AnimatedSprite(images, x, y, 0);
        }
        else
        {
            for (int i = 0; i < 6; ++i)
            {
                images.add(new Image("/images/PowerPlant_" + i + ".png"));
            }

            this.animatedSprite = new AnimatedSprite(images, x, y, 0.4);
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
