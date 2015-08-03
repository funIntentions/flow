package com.projects.helper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Dan on 6/8/2015.
 */
public class Utils
{
    /**
     * Finds the extension of a file.
     * @param file the file in question
     * @return the extension of the file
     */
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass)
    {
        for (E e : enumClass.getEnumConstants())
        {
            if(e.name().equalsIgnoreCase(value)) { return true; }
        }

        return false;
    }

    public static String getWorkingDir()
    {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
