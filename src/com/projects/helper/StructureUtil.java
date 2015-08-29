package com.projects.helper;

/**
 * Created by Dan on 7/29/2015.
 */
public class StructureUtil {
    private static int nextStructureId = 0;

    public static int getNextStructureId() {
        return nextStructureId++;
    }
}
