package dev.kefany.commands;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;


import org.jetbrains.annotations.NotNull;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.GameProfile;

import dev.kefany.utils.HexUtil;
import dev.kefany.Main;

public class EnderCMD implements CommandExecutor {
    public boolean onCommand(@NotNull final CommandSender commandSender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] strings) {
        final Player player = (Player) commandSender;
        if (player == null) {
            return false;
        }
            if (!commandSender.hasPermission("keenderchest.admin")) {
                commandSender.sendMessage(HexUtil.translate(Main.getInstance().getConfig().getString("messages.noPermission")));
                return true;
            }
            if (strings.length == 0) {
                commandSender.sendMessage(HexUtil.color("&dKeEnderChest &f1.0"));
                commandSender.sendMessage("");
                commandSender.sendMessage(HexUtil.color("&d/enderchest &agive &e[player]"));
                commandSender.sendMessage(HexUtil.color("&d/enderchest &areload"));
                commandSender.sendMessage("");
                return true;
            }
            if (strings[0].equals("reload")) {
                player.sendMessage(HexUtil.color("&aDone!"));
                Main.getInstance().reloadConfig();
                return true;
            }
            if (strings[0].equals("give")) {
                if (strings.length < 2) {
                    return true;
                }
                final String playerName = strings[1];
                final Player nick = Main.getInstance().getServer().getPlayer(playerName);
                if (nick == null) {
                    commandSender.sendMessage("Player not found!");
                    return true;
                }
                final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                final SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", Main.getInstance().getConfig().getString("item_enderchest.texture")));
                try {
                    final Field profileField = itemMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(itemMeta, profile);
                } catch (final IllegalAccessException | NoSuchFieldException ex2) {
                    ex2.printStackTrace();
                }
                itemMeta.setDisplayName(HexUtil.translate(Main.getInstance().getConfig().getString("item_enderchest.name")));
                itemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("ender"), PersistentDataType.STRING, "one");
                final boolean shouldGlow = Main.getInstance().getConfig().getBoolean("item_enderchest.glow");
                if (shouldGlow) {
                    itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
                    itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
                }
                final List<String> lore = Main.getInstance().getConfig().getStringList("item_enderchest.lore");
                final List<String> translatedLore = new ArrayList<String>();
                for (final String line : lore) {
                    final String translatedLine = HexUtil.translate(line);
                    translatedLore.add(translatedLine);
                }
                itemMeta.setLore(translatedLore);
                itemStack.setItemMeta(itemMeta);
                nick.getInventory().addItem(new ItemStack[]{itemStack});
                return true;
            }
            return true;
        }
    }
