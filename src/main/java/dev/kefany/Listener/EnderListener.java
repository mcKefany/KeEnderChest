package dev.kefany.Listener;

import org.bukkit.inventory.Inventory;
import dev.kefany.utils.HexUtil;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import dev.kefany.Main;

public class EnderListener implements Listener
{
    private HashMap<UUID, Long> cooldowns;
    
    public EnderListener() {
        this.cooldowns = new HashMap<UUID, Long>();
    }
    
    @EventHandler
    public void onBlockBreak(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            final ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (container.has(NamespacedKey.fromString("ender"), PersistentDataType.STRING)) {
                    final String value = (String)container.get(NamespacedKey.fromString("ender"), PersistentDataType.STRING);
                    if (value != null && value.equals("one")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("ender"), PersistentDataType.STRING)) {
            final String value = (String)item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("ender"), PersistentDataType.STRING);
            if (value != null && value.equals("one")) {
                final boolean delay = Main.getInstance().getConfig().getBoolean("item_enderchest.delay.enable");
                if (delay) {
                    final long currentTime = System.currentTimeMillis();
                    final long cooldownTime = TimeUnit.SECONDS.toSeconds(Main.getInstance().getConfig().getInt("item_enderchest.delay.time"));
                    long remainingTime = 0L;
                    if (this.cooldowns.containsKey(player.getUniqueId())) {
                        final long lastClickTime = this.cooldowns.get(player.getUniqueId());
                        if (currentTime - lastClickTime < cooldownTime) {
                            remainingTime = cooldownTime - (currentTime - lastClickTime);
                            final String kg = Main.getInstance().getConfig().getString("item_enderchest.delay.message").replace("{time}", String.valueOf(remainingTime / 1000L));
                            player.sendMessage(HexUtil.translate(kg));
                            this.cooldowns.put(player.getUniqueId(), currentTime);
                            event.setCancelled(true);
                            return;
                        }
                        this.cooldowns.put(player.getUniqueId(), currentTime);
                        event.setCancelled(true);
                    }
                }
                final boolean ShouldRetrieve = Main.getInstance().getConfig().getBoolean("item_enderchest.should_retrieve");
                if (ShouldRetrieve) {
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                }
                final Inventory enderChest = player.getEnderChest();
                player.openInventory(enderChest);
            }
        }
    }
}
