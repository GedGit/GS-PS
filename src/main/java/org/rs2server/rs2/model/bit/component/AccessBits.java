package org.rs2server.rs2.model.bit.component;

/**
 * Utilities and fields related to packing an {@link Access}.
 * @author twelve
 */
public final class AccessBits {

	public static final BitPackedValue CLICK_CONTINUE = BitPackedValue.of(1, 0);
	public static final BitPackedValue WORLDSWITCH = BitPackedValue.of(1, 17);
	public static final BitPackedValue DRAG_COMPONENT = BitPackedValue.of(1, 21);
	public static final BitPackedValue SPELL_USABLE = BitPackedValue.of(1, 22);
	public static final BitPackedValue DRAG_ITEM = BitPackedValue.of(1, 28);
	public static final BitPackedValue REPLACE_ITEM_ON_DRAG = BitPackedValue.of(1, 29);
	public static final BitPackedValue ITEM_OPTION = BitPackedValue.of(1, 30);
	public static final BitPackedValue USE_WITH_OPTION = BitPackedValue.of(1, 31);
	public static final BitPackedValue SHOPS = BitPackedValue.of(1, 51);
	public static final BitPackedValue ALLOW_LEFT_CLICK = optionBit(1);

	/**
	 * Creates a bit packed value that will allow options to appear.
	 * For example on emote widget sending {@code optionBit(1)} will allow for
	 * left click interaction.
	 * @param visibleAmount The visible amount of options to flag displayed.
	 * @return A {@link BitPackedValue}.
	 */
	public static BitPackedValue optionBit(int visibleAmount) {
		int set = 0;
		for (int i = 0; i < visibleAmount; i++) {
			set |= 1 << (i + 1);
		}
		return BitPackedValue.of(set, 0);
	}

	/**
	 * Creates a bit packed value that contains all of the interaction types flagged.
	 * @param types The types of interactions to flag.
	 * @return A {@link BitPackedValue}.
	 */
	public static BitPackedValue interactionBit(InteractionType... types) {
		int set = 0;
		for (InteractionType type : types) {
			set |= 1 << type.ordinal();
		}
		return BitPackedValue.of(set, 11);
	}

	public static BitPackedValue eventDepth(int depth) {
		return BitPackedValue.of(depth, 18);
	}

}
