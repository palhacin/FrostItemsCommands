package com.frostplugins.itemscommand.interfaces;

import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ItemBuilder;
import com.frostplugins.itemscommand.utils.SkullUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Interface {

    private static final int ITEMS_PER_PAGE = 7;

    public static Inventory get(int page) {
        List<ItemStack> allItems = new ArrayList<>();

        for (ItemsCommandObject itemCommand : ItemsCommandLoader.getItemsList()) {
            boolean hasSubItems = itemCommand.getSubItems() != null && !itemCommand.getSubItems().isEmpty();

            if (hasSubItems) {
                for (String subItem : itemCommand.getSubItems()) {
                    String displayName = itemCommand.getDisplayName().replace("{selected_sub_item}", subItem);

                    List<String> lore = new ArrayList<>();
                    if (itemCommand.getDescription() != null) {
                        lore = itemCommand.getDescription().stream()
                                .map(line -> line.replace("{selected_sub_item}", subItem))
                                .collect(Collectors.toList());
                    }

                    ItemStack item = createItem(itemCommand, displayName, lore);
                    if (item != null) allItems.add(item);
                }
            } else {
                String displayName = itemCommand.getDisplayName().replace("{selected_sub_item}", "");
                List<String> lore = new ArrayList<>();
                if (itemCommand.getDescription() != null) {
                    lore = new ArrayList<>(itemCommand.getDescription());
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, lore.get(i).replace("{selected_sub_item}", ""));
                    }
                }

                ItemStack item = createItem(itemCommand, displayName, lore);
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

    private static ItemStack createItem(ItemsCommandObject itemCommand, String displayName, List<String> lore) {
        if (itemCommand.isHead()) {
            return new ItemBuilder(SkullUtils.getSkull(itemCommand.getHeadUrl()))
                    .setName(displayName)
                    .setAmount(1)
                    .setLore(lore)
                    .build();
        } else {
            Material material = Material.getMaterial(itemCommand.getMaterial().toUpperCase());
            if (material == null) return null;

            return new ItemBuilder(material)
                    .setName(displayName)
                    .setAmount(1)
                    .setLore(lore)
                    .build();
        }
    }
}
