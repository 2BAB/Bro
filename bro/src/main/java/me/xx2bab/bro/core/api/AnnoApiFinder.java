package me.xx2bab.bro.core.api;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.annotations.BroApi;
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

        // Looking up from cache map
        if (aliasInstanceMap.containsKey(alias)) {
            instance = aliasInstanceMap.get(alias);
            properties = aliasPropertiesMap.get(alias);
        } else {
            // Or it is the first time we init the instance
            try {
                properties = aliasPropertiesMap.get(alias);
                if (properties == null) {
                    throw new IllegalArgumentException();
                }
                instance = (IBroApi) Class.forName(properties.clazz).newInstance();
                instance.onCreate();
                aliasInstanceMap.put(alias, instance);
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
