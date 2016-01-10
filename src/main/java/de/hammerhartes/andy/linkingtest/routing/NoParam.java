package de.hammerhartes.andy.linkingtest.routing;

import ratpack.handling.Context;

@FunctionalInterface
public interface NoParam<H> {

    void apply(H h, Context context);
}
