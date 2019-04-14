package com.evgenltd.hnhtool.harvester.common;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 01:54</p>
 */
public class ResourceConstants {

    public static final String PLAYER = "";
    public static final String HOUSE_WALL = "gfx/terobjs/arch/hwall"; // 3949L
    public static final String PALISADE_SEGMENT = "gfx/terobjs/arch/palisadeseg"; // 3956L
    public static final String PALISADE_CORNER_POST = "gfx/terobjs/arch/palisadecp"; // 3953L
    public static final String POLE_SEGMENT = "gfx/terobjs/arch/poleseg"; // 3959L

    public static final String CELLAR_DOOR = "gfx/terobjs/arch/cellardoor"; // 3944L
    public static final String CELLAR_STAIR = ""; // 3945L
    public static final String TIMBER_HOUSE_DOOR = "gfx/terobjs/arch/timberhouse-door"; // 3970L
    public static final String TIMBER_HOUSE = ""; // 3972L

    public static final String CUPBOARD = "gfx/terobjs/cupboard"; // 6426L

    public static boolean isWaste(final String resourceName) {
        return Arrays.asList(
                PLAYER,
                HOUSE_WALL,
                PALISADE_SEGMENT,
                PALISADE_CORNER_POST,
                POLE_SEGMENT
        ).contains(resourceName);
    }

    public static boolean isPlayer(final String resourceName) {
        return Objects.equals(resourceName, PLAYER);
    }

    public static boolean isDoorway(final String resourceName) {
        return Arrays.asList(
                CELLAR_DOOR,
                CELLAR_STAIR,
                TIMBER_HOUSE_DOOR,
                TIMBER_HOUSE
        ).contains(resourceName);
    }

    public static boolean isContainer(final String resourceName) {
        return Arrays.asList(
                CUPBOARD
        ).contains(resourceName);
    }

    public static boolean isDoorwayToBuilding(final String resourceName) {
        return Arrays.asList(
                CELLAR_DOOR,
                CELLAR_STAIR,
                TIMBER_HOUSE
        ).contains(resourceName);
    }

    public static boolean isDoorwayToHole(final String resourceName) {
        return false;
    }

    public static boolean isDoorwayToMine(final String resourceName) {
        return false;
    }
}
