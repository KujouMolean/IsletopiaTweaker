package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class IntTag extends NumberTag<Integer> implements Comparable<net.querz.nbt.tag.IntTag> {

	public static final byte ID = 3;
	public static final int ZERO_VALUE = 0;

	public IntTag() {
		super(ZERO_VALUE);
	}

	public IntTag(int value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(int value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asInt() == ((net.querz.nbt.tag.IntTag) other).asInt();
	}

	@Override
	public int compareTo(net.querz.nbt.tag.IntTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.IntTag clone() {
		return new net.querz.nbt.tag.IntTag(getValue());
	}
}
