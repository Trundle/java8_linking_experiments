package de.hammerhartes.test.andy.apptest.methodresolver;

import java.lang.reflect.Method;

import jodd.proxetta.ProxyAdvice;

import static jodd.proxetta.ProxyTarget.createArgumentsClassArray;
import static jodd.proxetta.ProxyTarget.returnValue;
import static jodd.proxetta.ProxyTarget.targetClass;
import static jodd.proxetta.ProxyTarget.targetMethodName;

/**
 * MethodResolver advice applied on all methods. It puts the method in a class variable that can be accessed later using
 * reflection.
 */
class MethodResolverAdvice implements ProxyAdvice {

    public Method method;

    public Object execute() {
        final Class<?> targetClass = targetClass();
        final String methodName = targetMethodName();
        final Class<?>[] argumentTypes = createArgumentsClassArray();
        try {
            method = targetClass.getMethod(methodName, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return returnValue(null);
    }

}