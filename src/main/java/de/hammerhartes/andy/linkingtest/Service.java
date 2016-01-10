package de.hammerhartes.andy.linkingtest;

import de.hammerhartes.andy.linkingtest.routing.Router;

import org.slf4j.MDC;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.HandlerDecorator;
import ratpack.handling.RequestId;
import ratpack.logging.MDCInterceptor;
import ratpack.server.RatpackServer;

public class Service {

    private static Handler REQUEST_ID_HANDLER = ctx -> {
        final RequestId requestId = ctx.get(RequestId.class);
        ctx.getResponse().getHeaders().add("X-Request-Id", requestId);
        MDC.put("requestId", requestId.toString());
        ctx.next();
    };

    public void run() throws Exception {
        RatpackServer.start(server -> server
                .registryOf(r -> r
                        .add(HandlerDecorator.prepend(REQUEST_ID_HANDLER))
                        .add(MDCInterceptor.instance())
                        .add(new RootResourceHandler()))
                .handlers(chain -> {
                    final Router router = new Router(chain);
                    router.add(RootResourceHandler.class);
                    chain.all(router);
                }));
    }

    public static void main(final String... args) throws Exception {
        new Service().run();
    }
}
