package com.projects.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * An image location in a 2D space.
 */
public class Sprite {
    protected double xPosition;
    protected double yPosition;
    protected Image image;

    /**
     * Sprite copy constructor.
     * @param sprite the sprite to be copied
     */
    public Sprite(Sprite sprite) {
        this.image = sprite.getImage();
        this.xPosition = sprite.getXPosition();
        this.yPosition = sprite.getYPosition();
    }

    /**
     * Sprite constructor.
     * @param image sprite's image
     * @param x x coordinate for world position
     * @param y y coordinate for world position
     */
    public Sprite(Image image, double x, double y) {
        this.image = image;
        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * Used in collision detection this gets the bounds of the sprite based on it's position and image dimensions.
     * @return the rectangle that is the sprite's bounds.
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(xPosition, yPosition, image.getWidth(), image.getHeight());
    }

    /**
     * Determines if this sprite has intersected with another rectangle.
     * @param boundary the boundary of the second rectangle
     * @return true if they're intersected, false otherwise
     */
    public boolean intersects(Rectangle2D boundary) {
        return boundary.intersects(this.getBoundary());
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
