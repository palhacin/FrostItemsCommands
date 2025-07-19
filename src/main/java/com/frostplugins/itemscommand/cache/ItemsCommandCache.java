package com.frostplugins.itemscommand.cache;

import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemsCommandCache {

    private static final Map<UUID, ItemStack> waitingAttributeItem = new HashMap<>();
    private static final Map<UUID, ItemsCommandObject> waitingAttributeObject = new HashMap<>();

    private static final Map<UUID, Integer> playerPage = new HashMap<>();

    public static void setWaitingAttributeItem(UUID uuid, ItemStack itemStack) {
        waitingAttributeItem.put(uuid, itemStack);
    }

    public static ItemStack getWaitingAttributeItem(UUID uuid) {
        return waitingAttributeItem.get(uuid);
    }

    public static void setWaitingAttributeObject(UUID uuid, ItemsCommandObject itemCommandObject) {
        waitingAttributeObject.put(uuid, itemCommandObject);
    }

    public static ItemsCommandObject getWaitingAttributeObject(UUID uuid) {
        return waitingAttributeObject.get(uuid);
    }

    public static void setPlayerPage(UUID uuid, int page) {
        playerPage.put(uuid, page);
    }

    public static int getPlayerPage(UUID uuid) {
        return playerPage.getOrDefault(uuid, 1);
    }

    public static void clearPlayerCache(UUID uuid) {
        waitingAttributeItem.remove(uuid);
        waitingAttributeObject.remove(uuid);
        playerPage.remove(uuid);
    }
}
