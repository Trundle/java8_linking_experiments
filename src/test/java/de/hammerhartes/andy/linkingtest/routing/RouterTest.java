package de.hammerhartes.andy.linkingtest.routing;


import org.junit.Test;

import java.net.URI;

import javax.ws.rs.Path;

import ratpack.handling.Context;

import static org.junit.Assert.assertEquals;

public class RouterTest {

    @Test
    public void testStringReturnWithoutParam() {
        final URI uri = Router.linkTo(Handler.class, Handler::get);
        assertEquals(URI.create("/handler"), uri);
    }

    @Test
    public void testSubpathWithoutParam() {
        final URI uri = Router.linkTo(Handler.class, Handler::subpath);
        assertEquals(URI.create("/handler/subpath"), uri);
    }

    @Test
    public void testSubpathWithParam() {
        final URI uri = Router.linkTo(Handler.class, Handler::subpathWithParam, "param");
        assertEquals(URI.create("/handler/param"), uri);
    }

    @Test
    public void testSubpathWitDerivedType() {
        final URI uri = Router.linkTo(Handler.class, Handler::charSequence, "param");
        assertEquals(URI.create("/handler/param"), uri);
    }

    @Path("/handler")
    public static class Handler {

        public void get(final Context context) {
            context.render("GET!");
        }

        @Path("subpath")
        public void subpath(final Context context) {
            context.render("subpath");
        }

        @Path("{id}")
        public void subpathWithParam(final Context context, final String id) {
            context.render(id);
        }

        @Path("{value}")
        public void charSequence(final Context context, final CharSequence value) {
            context.render(value);
        }
    }
}
