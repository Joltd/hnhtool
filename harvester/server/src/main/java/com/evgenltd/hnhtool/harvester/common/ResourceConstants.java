package com.evgenltd.hnhtool.harvester.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    public static final String HOUSE_WALL = "gfx/terobjs/arch/hwall";
    public static final String PALISADE_SEGMENT = "gfx/terobjs/arch/palisadeseg";
    public static final String PALISADE_CORNER_POST = "gfx/terobjs/arch/palisadecp";
    public static final String POLE_SEGMENT = "gfx/terobjs/arch/poleseg";

    public static final String CELLAR_DOOR = "gfx/terobjs/arch/cellardoor";
    public static final String CELLAR_STAIR = "gfx/terobjs/arch/cellarstairs";
    public static final String TIMBER_HOUSE_DOOR = "gfx/terobjs/arch/timberhouse-door";
    public static final String TIMBER_HOUSE = "gfx/terobjs/arch/timberhouse";

    public static final String CUPBOARD = "gfx/terobjs/cupboard";

    public static final Map<String,String> ITEM_TO_STACK_MATCHING = new HashMap<>();
    static {
        ITEM_TO_STACK_MATCHING.put("", "");
    }

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

    public static String getMatchedStack(final String itemResource) {
        return ITEM_TO_STACK_MATCHING.get(itemResource);
    }
}
