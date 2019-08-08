package me.xx2bab.bro.core.module;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.annotations.BroModule;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.common.gen.anno.IBroAliasRoutingTable;
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

    public ModuleRudder(BroContext broContext) {
        this.broContext = broContext;
        interceptor = broContext.interceptor;
        monitor = broContext.monitor;
        initModuleClasses();
    }

    public void initModuleClasses() {
        moduleInstanceMap = new HashMap<>();
        Map<String, BroProperties> map = broContext.broRudder
                .getImplementationByInterface(IBroAliasRoutingTable.class)
                .getRoutingMapByAnnotation(BroModule.class);
        for (Map.Entry<String, BroProperties> entry : map.entrySet()) {
            String name = entry.getValue().clazz;
            try {
                IBroModule instance = (IBroModule) Class.forName(name).newInstance();
                instance.onCreate();
                ModuleEntity bean = new ModuleEntity();
                bean.nick = entry.getKey();
                bean.instance = instance;
                bean.properties = entry.getValue();
                moduleInstanceMap.put(entry.getKey(), bean);
            } catch (Exception e) {
                BroRuntimeLog.e("Bro Module named " + name + " init failed : " + e.getMessage());
                monitor.onModuleException(BroErrorType.MODULE_INIT_ERROR);
            }
        }
    }

    public IBroModule getModule(String moduleNick) {
        IBroModule broModule = null;
        BroProperties properties = null;
        for (Map.Entry<String, ModuleEntity> entry : moduleInstanceMap.entrySet()) {
            if (entry.getKey().equals(moduleNick)) {
                broModule = entry.getValue().instance;
                properties = entry.getValue().properties;
                break;
            }
        }

        if (interceptor.beforeGetModule(broContext.context.get(),
                moduleNick,
                broModule,
                properties)) {
            return null;
        }
        if (broModule == null) {
            BroRuntimeLog.e("The Module Nick \"" + moduleNick + "\" is not found by Bro!");
            monitor.onModuleException(BroErrorType.MODULE_CANT_FIND_TARGET);
            return null;
        }
        return broModule;
    }

}
