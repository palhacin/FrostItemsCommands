package com.frostplugins.itemscommand.interfaces;

import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Interface {

    private static final int ITEMS_PER_PAGE = 7;

    public static Inventory get(int page) {
        List<ItemStack> allItems = new ArrayList<>();

        for (ItemsCommandObject itemCommand : ItemsCommandLoader.getItemsList()) {
            boolean hasSubItems = itemCommand.getSubItems() != null && !itemCommand.getSubItems().isEmpty();

            if (hasSubItems) {
                for (String subItem : itemCommand.getSubItems()) {
                    ItemStack item = ItemsCommandObject.createItem(itemCommand, subItem, "0");
                    if (item != null) allItems.add(item);
                }
            } else {
                ItemStack item = ItemsCommandObject.createItem(itemCommand, null, null);
                if (item != null) allItems.add(item);
            }
        }

        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));

        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§7Gerenciador de Itens Clicaveis #" + page);

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());

        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            inventory.setItem(slot++, allItems.get(i));
        }

        if (page > 1) {
            ItemStack backArrow = new ItemBuilder(Material.ARROW)
                    .setName("§c← Página anterior")
                    .build();
            inventory.setItem(18, backArrow);
        }

        if (page < totalPages) {
            ItemStack nextArrow = new ItemBuilder(Material.ARROW)
                    .setName("§aPróxima página →")
                    .build();
            inventory.setItem(26, nextArrow);
        }

        return inventory;
    }
}
