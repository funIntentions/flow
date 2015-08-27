package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * Created by Dan on 8/18/2015.
 */
public class Animation {
    private List<Image> frames;
    private double duration;
    private int frame = 0;
    private long start = 0;
    private boolean playing = false;

    public Animation(List<Image> frames, double duration) {
        this.frames = frames;
        this.duration = duration;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void start() {
        start = System.nanoTime();
        playing = true;
    }

    public Image animate(double now) {

        if (playing) {

            double time = (now - start) / 1000000000.0;

            frame = (int) ((time % (frames.size() * duration)) / duration);

            return frames.get(frame);

        } else {
            return frames.get(0);
        }
    }

    public void stop() {
        playing = false;
    }

    public List<Image> getFrames() {
        return frames;
    }

    public void setFrames(List<Image> frames) {
        this.frames = frames;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getFrame() {
        return frame;
    }
}
