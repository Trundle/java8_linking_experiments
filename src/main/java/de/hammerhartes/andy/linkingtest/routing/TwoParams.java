package de.hammerhartes.andy.linkingtest.routing;

import ratpack.handling.Context;

@FunctionalInterface
public interface TwoParams<H, P1, P2> {

    void apply(H h, Context context, P1 p1, P2 p2);
}
