package de.hammerhartes.andy.linkingtest.methodresolver;

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

/**
 * MethodResolver Proxetta builder and holder and facade.
 */
class MethodResolverProxetta {

    public static final String METHRESOLV_CLASSNAME_SUFFIX = "Methresolv";

    private final ProxyProxetta proxetta;

    public MethodResolverProxetta() {
        final ProxyAspect aspects = new ProxyAspect(MethodResolverAdvice.class, new AllMethodsPointcut());
        proxetta = ProxyProxetta.withAspects(aspects);
        proxetta.setClassNameSuffix(METHRESOLV_CLASSNAME_SUFFIX);
    }

    /**
     * Generates a new class.
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> defineProxy(Class<T> target) {
        ProxyProxettaBuilder builder = proxetta.builder();
        builder.setTarget(target);
        return builder.define();
    }
}
