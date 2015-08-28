package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * Sprite class that uses an animation to set its sprite image.
 */
public class AnimatedSprite extends Sprite {
    private int id;
    private Animation animation;

    /**
     * Animated Sprite copy constructor.
     * @param animatedSprite the animated sprite to be copied
     */
    public AnimatedSprite(AnimatedSprite animatedSprite) {
        super(animatedSprite.getImage(), animatedSprite.getXPosition(), animatedSprite.getYPosition());

        this.id = animatedSprite.getId();
        this.animation = animatedSprite.getAnimation();
    }

    /**
     * Animated Sprite constructor.
     * @param id a unique identifier
     * @param images a list of images for the animation
     * @param x x coordinate for position
     * @param y y coordinate for position
     * @param duration seconds the animation should take to complete
     */
    public AnimatedSprite(int id, List<Image> images, double x, double y, double duration) {
        super(images.get(0), x, y);

        this.id = id;
        this.animation = new Animation(images, duration);
    }

    /**
     * Updates the animation and image if the animation has changed it.
     * @param time current time in nanoseconds
     */
    public void animate(double time) {
        image = animation.animate(time);
    }

    /**
     * Manually sets the animations current frame, updating the image to whichever image corresponds to that frame.
     * @param index the frame index
     */
    public void setFrame(int index) {
        image = animation.getFrames().get(index);
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
