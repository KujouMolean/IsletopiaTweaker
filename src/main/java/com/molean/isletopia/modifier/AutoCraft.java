package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Singleton

public class AutoCraft implements Listener {
    public static final Map<Material, List<Recipe>> materials = new HashMap<>();
    public static final HashSet<Player> set = new HashSet<>();
    private final PlayerManager playerManager;
    private final PlayerPropertyManager playerPropertyManager;

    public AutoCraft(PlayerManager playerManager, PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
        this.playerManager = playerManager;
        initMaterialList();
        Tasks.INSTANCE.interval(10, () -> {
            ArrayList<Player> players = new ArrayList<>(set);
            set.clear();
            for (Player player : players) {
                Map<Material, Integer> materialIntegerMap = autoCraft(player, 0);

                for (Material material : materialIntegerMap.keySet()) {
                    MessageUtils.info(player, MessageUtils.getMessage(player, "autocraft.craft",
                            Pair.of("count", materialIntegerMap.get(material) + ""),
                            Pair.of("item", LangUtils.get(player.locale(), material.translationKey()))));
                }
            }
        });
    }

    private Map<Material, Integer> getMaterialMap(ShapelessRecipe shapelessRecipe) {
        List<ItemStack> ingredientList = shapelessRecipe.getIngredientList();
        HashMap<Material, Integer> materialIntegerHashMap = new HashMap<>();
        for (ItemStack itemStack : ingredientList) {
            if (itemStack != null) {
                Integer orDefault = materialIntegerHashMap.getOrDefault(itemStack.getType(), 0);
                materialIntegerHashMap.put(itemStack.getType(), orDefault + itemStack.getAmount());
            }
        }
        materialIntegerHashMap.remove(Material.AIR);
        return materialIntegerHashMap;
    }

    private Map<Material, Integer> getMaterialMap(ShapedRecipe shapedRecipe) {
        HashMap<Material, Integer> materialIntegerHashMap = new HashMap<>();
        Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
        @NotNull String[] shape = shapedRecipe.getShape();

        for (String s : shape) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                ItemStack itemStack = ingredientMap.get(c);
                if (itemStack != null) {
                    Integer orDefault = materialIntegerHashMap.getOrDefault(itemStack.getType(), 0);
                    materialIntegerHashMap.put(itemStack.getType(), orDefault + itemStack.getAmount());
                }

            }
        }

        materialIntegerHashMap.remove(Material.AIR);
        return materialIntegerHashMap;
    }


    public Map<Material, Integer> autoCraft(Player player, int depth) {
        HashMap<Material, Integer> count = new HashMap<>();
        List<Material> selectedMaterial = getSelectedMaterial(player);
        if (selectedMaterial.size() == 0) {
            return count;
        }
        boolean update = false;
        PlayerInventory inventory = player.getInventory();
        for (Material material : selectedMaterial) {
            List<Recipe> recipes = materials.getOrDefault(material, new ArrayList<>());
            if (recipes == null) {
                continue;
            }
            for (Recipe recipe : recipes) {
                Map<Material, Integer> materialMap = null;
                ItemStack result = null;
                if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    materialMap = getMaterialMap(shapelessRecipe);
                    result = new ItemStack(shapelessRecipe.getResult());

                }
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    materialMap = getMaterialMap(shapedRecipe);
                    result = new ItemStack(shapedRecipe.getResult());
                }
                if (recipe instanceof SmithingRecipe smithingRecipe) {
                    boolean bad = true;
                    for (ItemStack content : inventory.getContents()) {
                        if (content == null) {
                            continue;
                        }
                        if (smithingRecipe.getBase().test(content)) {
                            for (ItemStack content2 : inventory.getContents()) {
                                if (content2 == null) {
                                    continue;
                                }
                                if (smithingRecipe.getAddition().test(content2)) {
                                    materialMap = new HashMap<>();
                                    materialMap.put(content.getType(), 1);
                                    materialMap.put(content2.getType(), 1);
                                    result = new ItemStack(smithingRecipe.getResult());

                                    bad = false;
                                }
                            }
                        }
                    }
                    if (bad) {
                        continue;
                    }
                }

                if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                    materialMap = new HashMap<>();
                    materialMap.put(stonecuttingRecipe.getInput().getType(), stonecuttingRecipe.getInput().getAmount());
                    result = new ItemStack(stonecuttingRecipe.getResult());
                }
                assert materialMap != null;
                materialMatchLoop:
                while (true) {
                    for (Material material1 : materialMap.keySet()) {
                        if (!inventory.containsAtLeast(new ItemStack(material1), materialMap.get(material1))) {
                            break materialMatchLoop;
                        }
                    }
                    Integer orDefault = count.getOrDefault(result.getType(), 0);
                    count.put(result.getType(), orDefault + result.getAmount());
                    update = true;
                    for (Material material1 : materialMap.keySet()) {
                        InventoryUtils.takeItem(player, material1, materialMap.get(material1));
                    }
                    Collection<ItemStack> values = inventory.addItem(new ItemStack(result)).values();
                    for (ItemStack value : values) {
                        player.getLocation().getWorld().dropItem(player.getLocation(), value);
                    }
                }
            }
        }
        if (update && depth < 10) {
            Map<Material, Integer> materialIntegerMap = autoCraft(player, depth + 1);
            for (Material material : materialIntegerMap.keySet()) {
                Integer orDefault = count.getOrDefault(material, 0);
                count.put(material, orDefault + materialIntegerMap.get(material));
            }
        }

        return count;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerAttemptPickupItemEvent event) {
        playerManager.validate(event.getPlayer());
        set.add(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player player) {

            playerManager.validate(player);
            set.add(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player player) {
            playerManager.validate(player);
            set.add(player);
        }
    }


    private void initMaterialList() {
        Tasks.INSTANCE.timeout(1, () -> {
            Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                Recipe next = recipeIterator.next();
                Material type = next.getResult().getType();
                if (!materials.containsKey(type)) {
                    materials.put(type, new ArrayList<>());
                }
                if (next instanceof ShapelessRecipe) {
                    materials.get(type).add(next);
                }
                if (next instanceof ShapedRecipe) {
                    materials.get(type).add(next);
                }
                if (next instanceof SmithingRecipe) {
                    materials.get(type).add(next);
                }
                if (next instanceof StonecuttingRecipe) {
                    materials.get(type).add(next);
                }
            }
        });
    }

    public List<Material> getSelectedMaterial(Player player) {
        if (!playerPropertyManager.isLoad(player.getUniqueId())) {
            return new ArrayList<>();
        }
        String autoCraft = playerPropertyManager.getProperty(player, "AutoCraft");
        if (autoCraft == null) {
            autoCraft = "";
        }
        String[] split = autoCraft.split(",");

        ArrayList<Material> selected = new ArrayList<>();
        for (String s : split) {
            try {
                Material material = Material.valueOf(s);
                selected.add(material);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return selected;
    }

    public void setSelectedMaterialAsync(Player player, List<Material> materials, Runnable asyncRunnable) {
        List<String> strings = new ArrayList<>();
        for (Material material : materials) {
            strings.add(material.name());
        }
        strings = strings.stream().distinct().collect(Collectors.toList());
        playerPropertyManager.setPropertyAsync(player, "AutoCraft", String.join(",", strings), asyncRunnable);
    }


    public void addMaterial(Player player, Runnable asyncRunnable, Material... materials) {

        List<Material> selectedMaterial = getSelectedMaterial(player);
        selectedMaterial.addAll(Arrays.asList(materials));
        setSelectedMaterialAsync(player, selectedMaterial, asyncRunnable);

    }

    public void removeMaterial(Player player, Material material, Runnable asyncRunnable) {
        List<Material> selectedMaterial = getSelectedMaterial(player);
        selectedMaterial.remove(material);
        setSelectedMaterialAsync(player, selectedMaterial, asyncRunnable);
    }
}
