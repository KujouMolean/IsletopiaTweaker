package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.shared.database.MailboxDao;
import com.molean.isletopia.shared.model.Mail;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MailCommand implements TabCompleter, CommandExecutor, Listener {

    private final Map<Inventory, Mail> inventorySet = new HashMap<>();

    public MailCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("mail")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("mail")).setTabCompleter(this);
        PluginUtils.registerEvents(this);
    }


    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType().equals(Material.BUNDLE)) {
            Tasks.INSTANCE.timeout(1,() -> {
                for (ItemStack itemStack : event.getPlayer().getInventory()) {
                    if (itemStack != null && itemStack.getType().equals(Material.BUNDLE)) {
                        BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
                        if (bundleMeta.getItems().isEmpty()) {
                            item.setAmount(0);
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!inventorySet.containsKey(inventory)) {
            return;
        }
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        int slot = event.getSlot();
        ItemStack item = clickedInventory.getItem(slot);
        if (item == null) {
            return;
        }
        if (item.getType().equals(Material.BUNDLE)) {
            event.setCancelled(true);
        }
        if (item.getType().name().contains("SHULKER_BOX")) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
            ShulkerBox blockState = (ShulkerBox) blockStateMeta.getBlockState();

            Inventory shulkerBoxInventory = blockState.getInventory();
            if (shulkerBoxInventory.contains(Material.BUNDLE)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void on(InventoryCloseEvent event) throws SQLException {
        Inventory inventory = event.getInventory();
        if (!inventorySet.containsKey(inventory)) {
            return;
        }
        Mail mail = inventorySet.get(inventory);
        ItemStack itemStack = new ItemStack(Material.BUNDLE);
        BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
        for (ItemStack stack : inventory) {
            if (stack != null && stack.getType() != Material.AIR) {
                bundleMeta.addItem(stack);
            }
        }
        if(bundleMeta.getItems().isEmpty()){
            return;
        }
        itemStack.setItemMeta(bundleMeta);
        mail.setData(itemStack.serializeAsBytes());
        Tasks.INSTANCE.async(() -> {
            try {
                MailboxDao.request(mail);
                inventorySet.remove(inventory);
                UUID source = mail.getSource();
                UUID target = mail.getTarget();
                assert source != null;
                Player player = Bukkit.getPlayer(source);
                if (player != null) {
                    MessageUtils.success(player, "该包裹已经发送给了 " + UUIDManager.get(target));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 2) {
            MessageUtils.info(player, "用法: /mail 某玩家 赠言");
            return true;
        }

        String target = args[0];
        UUID uuid = UUIDManager.get(target);
        if (uuid == null) {
            MessageUtils.fail(player, "该玩家未进入过服务器哦!");
        }

        Inventory inventory = Bukkit.createInventory(player, 54, Component.text("放入送给" + target + "的物品"));
        Mail mail = new Mail();
        mail.setSource(player.getUniqueId());
        mail.setTarget(uuid);
        mail.setLocalDateTime(LocalDateTime.now());
        mail.setMessage(args[1]);
        mail.setClaimed(false);
        inventorySet.put(inventory, mail);
        player.openInventory(inventory);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        Map<UUID, String> snapshot = UUIDManager.INSTANCE.getSnapshot();
        if (args.length == 1) {
            for (String value : snapshot.values()) {
                if (value.startsWith(args[0])) {
                    strings.add(value);
                }
            }
        }
        return strings;
    }
}
