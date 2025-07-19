package com.frostplugins.itemscommand.listeners;

import com.frostplugins.itemscommand.cache.ItemsCommandCache;
import com.frostplugins.itemscommand.interfaces.Interface;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.services.ItemsCommandService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class InterfaceInteract implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().startsWith("§7Gerenciador de Itens Clicaveis")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        handlePageNavigation(player, displayName);

        ItemsCommandObject itemCommand = ItemsCommandService.getItemsCommandByItem(clicked);
        if (itemCommand != null) {
            handleItemAttributes(player, clicked, itemCommand);
        }
    }

    private void handlePageNavigation(Player player, String displayName) {
        if (displayName.equalsIgnoreCase("← Página anterior") || displayName.equalsIgnoreCase("Próxima página →")) {
            int currentPage = ItemsCommandCache.getPlayerPage(player.getUniqueId());
            int newPage = displayName.equalsIgnoreCase("← Página anterior")
                    ? Math.max(1, currentPage - 1)
                    : currentPage + 1;

            ItemsCommandCache.setPlayerPage(player.getUniqueId(), newPage);
            player.openInventory(Interface.get(newPage));
        }
    }

    private void handleItemAttributes(Player player, ItemStack clicked, ItemsCommandObject itemCommand) {
        List<String> attributes = itemCommand.getAttributes();
        String subItem = ItemsCommandService.getCurrentSubItemFromItem(clicked);

        if (attributes.contains("integer")) {
            ItemsCommandCache.setWaitingAttributeItem(player.getUniqueId(), clicked);
            ItemsCommandCache.setWaitingAttributeObject(player.getUniqueId(), itemCommand);
            player.closeInventory();
            player.sendMessage("§eDigite o valor do atributo (somente números) ou §c'cancelar'§e.");
        } else {
            giveItemToPlayer(player, itemCommand, subItem, null, clicked.getAmount());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();

        if (ItemsCommandCache.getWaitingAttributeItem(uuid) == null) return;

        event.setCancelled(true);

        if (message.equalsIgnoreCase("cancelar")) {
            cancelOperation(player);
        } else if (message.matches("\\d+")) {
            int attributeValue = Integer.parseInt(message);
            processAttributeValue(player, attributeValue);
        } else {
            player.sendMessage("§cValor inválido. Digite apenas números ou 'cancelar'.");
        }
    }

    private void cancelOperation(Player player) {
        ItemsCommandCache.clearPlayerCache(player.getUniqueId());
        player.sendMessage("§cOperação cancelada com sucesso.");
    }

    private void processAttributeValue(Player player, int attributeValue) {
        ItemStack originalItem = ItemsCommandCache.getWaitingAttributeItem(player.getUniqueId());
        ItemsCommandObject itemCommand = ItemsCommandCache.getWaitingAttributeObject(player.getUniqueId());

        String subItem = ItemsCommandService.getCurrentSubItemFromItem(originalItem);

        giveItemToPlayer(player, itemCommand, subItem, String.valueOf(attributeValue), originalItem.getAmount());
        ItemsCommandCache.clearPlayerCache(player.getUniqueId());
    }

    private void giveItemToPlayer(Player player, ItemsCommandObject itemCommand, String subItem, String attribute, int amount) {
        ItemStack item = ItemsCommandObject.createItem(itemCommand, subItem, attribute);
        assert item != null;
        item.setAmount(amount);
        player.getInventory().addItem(item);
        player.sendMessage("§aVocê pegou o item " + item.getItemMeta().getDisplayName());
    }
}
