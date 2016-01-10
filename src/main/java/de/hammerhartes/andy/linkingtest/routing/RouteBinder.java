package de.hammerhartes.andy.linkingtest.routing;

import com.google.common.reflect.TypeToken;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import de.hammerhartes.andy.linkingtest.converters.ParamReader;

import org.glassfish.jersey.uri.UriTemplate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.MatchResult;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.registry.Registry;
import ratpack.util.Types;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class RouteBinder {

    private static final TypeToken<ParamReader<?>> PARAM_READER_TYPE = new TypeToken<ParamReader<?>>() {
    };

    private final TypeResolver typeResolver = new TypeResolver();
    private final Registry registry;

    public RouteBinder(final Registry registry) {
        this.registry = requireNonNull(registry);
    }

    public <H> Handler buildRatpackHandler(final H handler, final Method method,
                                           final UriTemplate uriTemplate) {
        signatureSanityCheck(method, uriTemplate);
        final int parameterCount = method.getParameterCount();
        if (parameterCount == 1) {
            return ctx -> method.invoke(handler, ctx);
        } else if (parameterCount == 2) {
            return buildOneParamHandler(handler, method);
        } else if (parameterCount == 3) {
            return buildTwoParamsHandler(handler, method);
        }
        throw new UnsupportedOperationException();
    }

    private <H, P> Handler buildOneParamHandler(final H handler, final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<P> paramType = Types.cast(parameterTypes[1]);
        final ParamReader<P> paramReader = findReader(paramType);
        return ctx -> {
            final MatchResult match = ctx.getExecution().get(MatchResult.class);
            method.invoke(handler, ctx, paramReader.fromString(match.group(1)));
        };
    }

    private <H, P1, P2> Handler buildTwoParamsHandler(final H handler, final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<P1> param1Type = Types.cast(parameterTypes[1]);
        final ParamReader<P1> param1Reader = findReader(param1Type);
        final Class<P2> param2Type = Types.cast(parameterTypes[2]);
        final ParamReader<P2> param2Reader = findReader(param2Type);
        return ctx -> {
            final MatchResult match = ctx.getExecution().get(MatchResult.class);
            method.invoke(handler, ctx,
                          param1Reader.fromString(match.group(1)),
                          param2Reader.fromString(match.group(2)));
        };
    }

    private <P> @NotNull ParamReader<P> findReader(final Class<P> paramType) {
        for (ParamReader<?> reader : registry.getAll(PARAM_READER_TYPE)) {
            final ResolvedType readerType = typeResolver.resolve(reader.getClass());
            final ResolvedType readerParam = readerType.typeParametersFor(ParamReader.class).get(0);
            if (readerParam.getErasedType().isAssignableFrom(paramType)) {
                @SuppressWarnings("unchecked")
                final ParamReader<P> paramReader = (ParamReader<P>) reader;
                return paramReader;
            }
        }
        throw new IllegalArgumentException("No reader registered for type " + paramType.getSimpleName());
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
