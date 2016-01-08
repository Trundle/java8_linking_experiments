package de.hammerhartes.test.andy.apptest;

import de.hammerhartes.test.andy.apptest.annotations.Path;

import org.glassfish.jersey.uri.UriTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.function.Function;

import jodd.methref.Methref;

import static java.lang.String.format;

public class Router {

    @FunctionalInterface
    private interface GetMethod {

        Method apply(String name) throws NoSuchMethodException;
    }

    @FunctionalInterface
    public interface NoParam<H, R> {

        R apply(H h);
    }

    @FunctionalInterface
    public interface OneParam<H, P, R> {

        R apply(H h, P p);
    }

    public <H, R> URI linkTo(final Class<H> handlerClass, final NoParam<H, R> methodReference) {
        final UriTemplate template = getUriTemplate(handlerClass, methodReference::apply,
                                                    name -> handlerClass.getMethod(name));
        if (template.getNumberOfTemplateVariables() != 0) {
            // XXX actual message
            throw new RuntimeException("Meep meep != 0");
        }
        return URI.create(template.createURI());
    }

    public <H, P, R> URI linkTo(final Class<H> handlerCass, final OneParam<H, P, R> methodReference, final P param) {
        final UriTemplate template = getUriTemplate(handlerCass, handler -> methodReference.apply(handler, param),
                                                    name -> handlerCass.getMethod(name, param.getClass()));
        if (template.getNumberOfTemplateVariables() != 1) {
            // XXX actual message
            throw new RuntimeException("Meep meep != 1");
        }
        return URI.create(template.createURI(param.toString()));
    }

    private <H, R> UriTemplate getUriTemplate(final Class<H> handlerClass, final Function<H, R> resolve,
                                              final GetMethod getMethod) {
        final Methref<H> methref = Methref.on(handlerClass);
        resolve.apply(methref.to());
        final Method method;
        try {
            method = getMethod.apply(methref.ref());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        final String template = getUriTemplate(handlerClass, method);
        return new UriTemplate(template);
    }

    private <H> @NotNull String getUriTemplate(final Class<H> handlerClass, final Method method) {
        final Path methodAnnotation = method.getAnnotation(Path.class);
        final Path classAnnotation = handlerClass.getAnnotation(Path.class);
        String template = classAnnotation != null ? classAnnotation.value() : "";
        if (methodAnnotation != null) {
            if (!methodAnnotation.value().startsWith("/")) {
                template += "/";
            }
            template += methodAnnotation.value();
        }
        if (template.isEmpty()) {
            throw new RuntimeException(format("No @Path annotation found for '%s::%s'!",
                                              method.getDeclaringClass().getSimpleName(), method.getName()));
        }
        return template;
    }
}
