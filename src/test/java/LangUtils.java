import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class LangUtils {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream resourceAsStream = LangUtils.class.getClassLoader().getResourceAsStream("EntityName.properties");
        properties.load(resourceAsStream);
        Set<String> strings = properties.stringPropertyNames();

        for (EntityType value : EntityType.values()) {
            @SuppressWarnings("deprecation")
            String valueName = value.getName();
            if (valueName!=null&&!strings.contains(valueName.toLowerCase())) {
                System.out.println(value.name());
            }
        }
    }
}
