package org.rs2server.util.functional;

import java.util.Objects;

/**
 * @author twelve
 */
@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
	void accept(A a, B b, C c, D d);

	default QuadConsumer<A, B, C, D> andThen(QuadConsumer<? super A, ? super B, ? super C, ? super D> consumer) {
		Objects.requireNonNull(consumer);
		return (a, b, c, d) -> {
			this.accept(a, b, c, d);
			consumer.accept(a, b, c, d);
		};
	}
}

