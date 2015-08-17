package com.projects.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * Created by Dan on 8/17/2015.
 */
public class Sprite
{
    private double xPosition;
    private double yPosition;
    private Image image;

    public Sprite(Sprite sprite)
    {
        this.image = sprite.getImage();
        this.xPosition = sprite.getXPosition();
        this.yPosition = sprite.getYPosition();
    }

    public Sprite(Image image, double x, double y)
    {
        this.image = image;
        this.xPosition = x;
        this.yPosition = y;
    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(xPosition, yPosition, image.getWidth(), image.getHeight());
    }

    public boolean intersects(Rectangle2D boundary)
    {
        return boundary.intersects( this.getBoundary() );
    }

    public double getXPosition()
    {
        return xPosition;
    }

    public void setXPosition(double xPosition)
    {
        this.xPosition = xPosition;
    }

    public double getYPosition()
    {
        return yPosition;
    }

    public void setYPosition(double yPosition)
    {
        this.yPosition = yPosition;
    }

    public Image getImage()
    {
        return image;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }
}
