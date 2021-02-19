package net.querz.nbt.tag;

import net.querz.nbt.tag.ArrayTag;

import java.util.Arrays;

public class ByteArrayTag extends ArrayTag<byte[]> implements Comparable<net.querz.nbt.tag.ByteArrayTag> {

	public static final byte ID = 7;
	public static final byte[] ZERO_VALUE = new byte[0];

	public ByteArrayTag() {
		super(ZERO_VALUE);
	}

	public ByteArrayTag(byte[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((net.querz.nbt.tag.ByteArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(net.querz.nbt.tag.ByteArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public net.querz.nbt.tag.ByteArrayTag clone() {
		return new net.querz.nbt.tag.ByteArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
