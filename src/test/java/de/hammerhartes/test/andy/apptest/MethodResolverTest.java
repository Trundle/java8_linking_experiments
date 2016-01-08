package de.hammerhartes.test.andy.apptest;


import de.hammerhartes.test.andy.apptest.methodresolver.MethodResolver;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class MethodResolverTest {

    @Test
    public void testResolvesMethod() throws NoSuchMethodException {
        final Method method = Example.class.getMethod("hello", String.class);

        final MethodResolver<Example> methodResolver = MethodResolver.on(Example.class);
        methodResolver.to().hello("Joe");
        assertEquals(method, methodResolver.resolve());
    }

    @Test(expected = IllegalStateException.class)
    public void testResolveCalledBeforeTargetMethodThrowsIllegalStateException() {
        MethodResolver.on(Example.class).resolve();
    }

    public static class Example {

        public String hello(final String name) {
            return "Hello, " + name + "!";
        }
    }
}
