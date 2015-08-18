package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * Created by Dan on 8/18/2015.
 */
public class AnimatedSprite extends Sprite
{
    private Animation animation;

    public AnimatedSprite(AnimatedSprite animatedSprite)
    {
        super(animatedSprite.getImage(), animatedSprite.getXPosition(), animatedSprite.getYPosition());

        this.animation = animatedSprite.getAnimation();
    }

    public AnimatedSprite(List<Image> images, double x, double y, double duration)
    {
        super(images.get(0), x, y);

        animation = new Animation(images, duration);
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
}
