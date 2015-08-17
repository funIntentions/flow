package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.model.Sprite;
import com.projects.model.Structure;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
    private final int selectionRange = 14;
    private Sprite selectionSprite = new Sprite(new Image(Constants.SELECTION_IMAGE), 0, 0);
    private boolean selectionMade = false;
    private boolean mouseDown = false;
    private Structure selected = null;
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
                gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
                List<Structure> worldStructures = main.getWorldStructureData();

                if (selectionMade)
                {
                    gc.drawImage(selectionSprite.getImage(), selectionSprite.getXPosition(), selectionSprite.getYPosition());
                }

                for (Structure structure : worldStructures)
                {
                    Sprite structureSprite = structure.getSprite();
                    gc.drawImage(structureSprite.getImage(), structureSprite.getXPosition(), structureSprite.getYPosition());
                }
            }
        };

        animationTimer.start();
        selectionMade = false;
    }

    @FXML
    private void handleMousePressed(MouseEvent mouseEvent)
    {
        mouseDown = true;
        selectionMade = false;
        selected = null;

        List<Structure> worldStructures = main.getWorldStructureData();
        Rectangle2D selectionRect = new Rectangle2D(mouseEvent.getX() - selectionRange/2, mouseEvent.getY() - selectionRange/2, selectionRange, selectionRange);

        for (Structure structure : worldStructures)
        {
            if (structure.getSprite().intersects(selectionRect))
            {
                selectionMade = true;
                selectionSprite.setXPosition((structure.getSprite().getXPosition() + structure.getSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
                selectionSprite.setYPosition((structure.getSprite().getYPosition() + structure.getSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
                selected = structure;
                main.selectedStructureProperty().set(structure);
                break;
            }
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent mouseEvent)
    {
        if (mouseDown && selected != null)
        {
            selected.getSprite().setXPosition(mouseEvent.getX() - selected.getSprite().getImage().getWidth()/2);
            selected.getSprite().setYPosition(mouseEvent.getY() - selected.getSprite().getImage().getHeight()/2);
            selectionSprite.setXPosition((selected.getSprite().getXPosition() + selected.getSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
            selectionSprite.setYPosition((selected.getSprite().getYPosition() + selected.getSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent mouseEvent)
    {
        mouseDown = false;
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
