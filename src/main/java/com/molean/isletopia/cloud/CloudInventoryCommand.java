package com.molean.isletopia.cloud;

import com.molean.isletopia.infrastructure.assist.MultiCommand;
import com.molean.isletopia.infrastructure.assist.SimpleCommand;
import com.molean.isletopia.infrastructure.assist.SubCommand;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.CloudInventoryDao;
import com.molean.isletopia.shared.model.CloudInventorySlot;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CloudInventoryCommand extends MultiCommand {
    private static final ArrayList<String> materials = new ArrayList<>();

    static {

        for (Material value : Material.values()) {
            if (!value.isLegacy()) {
                materials.add(value.name());
            }
        }
    }
    private  static boolean isOpenedSuchMaterial(Player player, Material material) {
        try {
            for (CloudInventorySlot cloudInventorySlot : CloudInventoryDao.getInventorySlotsSnapshot(player.getUniqueId())) {
                if (cloudInventorySlot.getMaterial().equalsIgnoreCase(material.name())) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public CloudInventoryCommand() {
        super("CloudInventory");
        this.createSubCommand(new SubCommand("EnableAutoGet"))
                .completer((player, args) -> materials)
                .consumer((player, strings) -> {
                    if (strings.size() < 1) {
                        MessageUtils.fail(player, "FAILED");
                        return;
                    }
                    for (String string : strings) {
                        Material material = Material.getMaterial(string);

                        if (material != null) {
                            if (!CloudInventoryDao.containsSlot(player.getUniqueId(), material.name())) {
                                MessageUtils.fail(player, "FAILED");
                                continue;
                            }
                            String name = material.name();
                            if (!PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoGet").contains(name)) {
                                PlayerPropertyManager.INSTANCE.addStringListPropertyEntryAsync(player, "AutoGet", name);
                                MessageUtils.success(player, "OK");
                            } else {
                                MessageUtils.fail(player, "FAILED");
                            }
                        } else {
                            MessageUtils.fail(player, "FAILED");
                        }
                    }
                });

        this.createSubCommand(new SubCommand("DisableAutoGet"))
                .completer((player, args) -> PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoGet"))
                .consumer((player, strings) -> {
                    if (strings.size() < 1) {
                        MessageUtils.fail(player, "FAILED");
                        return;
                    }
                    for (String string : strings) {
                        Material material = Material.getMaterial(string);
                        if (material != null) {
                            String name = material.name();
                            if (PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoGet").contains(name)) {
                                PlayerPropertyManager.INSTANCE.removeStringListPropertyEntryAsync(player, "AutoGet", name);
                                MessageUtils.success(player, "OK");
                            } else {
                                MessageUtils.fail(player, "FAILED");
                            }
                        } else {
                            MessageUtils.fail(player, "FAILED");
                        }
                    }

                });


        this.createSubCommand(new SubCommand("EnableAutoPut"))
                .completer((player, args) -> materials)
                .consumer((player, strings) -> {
                    if (strings.size() < 1) {
                        MessageUtils.fail(player, "FAILED");
                        return;
                    }
                    for (String string : strings) {
                        Material material = Material.getMaterial(string);
                        if (material != null) {
                            if (!CloudInventoryDao.containsSlot(player.getUniqueId(), material.name())) {
                                MessageUtils.fail(player, "FAILED");
                                continue;
                            }
                            String name = material.name();
                            if (!PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoPut").contains(name)) {
                                PlayerPropertyManager.INSTANCE.addStringListPropertyEntryAsync(player, "AutoPut", name);
                                MessageUtils.success(player, "OK");
                            } else {
                                MessageUtils.fail(player, "FAILED");
                            }
                        } else {
                            MessageUtils.fail(player, "FAILED");
                        }
                    }

                });

        this.createSubCommand(new SubCommand("DisableAutoPut"))
                .completer((player, args) -> PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoPut"))
                .consumer((player, strings) -> {
                    if (strings.size() < 1) {
                        MessageUtils.fail(player, "FAILED");
                        return;
                    }
                    for (String string : strings) {
                        Material material = Material.getMaterial(string);
                        if (material != null) {
                            if (!isOpenedSuchMaterial(player, material)) {
                                MessageUtils.fail(player, "FAILED");
                                continue;

                            }
                            String name = material.name();
                            if (PlayerPropertyManager.INSTANCE.getPropertyAsStringList(player, "AutoPut").contains(name)) {
                                PlayerPropertyManager.INSTANCE.removeStringListPropertyEntryAsync(player, "AutoPut", name);
                                MessageUtils.success(player, "OK");
                            } else {
                                MessageUtils.fail(player, "FAILED");
                            }
                        } else {
                            MessageUtils.fail(player, "FAILED");
                        }
                    }

                });

        this.createSubCommand(new SubCommand("Get")).completer((player, args) -> {
            if (args.size() != 1) {
                return new ArrayList<>();
            }

            ArrayList<String> strings = new ArrayList<>();
            List<CloudInventorySlot> inventorySlotsSnapshot = null;
            try {
                inventorySlotsSnapshot = CloudInventoryDao.getInventorySlotsSnapshot(player.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            assert inventorySlotsSnapshot != null;
            for (CloudInventorySlot cloudInventorySlot : inventorySlotsSnapshot) {
                strings.add(cloudInventorySlot.getMaterial());
            }
            return strings;
        }).consumer((player, strings) -> {

            if (strings.size() < 2) {
                MessageUtils.fail(player, "/ci get material amount");
                return;
            }
            Material material = null;
            int amount = 0;
            try {
                material = Material.getMaterial(strings.get(0).toUpperCase(Locale.ROOT));
                assert material != null;
                amount = Integer.parseInt(strings.get(1));
            } catch (Exception e) {
                MessageUtils.fail(player, "FAILED");
                return;
            }

            if (amount > 27 * material.getMaxStackSize()) {
                MessageUtils.fail(player, "TOO LARGE NUMBER!");
                return;
            }

            boolean consume = CloudInventoryDao.consume(player.getUniqueId(), material.name(), amount);
            if (consume) {
                BukkitPlayerUtils.giveItem(player, material, amount);
            } else {
                MessageUtils.fail(player, "FAILED");
            }
        });
        this.createSubCommand(new SubCommand("Put")).completer((player, args) -> {
            ArrayList<String> strings = new ArrayList<>();
            List<CloudInventorySlot> inventorySlotsSnapshot = null;
            try {
                inventorySlotsSnapshot = CloudInventoryDao.getInventorySlotsSnapshot(player.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            assert inventorySlotsSnapshot != null;
            for (CloudInventorySlot cloudInventorySlot : inventorySlotsSnapshot) {
                strings.add(cloudInventorySlot.getMaterial().toUpperCase(Locale.ROOT));
            }
            return strings;
        }).consumer((player, strings) -> {
            if (strings.size() < 2) {
                MessageUtils.fail(player, "/ci put material amount");
                return;
            }

            Material material = null;
            int amount = 0;
            try {
                material = Material.getMaterial(strings.get(0).toUpperCase(Locale.ROOT));
                assert material != null;
                amount = Integer.parseInt(strings.get(1));
            } catch (Exception e) {
                MessageUtils.fail(player, "FAILED");
                return;
            }


            if (!CloudInventoryDao.containsSlot(player.getUniqueId(), material.name())) {
                MessageUtils.fail(player, "FAILED");
                return;
            }


            if (InventoryUtils.takeItem(player, material, amount)) {
                if (CloudInventoryDao.produce(player.getUniqueId(), material.name(), amount)) {
                    MessageUtils.success(player, "SUCCESS");
                } else {
                    MessageUtils.fail(player, "Unexpected severe error, contact admin!");
                }
            }
        });
        this.createSubCommand(new SubCommand("create"))
                .completer((player, strings) -> {
                    if (strings.size() == 1) {
                        return materials;
                    }else{
                        return new ArrayList<>();

                    }
                })
                .consumer((player, strings) -> {
                    Material material = null;
                    try {
                        material = Material.getMaterial(strings.get(0).toUpperCase(Locale.ROOT));
                        assert material != null;
                    } catch (Exception e) {
                        MessageUtils.fail(player, "/ci create material");
                        return;
                    }

                    try {
                        if (CloudInventoryDao.getInventorySlotsSnapshot(player.getUniqueId()).size() >= 9) {
                            MessageUtils.fail(player, "TOO MANY SLOT");
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        CloudInventoryDao.create(player.getUniqueId(), material.name());
                        MessageUtils.success(player, "OK");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        this.createSubCommand(new SimpleCommand("GUI"))
                .simpleConsumer(player -> new CloudInventoryList(player).open());
    }
}
