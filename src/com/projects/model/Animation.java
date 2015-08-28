package com.projects.model;

import javafx.scene.image.Image;

import java.util.List;

/**
 * An Animation that updates at a constant rate.
 */
public class Animation {
    private List<Image> frames;
    private double duration;
    private int frame = 0;
    private long start = 0;
    private boolean playing = false;

    /**
     * Animation constructor.
     * @param frames the images that will makeup the animation
     * @param duration the animations length in seconds
     */
    public Animation(List<Image> frames, double duration) {
        this.frames = frames;
        this.duration = duration;
    }

    /**
     * To be called before calling animate. Setting start to this moment in time is needed so the animation will start with on the first frame.
     */
    public void start() {
        start = System.nanoTime();
        playing = true;
    }

    /**
     * Advances the animation based on the difference between start time and the current time.
     * @param now the current time in nanoseconds
     * @return the image for the frame that the animation is on after updating
     */
    public Image animate(double now) {

        if (playing) {

            double time = (now - start) / 1000000000.0;

            frame = (int) ((time % (frames.size() * duration)) / duration);

            return frames.get(frame);

        } else {
            return frames.get(0);
        }
    }

    /**
     * Halts the animation, making the animation appear frozen unless animate is called afterward.
     */
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

    public boolean isPlaying() {
        return playing;
    }

}
