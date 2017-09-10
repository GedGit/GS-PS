package org.rs2server.util.functional;

import java.util.stream.Stream;

/**
 * Represents something that can be operated on via java 8 streams.
 * @author twelve
 */
public interface Streamable<T> {

	Stream<T> stream();

}
