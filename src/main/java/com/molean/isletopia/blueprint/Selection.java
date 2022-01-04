package com.molean.isletopia.blueprint;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.blueprint.obj.BluePrintData;
import com.molean.isletopia.menu.BluePrintMenu;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.ObjectUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Selection implements Listener, CommandExecutor {
    public Selection() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("blueprint")).setExecutor(this);
        remapping.put(Material.REDSTONE_WIRE, Material.REDSTONE);
        remapping.put(Material.WATER, Material.WATER_BUCKET);
        remapping.put(Material.LAVA, Material.LAVA_BUCKET);
        remapping.put(Material.PISTON_HEAD, Material.PISTON);
    }

    private final Map<UUID, Location> firstLocations = new HashMap<>();
    private final Map<UUID, Location> secondLocations = new HashMap<>();
    private final Map<Material, Material> remapping = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().equals(Material.BOOK)) {
            return;
        }
        if (!itemInMainHand.getEnchantments().isEmpty()) {
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            firstLocations.put(event.getPlayer().getUniqueId(), clickedBlock.getLocation());
            MessageUtils.notify(event.getPlayer(), "你已选中第一个点为"
                    + clickedBlock.getX() + ","
                    + clickedBlock.getY() + ","
                    + clickedBlock.getZ());
            event.setCancelled(true);
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            secondLocations.put(event.getPlayer().getUniqueId(), clickedBlock.getLocation());
            MessageUtils.notify(event.getPlayer(), "你已选中第二个点为"
                    + clickedBlock.getX() + ","
                    + clickedBlock.getY() + ","
                    + clickedBlock.getZ());
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void preview(PlayerInteractEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (event.getPlayer().isSneaking()) {
            return;
        }
        if (!itemInMainHand.getType().equals(Material.BOOK)) {
            return;
        }
        if (itemInMainHand.getEnchantments().isEmpty()) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (itemInMainHand.getAmount() > 1) {
            MessageUtils.fail(event.getPlayer(), "只能同时使用一本蓝图。");
            return;
        }
        Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
        byte[] bytes = NMSTagUtils.getAsBytes(itemInMainHand, "BluePrint");
        BluePrintData bluePrintData = (BluePrintData) ObjectUtils.deserialize(bytes);
        if (bluePrintData == null) {
            MessageUtils.fail(event.getPlayer(), "蓝图解析失败!");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new BluePrintMenu(event.getPlayer(), location).open();
        });
    }

    public static void updateItemStack(ItemStack itemStack, BluePrintData bluePrintData) {
        Map<Material, Integer> materialMap = bluePrintData.getMaterialMap();
        int count = 0;
        ArrayList<Component> components = new ArrayList<>();
        int i = 0;
        TextComponent textComponent = Component.text("");
        for (Material material : materialMap.keySet()) {
//            int require = materialMap.getOrDefault(material, 0) - bluePrintTemplate.getStore(material);
//            if (require > 0) {
//                if (count == 0) {
//                    components.add(Component.text("缺少以下材料:"));
//                }
//                textComponent = textComponent.append(Component.text(LangUtils.get(material.translationKey()) + "x" + require + "  "));
//                count++;
//            }
//            i++;
//            if (i % 5 == 0) {
//                components.add(textComponent);
//                textComponent = Component.text("");
//            }
        }
        components.add(textComponent);
        itemStack.lore(components);

    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event) {
        ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHandItem = event.getPlayer().getInventory().getItemInOffHand();
        if (offHandItem.getType().isAir() || mainHandItem.getType().isAir()) {
            return;
        }
        if (!offHandItem.getType().equals(Material.BOOK)) {
            return;
        }
        if (offHandItem.getEnchantments().isEmpty()) {
            return;
        }
        event.setCancelled(true);

        if (offHandItem.getAmount() > 1) {
            MessageUtils.fail(event.getPlayer(), "只能同时给一本蓝图填充材料。");
            return;
        }
        byte[] bytes = NMSTagUtils.getAsBytes(offHandItem, "BluePrint");
        BluePrintData bluePrintData = (BluePrintData) ObjectUtils.deserialize(bytes);
        if (bluePrintData == null) {
            MessageUtils.fail(event.getPlayer(), "蓝图解析失败，无法提供材料。");
            return;
        }
        Map<Material, Integer> materialMap = bluePrintData.getMaterialMap();
        Material mainHandItemType = mainHandItem.getType();
        if (mainHandItemType.equals(Material.BEACON)) {
            MessageUtils.fail(event.getPlayer(), "信标不能填充。");
            return;
        }
        if (mainHandItemType.name().toLowerCase(Locale.ROOT).contains("shulker_box")) {
            BlockStateMeta bsm = (BlockStateMeta) mainHandItem.getItemMeta();
            ShulkerBox shulkerBox = (ShulkerBox) bsm.getBlockState();
            for (int i = 0; i < shulkerBox.getInventory().getContents().length; i++) {
                ItemStack item = shulkerBox.getInventory().getItem(i);
                if (item == null) {
                    continue;
                }
                Material materialInInventory = item.getType();
                if (materialInInventory.equals(Material.BEACON)) {
                    continue;
                }
                String materialName = LangUtils.get(materialInInventory.translationKey());
                int require = 0;
                Material targetMaterial = null;
                for (Material materialRequired : materialMap.keySet()) {
                    if (materialName.equalsIgnoreCase(LangUtils.get(materialRequired.translationKey())) ||
                            materialInInventory.equals(remapping.get(materialRequired))) {
//                        require = materialMap.getOrDefault(materialRequired, 0) - bluePrintTemplate.getStore(materialRequired);
                        if (require > 0) {
                            targetMaterial = materialInInventory;
                            break;
                        }
                    }
                }
                if (require > 0) {
                    if (item.getAmount() > require) {
                        item.setAmount(item.getAmount() - require);
                        shulkerBox.getInventory().setItem(i, item);
//                        bluePrintTemplate.storeMaterial(targetMaterial, require);
                    } else {
//                        bluePrintTemplate.storeMaterial(targetMaterial, item.getAmount());
                        shulkerBox.getInventory().setItem(i, null);
                    }
                }
            }
            bsm.setBlockState(shulkerBox);
            shulkerBox.update();
            mainHandItem.setItemMeta(bsm);
            event.getPlayer().getInventory().setItemInMainHand(mainHandItem);
        } else {
            String mainHandMaterialName = LangUtils.get(mainHandItemType.translationKey());
            int require = 0;
            Material targetMaterial = null;
            for (Material materialRequired : materialMap.keySet()) {
                if (mainHandMaterialName.equalsIgnoreCase(LangUtils.get(materialRequired.translationKey())) ||
                        mainHandItemType.equals(remapping.get(materialRequired))) {

//                    require = materialMap.getOrDefault(materialRequired, 0) - bluePrintTemplate.getStore(materialRequired);
                    if (require > 0) {
                        targetMaterial = materialRequired;
                        break;
                    }
                }
            }

            if (targetMaterial == null) {
                MessageUtils.fail(event.getPlayer(), "此蓝图不需要这个材料。");
                return;
            } else {
                if (mainHandItem.getAmount() > require) {
                    mainHandItem.setAmount(mainHandItem.getAmount() - require);
//                    bluePrintTemplate.storeMaterial(targetMaterial, require);
                    event.getPlayer().getInventory().setItemInMainHand(mainHandItem);
                } else {
//                    bluePrintTemplate.storeMaterial(targetMaterial, mainHandItem.getAmount());
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
            }
        }
        byte[] serialize = ObjectUtils.serialize(bluePrintData);
        ItemStack result = NMSTagUtils.set(offHandItem, "BluePrint", serialize);
        updateItemStack(result, bluePrintData);
        event.getPlayer().getInventory().setItemInOffHand(result);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().equals(Material.BOOK)) {
            MessageUtils.fail(player, "手上必须为一本书。");
            return true;
        }

        if (!firstLocations.containsKey(player.getUniqueId()) || !secondLocations.containsKey(player.getUniqueId())) {
            MessageUtils.fail(player, "该书必须先左右选取两点。");
            return true;
        }
        Location loc1 = firstLocations.get(player.getUniqueId());
        Location loc2 = secondLocations.get(player.getUniqueId());
        Location bot = new Location(loc1.getWorld(),
                Math.min(loc1.getBlockX(), loc2.getBlockX()),
                Math.min(loc1.getBlockY(), loc2.getBlockY()),
                Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
        Location top = new Location(loc1.getWorld(),
                Math.max(loc1.getBlockX(), loc2.getBlockX()),
                Math.max(loc1.getBlockY(), loc2.getBlockY()),
                Math.max(loc1.getBlockZ(), loc2.getBlockZ()));

        if (top.getBlockX() - bot.getBlockX() > 512) {
            MessageUtils.fail(player, "选取范围过大。");
            return true;
        }
        if (top.getBlockY() - bot.getBlockY() > 512) {
            MessageUtils.fail(player, "选取范围过大。");
            return true;
        }
        if (top.getBlockZ() - bot.getBlockZ() > 384) {
            MessageUtils.fail(player, "选取范围过大。");
            return true;
        }
        if ((top.getBlockX() - bot.getBlockX()) *
                (top.getBlockY() - bot.getBlockY()) *
                (top.getBlockZ() - bot.getBlockZ()) > 1000000) {
            MessageUtils.fail(player, "选取范围过大。");
            return true;
        }
        BluePrintData bluePrintData = new BluePrintData(bot, top);
        byte[] serialize = ObjectUtils.serialize(bluePrintData);
        ItemStack result = NMSTagUtils.set(itemInMainHand, "BluePrint", serialize);
        updateItemStack(result, bluePrintData);
        result.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        result.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        player.getInventory().setItemInMainHand(result);
        return true;
    }

}
