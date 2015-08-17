package com.projects.view;

import com.projects.Main;
import com.projects.model.Sprite;
import com.projects.model.Structure;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

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
    private final int selectionRange = 4;
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

    @FXML
    private void handleMouseClick(MouseEvent mouseEvent)
    {
        List<Structure> worldStructures = main.getWorldStructureData();
        Rectangle2D selectionRect = new Rectangle2D(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX() + selectionRange, mouseEvent.getY() + selectionRange);

        for (Structure structure : worldStructures)
        {
            if (structure.getSprite().intersects(selectionRect))
            {
                main.selectedStructureProperty().set(structure);
                break;
            }
        }
    }

    public void setMain(Main main)
    {
        this.main = main;
    }

    public double getWidth()
    {
       return worldCanvas.getWidth();
    }

    public double getHeight()
    {
        return worldCanvas.getHeight();
    }
}
