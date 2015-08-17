package com.projects.view;

import com.projects.Main;
import com.projects.model.Sprite;
import com.projects.model.Structure;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

/**
 * Created by Dan on 8/17/2015.
 */
public class WorldViewController
{
    @FXML
    private Canvas worldCanvas;

    private GraphicsContext gc;
    private AnimationTimer animationTimer;
    private Main main;

    @FXML
    private void initialize()
    {
        gc = worldCanvas.getGraphicsContext2D();

        //final long start = System.nanoTime();

        animationTimer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                List<Structure> worldStructures = main.getWorldStructureData();

                for (Structure structure : worldStructures)
                {
                    Sprite structureSprite = structure.getSprite();
                    gc.drawImage(structureSprite.getImage(), structureSprite.getXPosition(), structureSprite.getYPosition());
                }
            }
        };

        animationTimer.start();
    }

    public void setMain(Main main)
    {
        this.main = main;
    }
}
