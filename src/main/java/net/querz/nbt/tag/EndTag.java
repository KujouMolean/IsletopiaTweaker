package net.querz.nbt.tag;

import net.querz.nbt.tag.Tag;

public final class EndTag extends Tag<Void> {

	public static final byte ID = 0;
	public static final net.querz.nbt.tag.EndTag INSTANCE = new net.querz.nbt.tag.EndTag();

	private EndTag() {
		super(null);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	protected Void checkValue(Void value) {
		return value;
	}

	@Override
	public String valueToString(int maxDepth) {
		return "\"end\"";
	}

	@Override
	public net.querz.nbt.tag.EndTag clone() {
		return INSTANCE;
	}
}
