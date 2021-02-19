package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class FloatTag extends NumberTag<Float> implements Comparable<net.querz.nbt.tag.FloatTag> {

	public static final byte ID = 5;
	public static final float ZERO_VALUE = 0.0F;

	public FloatTag() {
		super(ZERO_VALUE);
	}

	public FloatTag(float value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(float value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((net.querz.nbt.tag.FloatTag) other).getValue());
	}

	@Override
	public int compareTo(net.querz.nbt.tag.FloatTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.FloatTag clone() {
		return new net.querz.nbt.tag.FloatTag(getValue());
	}
}
