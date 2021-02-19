package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class DoubleTag extends NumberTag<Double> implements Comparable<net.querz.nbt.tag.DoubleTag> {

	public static final byte ID = 6;
	public static final double ZERO_VALUE = 0.0D;

	public DoubleTag() {
		super(ZERO_VALUE);
	}

	public DoubleTag(double value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(double value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((net.querz.nbt.tag.DoubleTag) other).getValue());
	}

	@Override
	public int compareTo(net.querz.nbt.tag.DoubleTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.DoubleTag clone() {
		return new net.querz.nbt.tag.DoubleTag(getValue());
	}
}
