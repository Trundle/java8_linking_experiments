package de.hammerhartes.andy.linkingtest.routing;

import com.google.common.collect.ImmutableSet;

import org.glassfish.jersey.uri.PatternWithGroups;

import java.util.Set;

import ratpack.handling.Handler;

import static java.util.Objects.requireNonNull;

/**
 * Represents a route: a path pattern, the allowed methods and a ratpack handler.
 */
class Route {

    private final PatternWithGroups routingPattern;
    private final Handler handler;
    private final Set<String> allowedMethods;

    public Route(final PatternWithGroups routingPattern, final Handler handler,
                 final Set<String> allowedMethods) {
        this.routingPattern = requireNonNull(routingPattern);
        this.handler = requireNonNull(handler);
        this.allowedMethods = ImmutableSet.copyOf(allowedMethods);
    }

    /**
     * Returns the path routing pattern.
     *
     * @return the path routing pattern
     */
    public PatternWithGroups routingPattern() {
        return routingPattern;
    }

    /**
     * Returns the route's handler.
     *
     * @return route handler
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * Returns the HTTP methods allowed for this route.
     *
     * @return allowed HTTP methods
     */
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }
}
