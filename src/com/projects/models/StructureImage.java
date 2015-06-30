package com.projects.models;

import com.projects.helper.StructureType;

import java.awt.image.BufferedImage;

/**
 * Created by Dan on 6/30/2015.
 */
public class StructureImage
{
    private BufferedImage image;
    private StructureType type;

    public StructureImage(StructureType structureType, BufferedImage bufferedImage)
    {
        image = bufferedImage;
        type = structureType;
    }

    public StructureType getType() {
        return type;
    }

    public void setType(StructureType type) {
        this.type = type;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
