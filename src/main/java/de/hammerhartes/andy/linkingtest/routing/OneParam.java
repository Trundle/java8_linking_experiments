package de.hammerhartes.andy.linkingtest.routing;

import ratpack.handling.Context;

@FunctionalInterface
public interface OneParam<H, P> {

    void apply(H h, Context context, P p);
}
