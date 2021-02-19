import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.dom4j.Element;

import java.util.List;

public class NBTXml {


    public static Tag<?> fromXml(Element element) {
        switch (element.getName()) {
            case "Float":
                return new FloatTag(Float.parseFloat(element.getText()));
            case "Compound": {
                CompoundTag tag = new CompoundTag();
                List<Element> elements = element.elements();
                elements.forEach(childElement -> {
                    tag.put(childElement.getName(), fromXml(childElement));
                });
                return tag;
            }

            case "Int":
                return new IntTag(Integer.parseInt(element.getText()));
            case "Double":
                return new DoubleTag(Double.parseDouble(element.getText()));
            case "String":
                return new StringTag(element.getText());
            case "Byte":
                return new ByteTag(Byte.parseByte(element.getText()));
            case "List": {
                ListTag<?> listTag = ListTag.createUnchecked(null);
                List<Element> elements = element.elements();
                elements.forEach(childElement -> {
                    Tag<?> tag = fromXml(childElement);
                    listTag.addUnchecked(tag);
                });
                return listTag;
            }
            case "ByteArray": {
                List<Element> elements = element.elements();
                byte[] bytes = new byte[elements.size()];
                for (int i = 0; i < elements.size(); i++) {
                    bytes[i] = Byte.parseByte(elements.get(i).getText());
                }
                return new ByteArrayTag(bytes);
            }
            case "Short":
                return new ShortTag(Short.parseShort(element.getText()));
            case "Long":
                return new LongTag(Long.parseLong(element.getText()));
            case "IntArray": {
                List<Element> elements = element.elements();
                int[] ints = new int[elements.size()];
                for (int i = 0; i < elements.size(); i++) {
                    ints[i] = Integer.parseInt(elements.get(i).getText());
                }
                return new IntArrayTag(ints);
            }
            case "LongArray": {
                List<Element> elements = element.elements();
                long[] longs = new long[elements.size()];
                for (int i = 0; i < elements.size(); i++) {
                    longs[i] = Long.parseLong(elements.get(i).getText());
                }
                return new LongArrayTag(longs);
            }
        }
        return EndTag.INSTANCE;
    }

    public static void toXml(NamedTag namedTag, Element element) {
        element.addAttribute("key", namedTag.getName());
        toXml(namedTag.getTag(), element);
    }

    public static void toXml(Tag<?> tag, Element element) {
        if (tag instanceof FloatTag) {
            element.setName("Float");
            element.setText(Float.toString(((FloatTag) tag).asFloat()));
        }
        if (tag instanceof CompoundTag) {
            element.setName("Compound");
            ((CompoundTag) tag).forEach((key, keyTag) -> {
                NamedTag namedTag = new NamedTag(key, keyTag);
                toXml(namedTag, element.addElement("Tag"));
            });

        }
        if (tag instanceof IntTag) {
            element.setName("Int");
            element.setText(Integer.toString(((IntTag) tag).asInt()));
        }
        if (tag instanceof DoubleTag) {
            element.setName("Double");
            element.setText(Double.toString(((DoubleTag) tag).asDouble()));
        }
        if (tag instanceof StringTag) {
            element.setName("String");
            element.setText(((StringTag) tag).getValue());
        }
        if (tag instanceof ByteTag) {
            element.setName("Byte");
            element.setText(Byte.toString(((ByteTag) tag).asByte()));
        }
        if (tag instanceof ListTag) {
            element.setName("List");
            ((ListTag<?>) tag).forEach(aTag -> {
                Class<? extends Tag> aClass = aTag.getClass();
                toXml(aTag, element.addElement(aClass.getSimpleName()));
            });
        }
        if (tag instanceof ByteArrayTag) {
            element.setName("ByteArray");
            byte[] bytes = ((ByteArrayTag) tag).getValue();
            for (byte aByte : bytes) {
                Element byteElement = element.addElement("Byte");
                byteElement.setText(Byte.toString(aByte));
            }
        }

        if (tag instanceof ShortTag) {
            element.setName("Short");
            element.setText(Short.toString(((ShortTag) tag).asShort()));
        }
        if (tag instanceof LongTag) {
            element.setName("Long");
            element.setText(Long.toString(((LongTag) tag).asLong()));
        }
        if (tag instanceof IntArrayTag) {
            element.setName("IntArray");
            int[] ints = ((IntArrayTag) tag).getValue();
            for (int aInt : ints) {
                Element byteElement = element.addElement("Int");
                byteElement.setText(Integer.toString(aInt));
            }
        }
        if (tag instanceof LongArrayTag) {
            element.setName("LongArray");
            long[] longs = ((LongArrayTag) tag).getValue();
            for (long aLong : longs) {
                Element byteElement = element.addElement("Long");
                byteElement.setText(Long.toString(aLong));
            }
        }

    }
}
