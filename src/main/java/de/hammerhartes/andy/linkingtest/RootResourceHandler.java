package de.hammerhartes.andy.linkingtest;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import ratpack.handling.Context;

import static de.hammerhartes.andy.linkingtest.routing.Router.linkTo;

@Path("/")
public class RootResourceHandler {

    @GET
    public void hello(final Context context) {
        final URI link = linkTo(RootResourceHandler.class, RootResourceHandler::greet, "stranger");
        context.render("Hello, world! Please also visit " + link);
    }

    @GET
    @Path("/{name}")
    public void greet(final Context context, final String name) {
        context.render("Hello, " + name + "!");
    }
}
