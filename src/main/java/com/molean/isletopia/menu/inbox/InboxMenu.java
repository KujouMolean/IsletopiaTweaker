package com.molean.isletopia.menu.inbox;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.mail.InboxObject;
import com.molean.isletopia.mail.MailObject;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InboxMenu implements Listener {
    private final Player player;
    private final Inventory inventory;

    private static final Gson GSON = new Gson();

    private final Map<Integer, UUID> map = new HashMap<>();


    public InboxMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 36, Component.text("邮箱"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        String inbox = UniversalParameter.getParameter(player.getName(), "Inbox");
        if (inbox != null && !inbox.isEmpty()) {
            InboxObject inboxObject = GSON.fromJson(inbox, InboxObject.class);
            List<MailObject> mailObjectList = inboxObject.getMailObjectList();
            for (int i = 0; i < mailObjectList.size() && i < 35; i++) {
                MailObject mailObject = inboxObject.getMailObjectList().get(i);
                map.put(i, mailObject.getUuid());
                if (mailObject.getItemStackList() != null && mailObject.getItemStackList().size() > 0) {
                    ItemStackSheet itemStackSheet = new ItemStackSheet(Material.CHEST, mailObject.getTitle());
                    for (String s : mailObject.getMessage().split("\n")) {
                        itemStackSheet.addLore(s);
                    }
                    itemStackSheet.addLore("§f发件玩家: " + mailObject.getFrom());
                    Timestamp timestamp = new Timestamp(mailObject.getTime());
                    LocalDateTime localDateTime = timestamp.toLocalDateTime();
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm"));
                    itemStackSheet.addLore("§f发件日期: " + format);
                    inventory.setItem(i, itemStackSheet.build());
                } else {
                    ItemStackSheet itemStackSheet = new ItemStackSheet(Material.PAPER, mailObject.getTitle());
                    for (String s : mailObject.getMessage().split("\n")) {
                        itemStackSheet.addLore(s);
                    }
                    inventory.setItem(i, itemStackSheet.build());
                }
            }
        }


        //here place icon
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }
        int slot = event.getSlot();
        if (slot == 35) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
        }
        if (map.containsKey(slot)) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                String inbox = UniversalParameter.getParameter(player.getName(), "Inbox");
                InboxObject inboxObject = GSON.fromJson(inbox, InboxObject.class);
                List<MailObject> mailObjectList = inboxObject.getMailObjectList();
                if (map.get(slot).equals(mailObjectList.get(slot).getUuid())) {
                    MailObject mailObject = mailObjectList.get(slot);
                    mailObjectList.remove(slot);
                    String s = GSON.toJson(inboxObject);

                    UniversalParameter.setParameter(player.getName(), "Inbox", s);
                    List<String> itemStackList = mailObject.getItemStackList();
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        for (String string : itemStackList) {
                            byte[] decode = Base64.getDecoder().decode(string);
                            player.getInventory().addItem(ItemStack.deserializeBytes(decode));
                        }
                    });
                } else {
                    player.sendMessage("§c数据不一致, 请重新领取");
                }
                new InboxMenu(player).open();
            });

        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.getHandlers().unregister(this);
    }
}
