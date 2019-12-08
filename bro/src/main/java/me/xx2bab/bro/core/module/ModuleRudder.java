package me.xx2bab.bro.core.module;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.annotations.BroApi;
import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroApi;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable;
import me.xx2bab.bro.common.gen.anno.IBroApiInterfaceAndAliasMap;
import me.xx2bab.bro.core.BroContext;
import me.xx2bab.bro.core.base.BroErrorType;
import me.xx2bab.bro.core.base.IBroInterceptor;
import me.xx2bab.bro.core.base.IBroMonitor;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class ModuleRudder {

    private Map<String, ModuleEntity> moduleInstanceMap;
    private BroContext broContext;
    private IBroInterceptor interceptor;
    private IBroMonitor monitor;
    private DAG<String> dag;

    public ModuleRudder(BroContext broContext) {
        this.broContext = broContext;
        interceptor = broContext.interceptor;
        monitor = broContext.monitor;

        moduleInstanceMap = new HashMap<>();
        dag = new DAG<>();
        Map<String, BroProperties> map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable.class)
                .getRoutingMapByAnnotation(BroModule.class);
        for (Map.Entry<String, BroProperties> entry : map.entrySet()) {
            String name = entry.getValue().clazz;
            try {
                IBroModule instance = (IBroModule) Class.forName(name).newInstance();
                ModuleEntity bean = new ModuleEntity();
                bean.clazz = name;
                bean.instance = instance;
                bean.properties = entry.getValue();
                moduleInstanceMap.put(name, bean);


                if (instance.getLaunchDependencies() == null) {
                    dag.addPrerequisite(name, null);
                } else {
                    for (Class<? extends IBroApi> apiClazz : instance.getLaunchDependencies()) {
                        dag.addPrerequisite(name, getAttachedModule(apiClazz.getCanonicalName()));
                    }
                }
            } catch (Exception e) {
                BroRuntimeLog.e("Bro Module named " + name + " newInstance() failed : " + e.getMessage());
                monitor.onModuleException(BroErrorType.MODULE_CLASS_NOT_FOUND_ERROR);
            }
        }
    }

    public void onCreate() {
        for (String moduleName : dag.topologicalSort()) {
            if (moduleInstanceMap.get(moduleName) != null) {
                moduleInstanceMap.get(moduleName).instance.onCreate(broContext.context.get());
            }
        }
        dag = null;
    }

    @SuppressWarnings("unchecked")
    public <T extends IBroModule> T getModule(Class<T> moduleName) {
        IBroModule broModule = null;
        BroProperties properties = null;
        for (Map.Entry<String, ModuleEntity> entry : moduleInstanceMap.entrySet()) {
            if (entry.getKey().equals(moduleName.getCanonicalName())) {
                broModule = entry.getValue().instance;
                properties = entry.getValue().properties;
                break;
            }
        }

        if (interceptor.beforeGetModule(broContext.context.get(),
                moduleName.getCanonicalName(),
                broModule,
                properties)) {
            return null;
        }
        if (broModule == null) {
            BroRuntimeLog.e("The Module Alias \"" + moduleName.getCanonicalName() + "\" is not found by Bro!");
            monitor.onModuleException(BroErrorType.MODULE_CANT_FIND_TARGET);
            return null;
        }
        return (T) broModule;
    }

    /**
     * TODO: refactor it by generating a better map for indexing
     */
    private String getAttachedModule(String apiClass) {
        String apiImplAlias = broContext.broRudder
                .getImplementationByInterface(IBroApiInterfaceAndAliasMap.class)
                .getAliasByInterface(apiClass);

        Map<String, BroProperties> map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable.class)
                .getRoutingMapByAnnotation(BroApi.class);

        if (map.containsKey(apiImplAlias)) {
            return map.get(apiImplAlias).module;
        }

        throw new IllegalArgumentException("Couldn't find api's corresponding module.");
    }

}
