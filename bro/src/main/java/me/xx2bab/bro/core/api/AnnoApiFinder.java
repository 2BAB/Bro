package me.xx2bab.bro.core.api;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroSingleton;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable;
import me.xx2bab.bro.common.gen.anno.IBroApiInterfaceAndAliasMap;
import me.xx2bab.bro.core.BroContext;
import me.xx2bab.bro.core.base.BroErrorType;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class AnnoApiFinder implements IApiFinder {

    private Map<String, IBroApi> aliasInstanceMap;
    private BroContext broContext;
    private IBroInterceptor interceptor;
    private IBroMonitor monitor;

    public AnnoApiFinder(BroContext broContext) {
        this.broContext = broContext;
        interceptor = broContext.interceptor;
        monitor = broContext.monitor;
        aliasInstanceMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IBroApi> T getApi(Class<T> apiInterface) {
        String alias = broContext.broRudder
                .getImplementationByInterface(IBroApiInterfaceAndAliasMap.class)
                .getAliasByInterface(apiInterface.getCanonicalName());
        return (T) getApi(alias);
    }

    @Override
    public IBroApi getApi(String alias) {
        if (alias == null || alias.isEmpty()) {
            BroRuntimeLog.e("The Api alias is EMPTY!");
            monitor.onApiException(BroErrorType.API_CANT_FIND_TARGET);
            return null;
        }
        IBroApi instance;
        BroProperties properties;
        Map<String, BroProperties> aliasPropertiesMap = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable.class)
                .getRoutingMapByAnnotation(BroApi.class);

        // If api cache was enabled, looking up from cache map first
        if (aliasInstanceMap.containsKey(alias)) {
            instance = aliasInstanceMap.get(alias);
            properties = aliasPropertiesMap.get(alias);
        } else {
            try {
                properties = aliasPropertiesMap.get(alias);
                if (properties == null) {
                    throw new IllegalArgumentException();
                }
                instance = (IBroApi) Class.forName(properties.getClazz()).newInstance();
                instance.onCreate();
                if (broContext.apiCacheEnabled || properties.getExtraAnnotations().containsKey(
                        BroSingleton.class.getCanonicalName())) {
                    aliasInstanceMap.put(alias, instance);
                }
            } catch (Exception e) {
                BroRuntimeLog.e("The Api alias \"" + alias + "\" is not found by Bro!");
                monitor.onApiException(BroErrorType.API_CANT_FIND_TARGET);
                return null;
            }
        }

        if (interceptor.beforeGetApi(broContext.context.get(), alias, instance, properties)) {
            return null;
        }

        return instance;
    }

}
