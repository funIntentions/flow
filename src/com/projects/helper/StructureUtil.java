package com.projects.helper;

/**
 * Utility for assisting with structures.
 */
public class StructureUtil {
    private static int nextStructureId = 0;

    public static int getNextStructureId() {
        return nextStructureId++;
    }
}
