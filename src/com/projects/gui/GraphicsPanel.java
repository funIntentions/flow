package com.projects.gui;

import com.projects.models.Structure;
import com.projects.models.StructureImage;
import com.projects.systems.StructureManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dan on 6/10/2015.
 */
public class GraphicsPanel extends JPanel implements SubscribedView
{
    Dimension worldDimensions;
    List<StructureImage> images;

    public GraphicsPanel()
    {
        setBackground(Color.PINK);
        worldDimensions = new Dimension(800, 600); // this is the preferred dimension
        images = new ArrayList<StructureImage>();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(800, 600);
    }

    @Override
    public void paintComponent(Graphics g) {
        int margin = 10;
        Dimension dim = getSize();
        dim.setSize(dim.getWidth() - margin * 2, dim.getHeight() - margin * 2);
        Dimension scaledWorld = getScaledDimension(worldDimensions, dim);
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(margin + (int)(dim.getWidth()/2 - scaledWorld.getWidth()/2), margin + (int)(dim.getHeight()/2 - scaledWorld.getHeight()/2), (int)scaledWorld.getWidth(), (int)scaledWorld.getHeight());

        for (StructureImage structureImage : images)
        {
            g.drawImage(structureImage.getImage(), 0, 0, null);
        }
    }

    public void modelPropertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(StructureManager.PC_IMAGES_READY))
        {
            images = (List<StructureImage>)event.getNewValue();
        }
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary)
    {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // Scale to meet bounds

        // first check if we need to scale width
        /*if (new_width < bound_width)
        {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height < bound_height)
        {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }*/

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
