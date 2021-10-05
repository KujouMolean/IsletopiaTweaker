package com.molean.isletopia.admin.individual;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class CustomMapRenderer extends MapRenderer {

    private final String colors;

    public CustomMapRenderer(String colors) {
        this.colors = colors;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        for (int i = 0; i < 128 * 128; i++) {
            int x = i % 128;
            int y = i / 128;
            byte color = (byte) Integer.parseInt(colors.substring(i * 2, i * 2 + 2), 16);
            canvas.setPixel(x, y, color);
        }
    }
}
