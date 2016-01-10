package de.hammerhartes.andy.linkingtest.routing;

import de.hammerhartes.andy.linkingtest.methodresolver.MethodResolver;

import org.glassfish.jersey.uri.UriTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;

import javax.ws.rs.Path;

import io.netty.handler.codec.http.HttpResponseStatus;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import static de.hammerhartes.andy.linkingtest.routing.UriTemplateHelper.joinTemplates;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class Router implements Handler {

    private final RouteScanner routeScanner = new RouteScanner();
    private final List<Route> routes = new ArrayList<>();
    private final Chain chain;

    public Router(final Chain chain) {
        this.chain = requireNonNull(chain);
    }

    public <H> void add(final Class<H> handlerClass) {
        final H handler = chain.getRegistry().get(handlerClass);
        routes.addAll(routeScanner.findRoutes(handlerClass, handler));
    }

    @Override
    public void handle(final Context ctx) throws Exception {
        final String path = "/" + ctx.getRequest().getPath();
        for (final Route route : routes) {
            final MatchResult match = route.routingPattern().match(path);
            if (match != null) {
                // XXX how to handle OPTIONS requests?
                if (route.getAllowedMethods().contains(ctx.getRequest().getMethod().getName())) {
                    // XXX own class
                    ctx.getExecution().add(match);
                    ctx.insert(route.getHandler());
                } else {
                    ctx.clientError(HttpResponseStatus.METHOD_NOT_ALLOWED.code());
                }
                return;
            }
        }
        ctx.next();
    }

    public static <H> URI linkTo(final Class<H> handlerClass, final NoParam<H> methodReference) {
        final UriTemplate template =
                getUriTemplate(handlerClass, handler -> methodReference.apply(handler, null));
        if (template.getNumberOfTemplateVariables() != 0) {
            // XXX actual message
            throw new RuntimeException("Meep meep != 0");
        }
        return URI.create(template.createURI());
    }

    public static <H, P> URI linkTo(final Class<H> handlerCass,
                                    final OneParam<H, P> methodReference, final P param) {
        final UriTemplate template =
                getUriTemplate(handlerCass, handler -> methodReference.apply(handler, null, param));
        if (template.getNumberOfTemplateVariables() != 1) {
            // XXX actual message
            throw new RuntimeException("Meep meep != 1");
        }
        return URI.create(template.createURI(param.toString()));
    }

    private static <H> UriTemplate getUriTemplate(final Class<H> handlerClass,
                                                  final Action<H> resolve) {
        final MethodResolver<H> methodResolver = MethodResolver.on(handlerClass);
        resolve.execute(methodResolver.to());
        final Method method = methodResolver.resolve();
        final String template = getUriTemplate(handlerClass, method);
        return new UriTemplate(template);
    }

    private static <H> @NotNull String getUriTemplate(final Class<H> handlerClass,
                                                      final Method method) {
        final Optional<String> methodAnnotation = Optional
                .ofNullable(method.getAnnotation(Path.class))
                .map(Path::value);
        final Optional<String> classAnnotation = Optional
                .ofNullable(handlerClass.getAnnotation(Path.class))
                .map(Path::value);
        if (!methodAnnotation.isPresent() && !classAnnotation.isPresent()) {
            throw new RuntimeException(format("No @Path annotation found for '%s::%s'!",
                                              method.getDeclaringClass().getSimpleName(),
                                              method.getName()));
        }
        return joinTemplates(classAnnotation, methodAnnotation);
    }

    @FunctionalInterface
    interface Action<P> {

        void execute(P param);
    }
}
