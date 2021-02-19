package net.querz.nbt.tag;

import net.querz.io.MaxDepthIO;

import java.util.*;
import java.util.function.Consumer;

/**
 * ListTag represents a typed List in the nbt structure.
 * An empty {@link net.querz.nbt.tag.ListTag} will be of type {@link EndTag} (unknown type).
 * The type of an empty untyped {@link net.querz.nbt.tag.ListTag} can be set by using any of the {@code add()}
 * methods or any of the {@code as...List()} methods.
 * */
public class ListTag<T extends Tag<?>> extends Tag<List<T>> implements Iterable<T>, Comparable<net.querz.nbt.tag.ListTag<T>>, MaxDepthIO {

	public static final byte ID = 9;

	private Class<?> typeClass = null;

	private ListTag() {
		super(createEmptyValue(3));
	}

	@Override
	public byte getID() {
		return ID;
	}

	/**
	 * <p>Creates a non-type-safe ListTag. Its element type will be set after the first
	 * element was added.</p>
	 *
	 * <p>This is an internal helper method for cases where the element type is not known
	 * at construction time. Use {@link #ListTag(Class)} when the type is known.</p>
	 *
	 * @return A new non-type-safe ListTag
	 */
	public static net.querz.nbt.tag.ListTag<?> createUnchecked(Class<?> typeClass) {
		net.querz.nbt.tag.ListTag<?> list = new net.querz.nbt.tag.ListTag<>();
		list.typeClass = typeClass;
		return list;
	}

	/**
	 * <p>Creates an empty mutable list to be used as empty value of ListTags.</p>
	 *
	 * @param <T> Type of the list elements
	 * @param initialCapacity The initial capacity of the returned List
	 * @return An instance of {@link List} with an initial capacity of 3
	 * */
	private static <T> List<T> createEmptyValue(int initialCapacity) {
		return new ArrayList<>(initialCapacity);
	}

	/**
	 * @param typeClass The exact class of the elements
	 * @throws IllegalArgumentException When {@code typeClass} is {@link EndTag}{@code .class}
	 * @throws NullPointerException When {@code typeClass} is {@code null}
	 */
	public ListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
		super(createEmptyValue(3));
		if (typeClass == EndTag.class) {
			throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
		}
		this.typeClass = Objects.requireNonNull(typeClass);
	}

	public Class<?> getTypeClass() {
		return typeClass == null ? EndTag.class : typeClass;
	}

	public int size() {
		return getValue().size();
	}

	public T remove(int index) {
		return getValue().remove(index);
	}

	public void clear() {
		getValue().clear();
	}

	public boolean contains(T t) {
		return getValue().contains(t);
	}

	public boolean containsAll(Collection<Tag<?>> tags) {
		return getValue().containsAll(tags);
	}

	public void sort(Comparator<T> comparator) {
		getValue().sort(comparator);
	}

	@Override
	public Iterator<T> iterator() {
		return getValue().iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		getValue().forEach(action);
	}

	public T set(int index, T t) {
		return getValue().set(index, Objects.requireNonNull(t));
	}

	/**
	 * Adds a Tag to this ListTag after the last index.
	 * @param t The element to be added.
	 * */
	public void add(T t) {
		add(size(), t);
	}

	public void add(int index, T t) {
		Objects.requireNonNull(t);
		if (getTypeClass() == EndTag.class) {
			typeClass = t.getClass();
		} else if (typeClass != t.getClass()) {
			throw new ClassCastException(
					String.format("cannot add %s to ListTag<%s>",
							t.getClass().getSimpleName(),
							typeClass.getSimpleName()));
		}
		getValue().add(index, t);
	}

	public void addAll(Collection<T> t) {
		for (T tt : t) {
			add(tt);
		}
	}

	public void addAll(int index, Collection<T> t) {
		int i = 0;
		for (T tt : t) {
			add(index + i, tt);
			i++;
		}
	}

	public void addBoolean(boolean value) {
		addUnchecked(new ByteTag(value));
	}

	public void addByte(byte value) {
		addUnchecked(new ByteTag(value));
	}

	public void addShort(short value) {
		addUnchecked(new ShortTag(value));
	}

	public void addInt(int value) {
		addUnchecked(new IntTag(value));
	}

	public void addLong(long value) {
		addUnchecked(new LongTag(value));
	}

	public void addFloat(float value) {
		addUnchecked(new FloatTag(value));
	}

	public void addDouble(double value) {
		addUnchecked(new DoubleTag(value));
	}

	public void addString(String value) {
		addUnchecked(new StringTag(value));
	}

	public void addByteArray(byte[] value) {
		addUnchecked(new ByteArrayTag(value));
	}

	public void addIntArray(int[] value) {
		addUnchecked(new IntArrayTag(value));
	}

	public void addLongArray(long[] value) {
		addUnchecked(new LongArrayTag(value));
	}

	public T get(int index) {
		return getValue().get(index);
	}

	public int indexOf(T t) {
		return getValue().indexOf(t);
	}

	@SuppressWarnings("unchecked")
	public <L extends Tag<?>> net.querz.nbt.tag.ListTag<L> asTypedList(Class<L> type) {
		checkTypeClass(type);
		return (net.querz.nbt.tag.ListTag<L>) this;
	}

	public net.querz.nbt.tag.ListTag<ByteTag> asByteTagList() {
		return asTypedList(ByteTag.class);
	}

	public net.querz.nbt.tag.ListTag<ShortTag> asShortTagList() {
		return asTypedList(ShortTag.class);
	}

	public net.querz.nbt.tag.ListTag<IntTag> asIntTagList() {
		return asTypedList(IntTag.class);
	}

	public net.querz.nbt.tag.ListTag<LongTag> asLongTagList() {
		return asTypedList(LongTag.class);
	}

	public net.querz.nbt.tag.ListTag<FloatTag> asFloatTagList() {
		return asTypedList(FloatTag.class);
	}

	public net.querz.nbt.tag.ListTag<DoubleTag> asDoubleTagList() {
		return asTypedList(DoubleTag.class);
	}

	public net.querz.nbt.tag.ListTag<StringTag> asStringTagList() {
		return asTypedList(StringTag.class);
	}

	public net.querz.nbt.tag.ListTag<ByteArrayTag> asByteArrayTagList() {
		return asTypedList(ByteArrayTag.class);
	}

	public net.querz.nbt.tag.ListTag<IntArrayTag> asIntArrayTagList() {
		return asTypedList(IntArrayTag.class);
	}

	public net.querz.nbt.tag.ListTag<LongArrayTag> asLongArrayTagList() {
		return asTypedList(LongArrayTag.class);
	}

	@SuppressWarnings("unchecked")
	public net.querz.nbt.tag.ListTag<net.querz.nbt.tag.ListTag<?>> asListTagList() {
		checkTypeClass(net.querz.nbt.tag.ListTag.class);
		typeClass = net.querz.nbt.tag.ListTag.class;
		return (net.querz.nbt.tag.ListTag<net.querz.nbt.tag.ListTag<?>>) this;
	}

	public net.querz.nbt.tag.ListTag<CompoundTag> asCompoundTagList() {
		return asTypedList(CompoundTag.class);
	}

	@Override
	public String valueToString(int maxDepth) {
		StringBuilder sb = new StringBuilder("{\"type\":\"").append(getTypeClass().getSimpleName()).append("\",\"list\":[");
		for (int i = 0; i < size(); i++) {
			sb.append(i > 0 ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
		}
		sb.append("]}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other) || size() != ((net.querz.nbt.tag.ListTag<?>) other).size() || getTypeClass() != ((net.querz.nbt.tag.ListTag<?>) other).getTypeClass()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(((net.querz.nbt.tag.ListTag<?>) other).get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTypeClass().hashCode(), getValue().hashCode());
	}

	@Override
	public int compareTo(net.querz.nbt.tag.ListTag<T> o) {
		return Integer.compare(size(), o.getValue().size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public net.querz.nbt.tag.ListTag<T> clone() {
		net.querz.nbt.tag.ListTag<T> copy = new net.querz.nbt.tag.ListTag<>();
		// assure type safety for clone
		copy.typeClass = typeClass;
		for (T t : getValue()) {
			copy.add((T) t.clone());
		}
		return copy;
	}

	//TODO: make private
	@SuppressWarnings("unchecked")
	public void addUnchecked(Tag<?> tag) {
		if (getTypeClass() != EndTag.class && typeClass != tag.getClass()) {
			throw new IllegalArgumentException(String.format(
					"cannot add %s to ListTag<%s>",
					tag.getClass().getSimpleName(), typeClass.getSimpleName()));
		}
		add(size(), (T) tag);
	}

	private void checkTypeClass(Class<?> clazz) {
		if (getTypeClass() != EndTag.class && typeClass != clazz) {
			throw new ClassCastException(String.format(
					"cannot cast ListTag<%s> to ListTag<%s>",
					typeClass.getSimpleName(), clazz.getSimpleName()));
		}
	}
}
