package org.rs2server.rs2.model.bit;

/**
 * @author Twelve
 */
public final class BitConfigBuilder {

    /**
     * The id of the bit config.
     */
    private final int id;
    /**
     * The value of the bit config.
     */
    private int value;

    public BitConfigBuilder(int id) {
        this.id = id;
    }

    /**
     * Creates a new {@link BitConfigBuilder} which operates on a specified config id.
     * @param id The config id.
     * @return The builder.
     */
    public static BitConfigBuilder of(int id) {
        return new BitConfigBuilder(id);
    }

    public final BitConfigBuilder shiftLeft(int offset) {
        value <<= offset;
        return this;
    }

    public final BitConfigBuilder shiftRight(int offset) {
        value >>= offset;
        return this;
    }

    public final BitConfigBuilder set(int value) {
        return or(value);
    }

    public final BitConfigBuilder unset(int value) {
        return and(value);
    }

    public final BitConfigBuilder set(int multiple, int bit) {
        return set(multiple << bit);
    }

    public final BitConfigBuilder unset(int multiple, int bit) {
        return unset(multiple << bit);
    }

    public final BitConfigBuilder and(int number) {
        value &= ~number;
        return this;
    }

    public final BitConfigBuilder or(int number) {
        value |= number;
        return this;
    }

    public final BitConfig build() {
        return new BitConfig(id, value);
    }
}
