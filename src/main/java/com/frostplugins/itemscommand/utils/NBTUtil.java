package com.frostplugins.itemscommand.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class NBTUtil {

    public static ItemStack setString(ItemStack item, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (!nmsItem.hasTag()) {
            nmsItem.setTag(new NBTTagCompound());
        }
        nmsItem.getTag().set(key, new NBTTagString(value));
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static String getString(ItemStack item, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag() && nmsItem.getTag().hasKey(key)) {
            return nmsItem.getTag().getString(key);
        }
        return null;
    }

}
