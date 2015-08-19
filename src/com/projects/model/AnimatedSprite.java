package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * Created by Dan on 8/18/2015.
 */
public class AnimatedSprite extends Sprite
{
    private int id;
    private Animation animation;

    public AnimatedSprite(AnimatedSprite animatedSprite)
    {
        super(animatedSprite.getImage(), animatedSprite.getXPosition(), animatedSprite.getYPosition());

        this.id = animatedSprite.getId();
        this.animation = animatedSprite.getAnimation();
    }

    public AnimatedSprite(int id, List<Image> images, double x, double y, double duration)
    {
        super(images.get(0), x, y);

        this.id = id;
        this.animation = new Animation(images, duration);
    }

    public void animate(double time)
    {
        image = animation.animate(time);
    }

    public void setFrame(int index)
    {
        image = animation.getFrames().get(index);
    }

    public Animation getAnimation()
    {
        return animation;
    }

    public void setAnimation(Animation animation)
    {
        this.animation = animation;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
