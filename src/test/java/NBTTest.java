import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;

public class NBTTest {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = NBTTest.class.getClassLoader().getResourceAsStream("player.nbt");

        NBTDeserializer nbtDeserializer = new NBTDeserializer();
        NamedTag namedTag = nbtDeserializer.fromStream(inputStream);

        Element test = DocumentHelper.createElement("test");
        System.out.println(namedTag.getTag());
        NBTXml.toXml(namedTag, test);
        System.out.println(NBTXml.fromXml(test));
    }


}
