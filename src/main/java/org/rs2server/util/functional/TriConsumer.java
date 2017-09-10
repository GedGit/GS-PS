package org.rs2server.util.functional;

import java.util.Objects;

/**
 * @author twelve
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
	void accept(A a, B b, C c);

	default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> consumer) {
		Objects.requireNonNull(consumer);
		return (a, b, c) -> {
			this.accept(a, b, c);
			consumer.accept(a, b, c);
		};
	}
}

