package com.molean.isletopia.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.io.IOException;
import java.io.InputStream;

public class SchematicUtils {
    public static void paste(Location location, Clipboard clipboard) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BukkitAdapter.adapt(location).toVector().toBlockPoint())
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }


    public static Clipboard load(String filename) {
        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormats.findByAlias("schematic");
        InputStream resourceAsStream = SchematicUtils.class.getClassLoader().getResourceAsStream(filename);
        try {
            assert resourceAsStream != null;
            assert format != null;
            try (ClipboardReader reader = format.getReader(resourceAsStream)) {
                clipboard = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clipboard;
    }

    public static void pasteCenter(Location location, Clipboard clipboard) {
        int width = clipboard.getRegion().getWidth();
        int length = clipboard.getRegion().getLength();
        int height = clipboard.getRegion().getHeight();
        Location add = location.clone().add(-width / 2.0, -length / 2.0, -height / 2.0);
        paste(add, clipboard);
    }

}
