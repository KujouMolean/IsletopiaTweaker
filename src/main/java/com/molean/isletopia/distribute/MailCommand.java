package com.molean.isletopia.distribute;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Completion;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.database.MailboxDao;
import com.molean.isletopia.shared.model.Mail;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("mail")
@Singleton
public class MailCommand extends BaseCommand implements Listener {

    private final Map<Inventory, Mail> inventorySet = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType().equals(Material.BUNDLE)) {
            Tasks.INSTANCE.timeout(1, () -> {
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
        if (bundleMeta.getItems().isEmpty()) {
            return;
        }
        itemStack.setItemMeta(bundleMeta);
        byte[] bytes = itemStack.serializeAsBytes();
        System.out.println(bytes.length);
        mail.setData(bytes);
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


    @Default
    @Completion("@players @empty")
    public void onDefault(Player player, String target, String message) {
        UUID uuid = UUIDManager.get(target);
        if (uuid == null) {
            MessageUtils.fail(player, "该玩家未进入过服务器哦!");
            return;
        }
        Inventory inventory = Bukkit.createInventory(player, 9, Component.text("放入送给" + target + "的物品"));
        Mail mail = new Mail();
        mail.setSource(player.getUniqueId());
        mail.setTarget(uuid);
        mail.setLocalDateTime(LocalDateTime.now());
        mail.setMessage(message);
        mail.setClaimed(false);
        inventorySet.put(inventory, mail);
        player.openInventory(inventory);
    }
}
