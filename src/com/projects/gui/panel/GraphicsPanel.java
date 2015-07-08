package com.projects.gui.panel;

import com.projects.gui.SubscribedView;
import com.projects.helper.ImageType;
import com.projects.models.Structure;
import com.projects.systems.StructureManager;
import com.projects.systems.simulation.World;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Dan on 6/10/2015.
 */
public class GraphicsPanel extends JPanel implements SubscribedView
{
    Dimension worldDimensions;
    Dimension cellDimensions;
    HashMap<ImageType, BufferedImage> images;
    HashMap<ImageType, BufferedImage> scaledImages;
    List<Structure> structures;
    Color worldSpaceColor;

    public GraphicsPanel()
    {
        setBackground(Color.DARK_GRAY);
        int numberOfHorizontalCells = 25;
        int numberOfVerticalCells = 25;
        cellDimensions = new Dimension(32, 32);
        worldDimensions = new Dimension((int)(numberOfHorizontalCells * cellDimensions.getWidth()), (int)(numberOfVerticalCells * cellDimensions.getHeight())); // this is the preferred dimension
        images = new HashMap<ImageType, BufferedImage>();
        scaledImages = new HashMap<ImageType, BufferedImage>();
        structures = new ArrayList<Structure>();
        worldSpaceColor = new Color(128,202,140);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return worldDimensions;
    }

    @Override
    public Dimension getMaximumSize() {
        return worldDimensions;
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        int margin = 10;
        Dimension dim = getSize();
        dim.setSize(dim.getWidth() - margin * 2, dim.getHeight() - margin * 2);
        Dimension scaledWorld = getScaledDimension(worldDimensions, dim);
        double widthRatio = scaledWorld.getWidth() / worldDimensions.getWidth();
        double heightRatio = scaledWorld.getHeight() / worldDimensions.getHeight();
        Dimension scaledCell = new Dimension((int)(widthRatio * cellDimensions.getWidth()), (int)(heightRatio * cellDimensions.getHeight()));
        super.paintComponent(graphics);
        graphics.setColor(worldSpaceColor);

        int xOffset = margin + (int)(dim.getWidth()/2 - scaledWorld.getWidth()/2);
        int yOffset = margin + (int)(dim.getHeight()/2 - scaledWorld.getHeight()/2);

        graphics.fillRect(xOffset, yOffset, (int) scaledWorld.getWidth(), (int) scaledWorld.getHeight());

        // Scale Images
        for (ImageType imageType : ImageType.values())
        {
            BufferedImage scaledImage = resizeImage(images.get(imageType), widthRatio, heightRatio);
            scaledImages.put(imageType, scaledImage);
        }

        drawCells(graphics, xOffset, yOffset, scaledCell);
    }

    private void drawCells(Graphics graphics, int xOffset, int yOffset, Dimension cell)
    {
        for (Structure structure : structures)
        {
            BufferedImage scaledImage = scaledImages.get(structure.getImage());

            graphics.drawImage(scaledImage, xOffset + structure.getX() * (int)cell.getWidth(), yOffset + structure.getY() * (int)cell.getHeight(), null);
        }
    }

    public BufferedImage resizeImage(BufferedImage before, double widthRatio, double heightRatio)
    {
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(widthRatio, heightRatio);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);
        return after;
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(StructureManager.PC_IMAGES_READY))
        {
            images = (HashMap<ImageType, BufferedImage>)event.getNewValue();
        }
        else if (event.getPropertyName().equals(World.PC_NEW_STRUCTURE))
        {
            structures.add((Structure) event.getNewValue());
        }
        else if (event.getPropertyName().equals(World.PC_REMOVE_STRUCTURE))
        {
            int removed = (Integer)event.getNewValue();

            removeStructure(removed);
            repaint();
        }
        else if (event.getPropertyName().equals(World.PC_NEW_WORLD))
        {
            structures.clear();
            structures = (List<Structure>)event.getNewValue();
        }
        else if (event.getPropertyName().equals(World.PC_STRUCTURE_UPDATE))
        {
            repaint();
        }
    }

    private void removeStructure(int removed)
    {
        for (Structure structure : structures)
        {
            if (structure.getId() == removed)
            {
                structures.remove(structure);
                return;
            }
        }
    }

    public Dimension getScaledDimension(Dimension imgSize, Dimension boundary)
    {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // Scale to fit in bounds

        // first check if we need to scale width
        if (new_width > bound_width)
        {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height)
        {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
