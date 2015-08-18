package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * Created by Dan on 8/18/2015.
 */
public class Animation
{
    private List<Image> frames;
    private double duration;

    public Animation(List<Image> frames, double duration)
    {
        this.frames = frames;
        this.duration = duration;
    }

    public Image animate(double time)
    {
        int index = (int)((time % (frames.size() * duration)) / duration);
        return frames.get(index);
    }

    public List<Image> getFrames()
    {
        return frames;
    }

    public void setFrames(List<Image> frames)
    {
        this.frames = frames;
    }

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double duration)
    {
        this.duration = duration;
    }
}
