package de.hammerhartes.andy.linkingtest.converters;

/**
 * Converts a parameter value from {@code String} to {@code T}.
 *
 * @param <T> the type to convert to
 */
public interface ParamReader<T> {

    T fromString(String value);
}
