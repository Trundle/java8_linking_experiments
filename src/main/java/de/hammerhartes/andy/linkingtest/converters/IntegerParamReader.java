package de.hammerhartes.andy.linkingtest.converters;

public class IntegerParamReader implements ParamReader<Integer> {

    @Override
    public Integer fromString(final String value) {
        return Integer.valueOf(value);
    }
}
