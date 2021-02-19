package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class ShortTag extends NumberTag<Short> implements Comparable<net.querz.nbt.tag.ShortTag> {

	public static final byte ID = 2;
	public static final short ZERO_VALUE = 0;

	public ShortTag() {
		super(ZERO_VALUE);
	}

	public ShortTag(short value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(short value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asShort() == ((net.querz.nbt.tag.ShortTag) other).asShort();
	}

	@Override
	public int compareTo(net.querz.nbt.tag.ShortTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.ShortTag clone() {
		return new net.querz.nbt.tag.ShortTag(getValue());
	}
}
