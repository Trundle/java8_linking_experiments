package de.hammerhartes.andy.linkingtest;

import de.hammerhartes.andy.linkingtest.annotations.Inject;
import de.hammerhartes.andy.linkingtest.annotations.Path;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class OtherResourceHandler {

    private final Router router;

    @Inject
    public OtherResourceHandler(final Router router) {
        this.router = requireNonNull(router);
    }

    @Path("/some/path")
    public URI linkToScreens() {
        return router.linkTo(ScreenResourceHandler.class, ScreenResourceHandler::listAll);
    }

    @Path("/some/path/some-screen")
    public URI linkToOneScreen() {
        return router.linkTo(ScreenResourceHandler.class, ScreenResourceHandler::getScreen, "some-screen");
    }
}
