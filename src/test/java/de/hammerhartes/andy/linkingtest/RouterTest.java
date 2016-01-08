package de.hammerhartes.andy.linkingtest;

import de.hammerhartes.andy.linkingtest.annotations.Path;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class RouterTest {

    private Router router = new Router();

    @Test
    public void testStringReturnWithoutParam() {
        final URI uri = router.linkTo(Handler.class, Handler::get);
        assertEquals(URI.create("/handler"), uri);
    }

    @Test
    public void testSubpathWithoutParam() {
        final URI uri = router.linkTo(Handler.class, Handler::subpath);
        assertEquals(URI.create("/handler/subpath"), uri);
    }

    @Test
    public void testSubpathWithParam() {
        final URI uri = router.linkTo(Handler.class, Handler::subpathWithParam, "param");
        assertEquals(URI.create("/handler/param"), uri);
    }

    @Test
    public void testSubpathWitDerivedType() {
        final URI uri = router.linkTo(Handler.class, Handler::charSequence, "param");
        assertEquals(URI.create("/handler/param"), uri);
    }

    @Path("/handler")
    public static class Handler {

        public String get() {
            return "GET!";
        }

        @Path("subpath")
        public String subpath() {
            return "subpath";
        }

        @Path("{id}")
        public String subpathWithParam(final String id) {
            return id;
        }

        @Path("{value}")
        public CharSequence charSequence(final CharSequence value) {
            return value;
        }
    }
}
