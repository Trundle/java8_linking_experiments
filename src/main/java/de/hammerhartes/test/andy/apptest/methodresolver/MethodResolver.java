package de.hammerhartes.test.andy.apptest.methodresolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Similar to {@link jodd.methref.Methref}, but resolves to {@link Method} objects instead of method
 * names. Useful for getting method references "back" from lambdas.
 *
 * <p>Example usage:
 * <pre>
 *     class Example {
 *         public String hello(String name) {
 *             return "Hello " + name;
 *         }
 *     }
 *
 *     MethodResolver&lt;Example&gt; resolver = MethodResolver.on(Example.class);
 *     resolver.to().hello("Joe");
 *     // Returns the method Example#hello
 *     Method m = resolver.resolve();
 * </pre>
 */
public class MethodResolver<T> {

    private static final MethodResolverProxetta PROXETTA = new MethodResolverProxetta();
    private static final Map<Class<?>, Class<?>> CACHE = new WeakHashMap<>();

    private final T instance;

    private MethodResolver(final Class<T> cls) {
        @SuppressWarnings("unchecked")
        final Class<T> proxyClass = (Class<T>) CACHE.computeIfAbsent(cls, PROXETTA::defineProxy);
        try {
            instance = proxyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a proxy instance of the target class, so methods can be called immediately after
     * (fluent interface).
     *
     * @return proxy instance of the target class
     */
    public T to() {
        return instance;
    }

    /**
     * Returns the method object. Note that a method on the object returned by {@link #to()} has
     * to be called before this method can be called.
     *
     * @return resolved method object
     */
    public Method resolve() {
        final Field methodField;
        try {
            methodField = instance.getClass().getDeclaredField("$__method$0");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        final Method method;
        try {
            method = (Method) methodField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (method == null) {
            throw new IllegalStateException("No target method called!");
        }
        return method;
    }

    public static <T> MethodResolver<T> on(final Class<T> cls) {
        return new MethodResolver<>(cls);
    }
}
