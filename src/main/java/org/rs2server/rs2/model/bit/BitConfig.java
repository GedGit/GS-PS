package org.rs2server.rs2.model.bit;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a constructed bit packed config.
 * @author Twelve
 */
@Immutable
public final class BitConfig {

    private final int id;
    private final int value;

    public BitConfig(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public final int getId() {
        return id;
    }

    public final int getValue() {
        return value;
    }
}
