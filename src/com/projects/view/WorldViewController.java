package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.ProductionState;
import com.projects.model.Building;
import com.projects.model.PowerPlant;
import com.projects.model.Sprite;
import com.projects.model.Structure;
import javafx.animation.AnimationTimer;
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

        final long start = System.nanoTime();

        animationTimer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                double time = (now - start) / 1000000000.0;

                gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
                List<Structure> worldStructures = main.getWorldStructureData();

                if (selectionMade)
                {
                    gc.drawImage(selectionSprite.getImage(), selectionSprite.getXPosition(), selectionSprite.getYPosition());
                }

                for (Structure structure : worldStructures)
                {
                    if (structure instanceof Building)
                    {
                        Building building = (Building)structure;

                        switch (building.getDemandState())
                        {
                            case LOW:
                            {
                                building.getAnimatedSprite().setFrame(0);
                            } break;
                            case AVERAGE:
                            {
                                building.getAnimatedSprite().setFrame(1);
                            } break;
                            case MEDIUM:
                            {
                                building.getAnimatedSprite().setFrame(2);
                            } break;
                            case HIGH:
                            {
                                building.getAnimatedSprite().setFrame(3);
                            } break;
                        }
                    }
                    else
                    {
                        PowerPlant powerPlant = (PowerPlant)structure;

                        if (powerPlant.getProductionState() == ProductionState.PRODUCING)
                        {
                            powerPlant.getAnimatedSprite().animate(time);
                        }
                        else
                        {
                            powerPlant.getAnimatedSprite().setFrame(0);
                        }
                    }

                    Sprite structureSprite = structure.getAnimatedSprite();
                    gc.drawImage(structureSprite.getImage(), structureSprite.getXPosition(), structureSprite.getYPosition());
                }
            }
        };

        animationTimer.start();
        selectionMade = false;
    }

    private void clearSelection()
    {
        selectionMade = false;
        selected = null;
    }

    private void setSelection(Structure structure)
    {
        selectionMade = true;
        selectionSprite.setXPosition((structure.getAnimatedSprite().getXPosition() + structure.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
        selectionSprite.setYPosition((structure.getAnimatedSprite().getYPosition() + structure.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        selected = structure;
    }

    @FXML
    private void handleMousePressed(MouseEvent mouseEvent)
    {
        Rectangle2D selectionRect = new Rectangle2D(mouseEvent.getX() - selectionRange/2, mouseEvent.getY() - selectionRange/2, selectionRange, selectionRange);
        mouseDown = true;

        if (selected == null || !selected.getAnimatedSprite().intersects(selectionRect)) // prioritize keeping the current selection
        {
            clearSelection();
            List<Structure> worldStructures = main.getWorldStructureData();

            for (Structure structure : worldStructures)
            {
                if (structure.getAnimatedSprite().intersects(selectionRect))
                {
                    setSelection(structure);
                    main.selectedStructureProperty().set(structure);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent mouseEvent)
    {
        if (mouseDown && selected != null)
        {
            selected.getAnimatedSprite().setXPosition(mouseEvent.getX() - selected.getAnimatedSprite().getImage().getWidth()/2);
            selected.getAnimatedSprite().setYPosition(mouseEvent.getY() - selected.getAnimatedSprite().getImage().getHeight()/2);
            selectionSprite.setXPosition((selected.getAnimatedSprite().getXPosition() + selected.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
            selectionSprite.setYPosition((selected.getAnimatedSprite().getYPosition() + selected.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent mouseEvent)
    {
        mouseDown = false;

        if (selected != null)
        {
            if (selected.getAnimatedSprite().getXPosition() < 0)
            {
                selected.getAnimatedSprite().setXPosition(0);
            }
            else if (selected.getAnimatedSprite().getXPosition() > worldCanvas.getWidth() - Constants.IMAGE_SIZE)
            {
                selected.getAnimatedSprite().setXPosition(worldCanvas.getWidth() - Constants.IMAGE_SIZE);
            }

            if (selected.getAnimatedSprite().getYPosition() < 0)
            {
                selected.getAnimatedSprite().setYPosition(0);
            }
            else if (selected.getAnimatedSprite().getYPosition() > worldCanvas.getHeight() - Constants.IMAGE_SIZE)
            {
                selected.getAnimatedSprite().setYPosition(worldCanvas.getHeight() - Constants.IMAGE_SIZE);
            }

            selectionSprite.setXPosition((selected.getAnimatedSprite().getXPosition() + selected.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
            selectionSprite.setYPosition((selected.getAnimatedSprite().getYPosition() + selected.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        }
    }

    public void setMain(Main main)
    {
        this.main = main;

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            if (selected == null || selected.getId() != newValue.getId())
            {
                clearSelection();
                List<Structure> worldStructures = main.getWorldStructureData();

                for (Structure structure : worldStructures)
                {
                    if (structure.getId() == newValue.getId())
                    {
                        setSelection(structure);
                    }
                }
            }
        });
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
