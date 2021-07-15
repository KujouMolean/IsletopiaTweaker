import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class Test {
    public static void main(String[] args) throws IOException {
        InputStream resourceAsStream = Test.class.getClassLoader().getResourceAsStream("biome.properties");

        Properties properties = new Properties();
        properties.load(resourceAsStream);
        Biome[] values = Biome.values();
        for (Biome value : values) {
            String name = properties.getProperty(value.name() + ".name");
            String icon = properties.getProperty(value.name() + ".icon");
            String creatures = properties.getProperty(value.name() + ".creatures");
            String environment = properties.getProperty(value.name() + ".environment");

            if(name==null){
                name = "未知";
            }
            if(icon==null){
                icon = "PLAYER_HEAD";
            }


            if(creatures==null){
                creatures = "";
            }
            String[] split1 = creatures.split(",");
            for (int i = 0; i < split1.length; i++) {
                split1[i] = "\"" + split1[i] + "\"";
            }
            creatures = String.join(", ", split1);


            if(environment==null){
                environment = "";
            }
            String[] split2 = environment.split(",");
            for (int i = 0; i < split2.length; i++) {
                split2[i] = "\"" + split2[i] + "\"";
            }
            environment = String.join(", ", split2);


            String out = "{biome}(\"{name}\", Material.{icon}, List.of({creatures}), List.of({environment}))";
            out = out.replaceAll("\\{biome}", value.name());
            out = out.replaceAll("\\{name}", name);
            out = out.replaceAll("\\{icon}", icon);
            out = out.replaceAll("\\{creatures}", creatures);
            out = out.replaceAll("\\{environment}", environment);

            System.out.println(out+",");
        }

    }
}
