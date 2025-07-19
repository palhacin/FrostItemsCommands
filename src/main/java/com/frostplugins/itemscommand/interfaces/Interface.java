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
    private static final String INVENTORY_TITLE = "§7Gerenciador de Itens Clicaveis #";
    private static final String BACK_ARROW_NAME = "§c← Página anterior";
    private static final String NEXT_ARROW_NAME = "§aPróxima página →";

    public static Inventory get(int page) {
        List<ItemStack> allItems = new ArrayList<>();

        ItemsCommandLoader.getItemsList().forEach(itemCommand -> {
            if (itemCommand.getSubItems() != null && !itemCommand.getSubItems().isEmpty()) {
                itemCommand.getSubItems().forEach(subItem -> {
                    ItemStack item = ItemsCommandObject.createItem(itemCommand, subItem, "0");
                    if (item != null) allItems.add(item);
                });
            } else {
                ItemStack item = ItemsCommandObject.createItem(itemCommand, null, null);
                if (item != null) allItems.add(item);
            }
        });

        int totalPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));

        Inventory inventory = Bukkit.createInventory(null, 3 * 9, INVENTORY_TITLE + page);

        setInventoryItems(inventory, allItems, page);

        addPageNavigationButtons(inventory, page, totalPages);

        return inventory;
    }

    private static void setInventoryItems(Inventory inventory, List<ItemStack> allItems, int page) {
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allItems.size());

        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            inventory.setItem(slot++, allItems.get(i));
        }
    }

    private static void addPageNavigationButtons(Inventory inventory, int page, int totalPages) {
        if (page > 1) {
            ItemStack backArrow = new ItemBuilder(Material.ARROW)
                    .setName(BACK_ARROW_NAME)
                    .build();
            inventory.setItem(18, backArrow);
        }

        if (page < totalPages) {
            ItemStack nextArrow = new ItemBuilder(Material.ARROW)
                    .setName(NEXT_ARROW_NAME)
                    .build();
            inventory.setItem(26, nextArrow);
        }
    }
}
