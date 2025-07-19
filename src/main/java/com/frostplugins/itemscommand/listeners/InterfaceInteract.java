package com.frostplugins.itemscommand.listeners;

import com.frostplugins.itemscommand.interfaces.Interface;
import com.frostplugins.itemscommand.loader.ItemsCommandLoader;
import com.frostplugins.itemscommand.objects.ItemsCommandObject;
import com.frostplugins.itemscommand.utils.ItemsCommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InterfaceInteract implements Listener {

    private final Map<UUID, ItemStack> waitingAttributeItem = new HashMap<>();
    private final Map<UUID, ItemsCommandObject> waitingAttributeObject = new HashMap<>();

    private final Map<UUID, Integer> playerPage = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().startsWith("§7Gerenciador de Itens Clicaveis")) return;

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;

        String display = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (clicked.getType() == Material.ARROW) {
            if (display.equalsIgnoreCase("← Página anterior")) {
                int currentPage = playerPage.getOrDefault(player.getUniqueId(), 1);
                int newPage = Math.max(1, currentPage - 1);
                playerPage.put(player.getUniqueId(), newPage);
                player.openInventory(Interface.get(newPage));
                return;
            }

            if (display.equalsIgnoreCase("Próxima página →")) {
                int currentPage = playerPage.getOrDefault(player.getUniqueId(), 1);
                int newPage = currentPage + 1;
                playerPage.put(player.getUniqueId(), newPage);
                player.openInventory(Interface.get(newPage));
                return;
            }
        }

        ItemsCommandObject itemCommand = ItemsCommandUtil.getItemsCommandByItem(clicked, ItemsCommandLoader.getItemsList());
        if (itemCommand == null) return;

        List<String> attributes = itemCommand.getAttributes();

        if (attributes != null && attributes.contains("integer")) {
            waitingAttributeItem.put(player.getUniqueId(), clicked);
            waitingAttributeObject.put(player.getUniqueId(), itemCommand);
            player.closeInventory();
            player.sendMessage("§eDigite o valor do atributo (somente números) ou §c'cancelar'§e.");
        } else {
            player.getInventory().addItem(clicked);
            player.sendMessage("§aVocê pegou o item " + clicked.getItemMeta().getDisplayName());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();

        if (!waitingAttributeItem.containsKey(uuid)) return;

        event.setCancelled(true);

        if (message.equalsIgnoreCase("cancelar")) {
            waitingAttributeItem.remove(uuid);
            waitingAttributeObject.remove(uuid);
            player.sendMessage("§cOperação cancelada com sucesso.");
            return;
        }

        if (!message.matches("\\d+")) {
            player.sendMessage("§cValor inválido. Digite apenas números ou 'cancelar'.");
            return;
        }

        int attributeValue = Integer.parseInt(message);
        ItemStack originalItem = waitingAttributeItem.remove(uuid);

        ItemStack finalItem = ItemsCommandUtil.replaceItemAttributes(originalItem, attributeValue);

        player.getInventory().addItem(finalItem);
        player.sendMessage("§aVocê pegou o item " + finalItem.getItemMeta().getDisplayName());
    }
}
