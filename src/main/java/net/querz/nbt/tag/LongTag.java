package net.querz.nbt.tag;

import net.querz.nbt.tag.NumberTag;

public class LongTag extends NumberTag<Long> implements Comparable<net.querz.nbt.tag.LongTag> {

	public static final byte ID = 4;
	public static final long ZERO_VALUE = 0L;

	public LongTag() {
		super(ZERO_VALUE);
	}

	public LongTag(long value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	public void setValue(long value) {
		super.setValue(value);
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asLong() == ((net.querz.nbt.tag.LongTag) other).asLong();
	}

	@Override
	public int compareTo(net.querz.nbt.tag.LongTag other) {
		return getValue().compareTo(other.getValue());
	}

	@Override
	public net.querz.nbt.tag.LongTag clone() {
		return new net.querz.nbt.tag.LongTag(getValue());
	}
}
