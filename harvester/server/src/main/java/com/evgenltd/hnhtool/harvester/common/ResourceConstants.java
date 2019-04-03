package com.evgenltd.hnhtool.harvester.common;

import java.util.Arrays;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-04-2019 01:54</p>
 */
public class ResourceConstants {

    public static final Long PLAYER = 0L;
    public static final Long HOUSE_WALL = 3948L; // gfx/terobjs/arch/hwall
    public static final Long CELLAR_DOOR = 3943L; // gfx/terobjs/arch/cellardoor
    public static final Long TIMBER_HOUSE_DOOR = 3969L; // gfx/terobjs/arch/timberhouse-door
    public static final Long CUPBOARD = 6425L; // gfx/terobjs/cupboard

    public static boolean isWaste(final Integer resourceId) {
        return Arrays.asList(
                PLAYER,
                HOUSE_WALL
        ).contains(resourceId.longValue());
    }

    public static boolean isDoorway(final Long resourceId) {
        return Arrays.asList(
                CELLAR_DOOR,
                TIMBER_HOUSE_DOOR
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
                TIMBER_HOUSE_DOOR
        ).contains(resourceId);
    }

    public static boolean isDoorwayToHole(final Long resourceId) {
        return false;
    }

    public static boolean isDoorwayToMine(final Long resourceId) {
        return false;
    }
}
