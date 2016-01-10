package de.hammerhartes.andy.linkingtest.routing;

import org.glassfish.jersey.uri.UriTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import ratpack.handling.Handler;

import static de.hammerhartes.andy.linkingtest.routing.UriTemplateHelper.joinTemplates;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Finds handler methods.
 */
public class RouteScanner {

    private final RouteBinder routeBinder = new RouteBinder();

    public <H> List<Route> findRoutes(final Class<H> handlerClass, final H handler) {
        if (!isPublic(handlerClass.getModifiers())) {
            throw new IllegalArgumentException(format("Class '%s' is not accessible!",
                                                      handlerClass.getSimpleName()));
        }
        final Optional<String> parentTemplate = Optional
                .ofNullable(handlerClass.getAnnotation(Path.class))
                .map(Path::value);
        return Arrays.stream(handlerClass.getMethods())
                .filter(m -> isPublic(m.getModifiers()))
                .map(method -> toRoute(handler, parentTemplate, method))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private <H> Optional<Route> toRoute(final H handler, final Optional<String> parentTemplate,
                                        final Method method) {
        final Set<String> httpMethods = Arrays
                .stream(method.getAnnotations())
                .filter(RouteScanner::isHttpMethodAnnotation)
                .map(annotation -> annotation.annotationType().getAnnotation(HttpMethod.class).value())
                .collect(toSet());
        if (!httpMethods.isEmpty()) {
            final Optional<String> path = Optional
                    .ofNullable(method.getAnnotation(Path.class))
                    .map(Path::value);
            if (!path.isPresent() && !parentTemplate.isPresent()) {
                final String message =
                        format("Handler method '%s#%s' without any @Path annotation!",
                               method.getDeclaringClass().getSimpleName(), method.getName());
                throw new IllegalArgumentException(message);
            }
            final UriTemplate template = new UriTemplate(joinTemplates(parentTemplate, path));
            final Handler ratpackHandler = routeBinder.buildRatpackHandler(handler, method, template);
            return Optional.of(new Route(template.getPattern(), ratpackHandler, httpMethods));
        } else {
            return Optional.empty();
        }
    }

    private static boolean isHttpMethodAnnotation(final Annotation annotation) {
        return annotation.annotationType().getAnnotation(HttpMethod.class) != null;
    }
}
