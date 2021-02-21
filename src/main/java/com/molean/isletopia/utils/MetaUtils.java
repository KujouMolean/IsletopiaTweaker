package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.List;
import java.util.Objects;

public class MetaUtils {

    public static void setMeta(Metadatable metadatable, String key, Object value) {
        metadatable.setMetadata(key, new FixedMetadataValue(IsletopiaTweakers.getPlugin(), value));
    }

    public static boolean hasMeta(Metadatable metadatable, String key) {
        List<MetadataValue> metadata = metadatable.getMetadata(key);
        for (MetadataValue metadataValue : metadata) {
            return Objects.equals(metadataValue.getOwningPlugin(), IsletopiaTweakers.getPlugin());
        }
        return false;
    }

    public static Object getMeta(Metadatable metadatable, String key) {
        List<MetadataValue> metadata = metadatable.getMetadata(key);
        for (MetadataValue metadataValue : metadata) {
            if (Objects.equals(metadataValue.getOwningPlugin(), IsletopiaTweakers.getPlugin())) {
                return metadataValue.value();
            }
        }
        return null;
    }

    public static Boolean getBoolean(Metadatable metadatable, String key) {
        Object meta = getMeta(metadatable, key);
        return meta == null ? null : (Boolean) meta;
    }

    public static String getString(Metadatable metadatable, String key) {
        Object meta = getMeta(metadatable, key);
        return meta == null ? null : (String) meta;
    }
}
