package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class ByteTag extends NumberTag<Byte> implements Comparable<net.querz.nbt.tag.ByteTag> {

	public static final byte ID = 1;
	public static final byte ZERO_VALUE = 0;

	public ByteTag() {
		super(ZERO_VALUE);
	}

	public ByteTag(byte value) {
		super(value);
	}

	public ByteTag(boolean value) {
		super((byte) (value ? 1 : 0));
	}

	@Override
	public byte getID() {
		return ID;
	}

	public boolean asBoolean() {
		return getValue() > 0;
	}

	public void setValue(byte value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asByte() == ((net.querz.nbt.tag.ByteTag) other).asByte();
	}

	@Override
	public int compareTo(net.querz.nbt.tag.ByteTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.ByteTag clone() {
		return new net.querz.nbt.tag.ByteTag(getValue());
	}
}
