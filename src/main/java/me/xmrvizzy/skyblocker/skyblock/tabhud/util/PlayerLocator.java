package me.xmrvizzy.skyblocker.skyblock.tabhud.util;

import me.xmrvizzy.skyblocker.utils.Utils;

/**
 * Uses data from the player list to determine the area the player is in.
 */
public class PlayerLocator {

    public static enum Location {
        DUNGEON,
        GUEST_ISLAND,
        HOME_ISLAND,
        CRIMSON_ISLE,
        DUNGEON_HUB,
        FARMING_ISLAND,
        PARK,
        DWARVEN_MINES,
        CRYSTAL_HOLLOWS,
        END,
        GOLD_MINE,
        DEEP_CAVERNS,
        HUB,
        SPIDER_DEN,
        JERRY,
        GARDEN,
        INSTANCED,
        THE_RIFT,
        UNKNOWN
    }

    public static Location getPlayerLocation() {

        if (!Utils.isOnSkyblock()) {
            return Location.UNKNOWN;
        }

        String areaDesciptor = PlayerListMgr.strAt(41);

        if (areaDesciptor == null || areaDesciptor.length() < 6) {
            return Location.UNKNOWN;
        }

        if (areaDesciptor.startsWith("Dungeon")) {
            return Location.DUNGEON;
        }

        switch (areaDesciptor.substring(6)) {
            case "Private Island":
                String islandType = PlayerListMgr.strAt(44);
                if (islandType == null) {
                    return Location.UNKNOWN;
                } else if (islandType.endsWith("Guest")) {
                    return Location.GUEST_ISLAND;
                } else {
                    return Location.HOME_ISLAND;
                }
            case "Crimson Isle":
                return Location.CRIMSON_ISLE;
            case "Dungeon Hub":
                return Location.DUNGEON_HUB;
            case "The Farming Islands":
                return Location.FARMING_ISLAND;
            case "The Park":
                return Location.PARK;
            case "Dwarven Mines":
                return Location.DWARVEN_MINES;
            case "Crystal Hollows":
                return Location.CRYSTAL_HOLLOWS;
            case "The End":
                return Location.END;
            case "Gold Mine":
                return Location.GOLD_MINE;
            case "Deep Caverns":
                return Location.DEEP_CAVERNS;
            case "Hub":
                return Location.HUB;
            case "Spider's Den":
                return Location.SPIDER_DEN;
            case "Jerry's Workshop":
                return Location.JERRY;
            case "Garden":
                return Location.GARDEN;
            case "Instanced":
                return Location.INSTANCED;
            case "The Rift":
            	return Location.THE_RIFT;
            default:
                return Location.UNKNOWN;
        }
    }
}
