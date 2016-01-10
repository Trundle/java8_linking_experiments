package de.hammerhartes.andy.linkingtest.converters;

/**
 * The trivial param reader: does not convert at all.
 */
public class StringParamReader implements ParamReader<String> {

    @Override
    public String fromString(final String value) {
        return value;
    }
}
