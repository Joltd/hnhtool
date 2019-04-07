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

    public static final Long PLAYER = 0L;
    public static final Long HOUSE_WALL = 3949L; // gfx/terobjs/arch/hwall
    public static final Long PALISADE_SEGMENT = 3956L; // gfx/terobjs/arch/palisadeseg
    public static final Long PALISADE_CORNER_POST = 3953L; // gfx/terobjs/arch/palisadecp
    public static final Long POLE_SEGMENT = 3959L; // gfx/terobjs/arch/poleseg

    public static final Long CELLAR_DOOR = 3944L; // gfx/terobjs/arch/cellardoor
    public static final Long CELLAR_STAIR = 3945L; //
    public static final Long TIMBER_HOUSE_DOOR = 3970L; // gfx/terobjs/arch/timberhouse-door
    public static final Long TIMBER_HOUSE = 3972L;

    public static final Long CUPBOARD = 6426L; // gfx/terobjs/cupboard

    public static boolean isWaste(final Long resourceId) {
        return Arrays.asList(
                PLAYER,
                HOUSE_WALL,
                PALISADE_SEGMENT,
                PALISADE_CORNER_POST,
                POLE_SEGMENT
        ).contains(resourceId);
    }

    public static boolean isPlayer(final Long resourceId) {
        return Objects.equals(resourceId, PLAYER);
    }

    public static boolean isDoorway(final Long resourceId) {
        return Arrays.asList(
                CELLAR_DOOR,
                CELLAR_STAIR,
                TIMBER_HOUSE_DOOR,
                TIMBER_HOUSE
        ).contains(resourceId);
    }

    public static boolean isContainer(final Long resourceId) {
        return Arrays.asList(
                CUPBOARD
        ).contains(resourceId);
    }

    public static boolean isDoorwayToBuilding(final Long resourceId) {
        return Arrays.asList(
                CELLAR_DOOR,
                CELLAR_STAIR,
                TIMBER_HOUSE
        ).contains(resourceId);
    }

    public static boolean isDoorwayToHole(final Long resourceId) {
        return false;
    }

    public static boolean isDoorwayToMine(final Long resourceId) {
        return false;
    }
}
