package com.projects.view;

import com.projects.Main;
import com.projects.helper.Constants;
import com.projects.helper.ProductionState;
import com.projects.helper.Utils;
import com.projects.model.Building;
import com.projects.model.PowerPlant;
import com.projects.model.Sprite;
import com.projects.model.Structure;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 8/17/2015.
 */
public class WorldViewController {
    private final int selectionRange = 14;
    @FXML
    private Canvas worldCanvas;

    private Main main;
    private GraphicsContext gc;
    private Sprite selectionSprite;
    private Structure selected = null;

    private Color highUsage = new Color(213f / 255f, 43f / 255f, 43f / 255f, 1);
    private Color mediumUsage = new Color(213f / 255f, 142f / 255f, 61f / 255f, 1);
    private Color averageUsage = new Color(204f / 255f, 213f / 255f, 87f / 255f, 1);
    private Color lowUsage = new Color(63f / 255f, 57f / 255f, 57f / 255f, 1);
    private Label highUsageLabel = new Label("high usage");
    private Label mediumUsageLabel = new Label("medium usage");
    private Label averageUsageLabel = new Label("average usage");
    private Label lowUsageLabel = new Label("low usage");

    private boolean selectionMade = false;
    private boolean mouseDown = false;
    private boolean showLegend = true;

    long start = 0;
    private int boxSize = 15;
    private int legendXOffset = 30;
    private int legendYOffset = 30;
    private int legendYSpacing = 20;

    @FXML
    private void initialize() {
        try {

            BufferedImage bufferedImage = ImageIO.read(new File(Utils.getWorkingDir() + "/images/Selection.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            selectionSprite = new Sprite(image, 0, 0);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        gc = worldCanvas.getGraphicsContext2D();

        start = System.nanoTime();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                gc.clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
                List<Structure> worldStructures = main != null ? main.getWorldStructureData() : new ArrayList<>();

                for (Structure structure : worldStructures) {
                    if (structure instanceof Building) {
                        Building building = (Building) structure;

                        switch (building.getDemandState()) {
                            case LOW: {
                                building.getAnimatedSprite().setFrame(0);
                            }
                            break;
                            case AVERAGE: {
                                building.getAnimatedSprite().setFrame(1);
                            }
                            break;
                            case MEDIUM: {
                                building.getAnimatedSprite().setFrame(2);
                            }
                            break;
                            case HIGH: {
                                building.getAnimatedSprite().setFrame(3);
                            }
                            break;
                        }
                    } else {
                        PowerPlant powerPlant = (PowerPlant) structure;

                        if (powerPlant.getProductionState() == ProductionState.PRODUCING) {
                            if (powerPlant.getAnimatedSprite().getAnimation().isPlaying())
                                powerPlant.getAnimatedSprite().animate(now);
                            else {
                                powerPlant.getAnimatedSprite().getAnimation().start();
                                powerPlant.getAnimatedSprite().animate(now);
                            }
                        } else {
                            if (powerPlant.getAnimatedSprite().getAnimation().getFrame() != 0)
                                powerPlant.getAnimatedSprite().animate(now);
                            else {
                                powerPlant.getAnimatedSprite().getAnimation().stop();powerPlant.getAnimatedSprite().animate(now);
                            }
                        }
                    }

                    Sprite structureSprite = structure.getAnimatedSprite();
                    gc.drawImage(structureSprite.getImage(), structureSprite.getXPosition(), structureSprite.getYPosition());

                }

                if (selectionMade) {
                    gc.drawImage(selectionSprite.getImage(), selectionSprite.getXPosition(), selectionSprite.getYPosition());
                }

                if (showLegend) {
                    float yOffset = legendYOffset;

                    gc.setFill(highUsage);
                    gc.fillRoundRect(legendXOffset, yOffset, boxSize, boxSize, 10, 10);
                    gc.setFill(Color.BLACK);
                    gc.setFont(highUsageLabel.getFont());
                    gc.fillText(highUsageLabel.getText(), legendXOffset + 40, yOffset + boxSize);

                    yOffset += legendYSpacing;

                    gc.setFill(mediumUsage);
                    gc.fillRoundRect(legendXOffset, yOffset, boxSize, boxSize, 10, 10);
                    gc.setFill(Color.BLACK);
                    gc.setFont(mediumUsageLabel.getFont());
                    gc.fillText(mediumUsageLabel.getText(), legendXOffset + 40, yOffset + boxSize);

                    yOffset += legendYSpacing;

                    gc.setFill(averageUsage);
                    gc.fillRoundRect(legendXOffset, yOffset, boxSize, boxSize, 10, 10);
                    gc.setFill(Color.BLACK);
                    gc.setFont(averageUsageLabel.getFont());
                    gc.fillText(averageUsageLabel.getText(), legendXOffset + 40, yOffset + boxSize);

                    yOffset += legendYSpacing;

                    gc.setFill(lowUsage);
                    gc.fillRoundRect(legendXOffset, yOffset, boxSize, boxSize, 10, 10);
                    gc.setFill(Color.BLACK);
                    gc.setFont(lowUsageLabel.getFont());
                    gc.fillText(lowUsageLabel.getText(), legendXOffset + 40, yOffset + boxSize);
                }
            }
        };

        animationTimer.start();
        selectionMade = false;
    }

    @FXML
    private void handleMousePressed(MouseEvent mouseEvent) {
        Rectangle2D selectionRect = new Rectangle2D(mouseEvent.getX() - selectionRange / 2, mouseEvent.getY() - selectionRange / 2, selectionRange, selectionRange);
        mouseDown = true;

        if (selected == null || !selected.getAnimatedSprite().intersects(selectionRect)) // prioritize keeping the current selection
        {
            clearSelection();
            List<Structure> worldStructures = main.getWorldStructureData();

            for (Structure structure : worldStructures) {
                if (structure.getAnimatedSprite().intersects(selectionRect)) {
                    setSelection(structure);
                    main.selectedStructureProperty().set(structure);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (mouseDown && selected != null) {
            selected.getAnimatedSprite().setXPosition(mouseEvent.getX() - selected.getAnimatedSprite().getImage().getWidth() / 2);
            selected.getAnimatedSprite().setYPosition(mouseEvent.getY() - selected.getAnimatedSprite().getImage().getHeight() / 2);
            selectionSprite.setXPosition((selected.getAnimatedSprite().getXPosition() + selected.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
            selectionSprite.setYPosition((selected.getAnimatedSprite().getYPosition() + selected.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        }
    }

    @FXML
    private void handleMouseReleased(MouseEvent mouseEvent) {
        mouseDown = false;

        if (selected != null) {
            if (selected.getAnimatedSprite().getXPosition() < 0) {
                selected.getAnimatedSprite().setXPosition(0);
            } else if (selected.getAnimatedSprite().getXPosition() > worldCanvas.getWidth() - Constants.IMAGE_SIZE) {
                selected.getAnimatedSprite().setXPosition(worldCanvas.getWidth() - Constants.IMAGE_SIZE);
            }

            if (selected.getAnimatedSprite().getYPosition() < 0) {
                selected.getAnimatedSprite().setYPosition(0);
            } else if (selected.getAnimatedSprite().getYPosition() > worldCanvas.getHeight() - Constants.IMAGE_SIZE) {
                selected.getAnimatedSprite().setYPosition(worldCanvas.getHeight() - Constants.IMAGE_SIZE);
            }

            selectionSprite.setXPosition((selected.getAnimatedSprite().getXPosition() + selected.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
            selectionSprite.setYPosition((selected.getAnimatedSprite().getYPosition() + selected.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        }
    }

    public void clearSelection() {
        selectionMade = false;
        selected = null;
    }

    private void setSelection(Structure structure) {
        selectionMade = true;
        selectionSprite.setXPosition((structure.getAnimatedSprite().getXPosition() + structure.getAnimatedSprite().getImage().getWidth() / 2) - selectionSprite.getImage().getWidth() / 2);
        selectionSprite.setYPosition((structure.getAnimatedSprite().getYPosition() + structure.getAnimatedSprite().getImage().getHeight() / 2) - selectionSprite.getImage().getHeight() / 2);
        selected = structure;
    }

    public void setMain(Main main) {
        this.main = main;

        main.selectedStructureProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue == null) {
                clearSelection();
            } else if (selected == null || selected.getId() != newValue.getId()) {
                clearSelection();
                List<Structure> worldStructures = main.getWorldStructureData();

                for (Structure structure : worldStructures) {
                    if (structure.getId() == newValue.getId()) {
                        setSelection(structure);
                    }
                }
            }
        });
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public double getWidth() {
        return worldCanvas.getWidth();
    }

    public double getHeight() {
        return worldCanvas.getHeight();
    }
}
