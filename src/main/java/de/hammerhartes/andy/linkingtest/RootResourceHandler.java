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
        final URI secondLink =
                linkTo(RootResourceHandler.class, RootResourceHandler::greetNTimes, "stranger", 10);
        context.render("Hello, world! Please also visit " + link + " or " + secondLink);
    }

    @GET
    @Path("/{name}")
    public void greet(final Context context, final String name) {
        context.render("Hello, " + name + "!");
    }

    @GET
    @Path("/{name}/{times}")
    public void greetNTimes(final Context context, final String name, final Integer times) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            stringBuilder.append("Hello ");
            stringBuilder.append(name);
            stringBuilder.append("!\n");
        }
        context.render(stringBuilder.toString());
    }
}
