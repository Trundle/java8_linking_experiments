package de.hammerhartes.andy.linkingtest.routing;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import ratpack.handling.Context;
import ratpack.registry.Registry;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

public class RouteScannerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private final RouteScanner routeScanner = new RouteScanner(Registry.empty());

    @Test
    public void testEmptyRoutes() {
        final List<Route> routes = routeScanner.findRoutes(Empty.class, new Empty());
        assertEquals(0, routes.size());
    }

    @Test
    public void testMissingPathThrowsIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Handler method 'NoPath#get' without any @Path annotation!");

        routeScanner.findRoutes(NoPath.class, new NoPath());
    }

    @Test
    public void testWrongContextTypeThrowsIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "The first parameter is expected to be of type Context, but was of type String instead");

        routeScanner.findRoutes(WrongContextType.class, new WrongContextType());
    }

    @Test
    public void testRoutes() {
        final List<Route> routes = routeScanner.findRoutes(Handler.class, new Handler());

        assertEquals(3, routes.size());
        checkMethods(routes.get(0));
        checkMethods(routes.get(1));
        checkMethods(routes.get(2));
    }

    private static void checkMethods(final Route route) {
        final Set<String> allowedMethods = route.getAllowedMethods();
        assertEquals(singleton("GET"), allowedMethods);
    }

    public static class Empty {

    }

    public static class NoPath {

        @GET
        public void get(final Context context) {

        }
    }

    @Path("/wrong-context-types")
    public static class WrongContextType {

        @GET
        public void get(final String context) {

        }
    }

    @Path("/path")
    public static class Handler {

        @GET
        public void get(final Context context) {

        }

        @GET
        @Path("/subpath")
        public void subpath(final Context context) {

        }

        @GET
        @Path("/subpath/{id}")
        public void subpath(final Context context, final String id) {

        }
    }
}
