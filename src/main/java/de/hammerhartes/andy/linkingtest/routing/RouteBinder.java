package de.hammerhartes.andy.linkingtest.routing;

import org.glassfish.jersey.uri.UriTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.MatchResult;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import static java.lang.String.format;

public class RouteBinder {

    public <H> Handler buildRatpackHandler(final H handler, final Method method,
                                           final UriTemplate uriTemplate) {
        signatureSanityCheck(method, uriTemplate);
        final int parameterCount = method.getParameterCount();
        if (parameterCount == 1) {
            return ctx -> method.invoke(handler, ctx);
        } else if (parameterCount == 2) {
            return ctx -> {
                final MatchResult match = ctx.getExecution().get(MatchResult.class);
                method.invoke(handler, ctx, match.group(1));
            };
        }
        throw new UnsupportedOperationException();
    }

    private void signatureSanityCheck(final Method method, final UriTemplate uriTemplate) {
        final Parameter[] parameters = method.getParameters();
        if (parameters.length != uriTemplate.getNumberOfTemplateVariables() + 1) {
            throw new IllegalArgumentException("Wrong number of arguments!");
        }
        final Parameter firstParameter = parameters[0];
        if (!firstParameter.getType().isAssignableFrom(Context.class)) {
            final String message =
                    format("The first parameter is expected to be of type Context, but was of type "
                           + "%s instead",
                           firstParameter.getType().getSimpleName());
            throw new IllegalArgumentException(message);
        }
        for (int i = 1; i < parameters.length; ++i) {
            final Parameter parameter = parameters[1];
            if (!parameter.getType().isAssignableFrom(String.class)) {
                final String message =
                        format("Only String path parameters are supported for now "
                               + "(found parameter of type %s)",
                               parameter.getType().getSimpleName());
                throw new IllegalArgumentException(message);
            }
        }
    }
}
