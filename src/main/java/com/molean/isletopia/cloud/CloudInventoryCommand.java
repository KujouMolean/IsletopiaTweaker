package com.molean.isletopia.cloud;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Singleton
@CommandAlias("CloudInventory|ci")
public class CloudInventoryCommand extends BaseCommand {
    private final PlayerPropertyManager playerPropertyManager;
    private final CloudInventoryService cloudInventoryService;

    public CloudInventoryCommand(PlayerPropertyManager playerPropertyManager, CloudInventoryService cloudInventoryService) {
        this.cloudInventoryService = cloudInventoryService;
        this.playerPropertyManager = playerPropertyManager;
    }


    @Subcommand("EnableAutoGet")
    public void enableAutoGet(Player player, Material material) {
        cloudInventoryService.containsSlot(player, material, contains -> {
            if (Boolean.FALSE.equals(contains)) {
                MessageUtils.fail(player, "FAILED");
                return;
            }
            String name = material.name();
            if (!playerPropertyManager.getPropertyAsStringList(player, "AutoGet").contains(name)) {
                playerPropertyManager.addStringListPropertyEntryAsync(player, "AutoGet", name);
                MessageUtils.success(player, "OK");
            } else {
                MessageUtils.fail(player, "FAILED");
            }
        });

    }

    @Subcommand("DisableAutoGet")
    public void disableAutoGet(Player player, Material material) {
        String name = material.name();
        if (playerPropertyManager.getPropertyAsStringList(player, "AutoGet").contains(name)) {
            playerPropertyManager.removeStringListPropertyEntryAsync(player, "AutoGet", name);
            MessageUtils.success(player, "OK");
        } else {
            MessageUtils.fail(player, "FAILED");
        }

    }

    @Subcommand("EnableAutoPut")
    public void enableAutoPut(Player player, Material material) {
        cloudInventoryService.containsSlot(player, material, contains -> {
            if (Boolean.FALSE.equals(contains)) {
                MessageUtils.fail(player, "FAILED");
                return;
            }
            String name = material.name();
            if (!playerPropertyManager.getPropertyAsStringList(player, "AutoPut").contains(name)) {
                playerPropertyManager.addStringListPropertyEntryAsync(player, "AutoPut", name);
                MessageUtils.success(player, "OK");
            } else {
                MessageUtils.fail(player, "FAILED");
            }
        });

    }

    @Subcommand("DisableAutoPut")
    public void disableAutoPut(Player player, Material material) {
        cloudInventoryService.containsSlot(player, material, contains -> {
            if (Boolean.FALSE.equals(contains)) {
                MessageUtils.fail(player, "FAILED");
                return;
            }
            String name = material.name();
            if (playerPropertyManager.getPropertyAsStringList(player, "AutoPut").contains(name)) {
                playerPropertyManager.removeStringListPropertyEntryAsync(player, "AutoPut", name);
                MessageUtils.success(player, "OK");
            } else {
                MessageUtils.fail(player, "FAILED");
            }
        });
    }

    @Subcommand("Create")
    public void create(Player player, Material material) {
        Set<Material> list = cloudInventoryService.list(player);
        if (list.size() >= 9) {
            MessageUtils.fail(player, "TOO MANY SLOT");
            return;
        }
        cloudInventoryService.createSlot(player, material, exception -> {
            if (exception == null) {
                MessageUtils.success(player, "OK");
            } else {
                MessageUtils.success(player, "FAILED");
            }
        });

    }

    @Subcommand("Remove")
    public void remove(Player player, Material material) {

        cloudInventoryService.removeSlot(player, material, exception -> {
            if (exception == null) {
                MessageUtils.success(player, "OK");
            } else {
                MessageUtils.success(player, "FAILED");
            }
        });


    }

    @Subcommand("Get")
    public void get(Player player, Material material, int amount) {
        if (amount > 27 * material.getMaxStackSize()) {
            MessageUtils.fail(player, "TOO LARGE NUMBER!");
            return;
        }

        cloudInventoryService.consume(player, material, amount, consume -> {
            if (consume) {
                BukkitPlayerUtils.giveItem(player, material, amount);
            } else {
                MessageUtils.fail(player, "FAILED");
            }
        });
    }

    @Subcommand("Put")
    public void put(Player player, Material material, int amount) {
        cloudInventoryService.containsSlot(player, material, contains -> {
            if (Boolean.FALSE.equals(contains)) {
                MessageUtils.fail(player, "FAILED");
                return;
            }
            if (InventoryUtils.takeItem(player, material, amount)) {
                cloudInventoryService.produce(player, material, amount, produce -> {
                    if (produce) {
                        MessageUtils.success(player, "SUCCESS");
                    } else {
                        MessageUtils.fail(player, "Unexpected severe error, contact admin!");
                    }
                });
            }
        });
    }
}
