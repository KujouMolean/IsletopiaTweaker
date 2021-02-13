import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SkullTest {
    public static void main(String[] args) {
//        String list = "MHF_EGG";
//        String[] split = list.split("\n");
        for (EntityType entityType : EntityType.values()) {
            String s = "MHF_" + entityType.name();
            String id;
            try {
                {
                    URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + s);
                    InputStream inputStream = url.openStream();
                    byte[] bytes = inputStream.readAllBytes();
                    JsonParser jsonParser = new JsonParser();
                    JsonElement parse = jsonParser.parse(new String(bytes));
                    JsonObject jsonObject = parse.getAsJsonObject();
                    JsonElement idElement = jsonObject.get("id");
                    id = idElement.getAsString();
                }
                {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id);
                    InputStream inputStream = url.openStream();
                    byte[] bytes = inputStream.readAllBytes();
                    JsonParser jsonParser = new JsonParser();
                    JsonElement parse = jsonParser.parse(new String(bytes));
                    JsonObject jsonObject = parse.getAsJsonObject();
                    JsonElement properties = jsonObject.get("properties");
                    JsonArray asJsonArray = properties.getAsJsonArray();
                    JsonElement jsonElement = asJsonArray.get(0);
                    JsonObject asJsonObject = jsonElement.getAsJsonObject();
                    JsonElement value = asJsonObject.get("value");
                    String asString = value.getAsString();
                    System.out.println(entityType.name() + "=" + value);
                }
            } catch (Exception ignored) {

            }


        }
    }
}
