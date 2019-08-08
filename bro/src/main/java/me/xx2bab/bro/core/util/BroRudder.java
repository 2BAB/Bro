package me.xx2bab.bro.core.util;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.common.Constants;
import me.xx2bab.bro.common.gen.GenOutputs;

public class BroRudder {

    private GenOutputs genOutputs;
    private Map<Class, Object> implCache;

    public BroRudder() {
        genOutputs = new GenOutputs();
        implCache = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getImplementationByInterface(Class<T> interfaze) {
        Object cacheRes = implCache.get(interfaze);
        if (cacheRes != null) {
            return (T) cacheRes;
        }
        try {
            String implClassName = genOutputs.generateClassNameForImplementation(interfaze);
            Object res = Class.forName(Constants.GEN_PACKAGE_NAME + "." + implClassName).newInstance();
            implCache.put(interfaze, res);
            return (T) res;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
