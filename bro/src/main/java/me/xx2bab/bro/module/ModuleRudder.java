package me.xx2bab.bro.module;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.Bro;
import me.xx2bab.bro.base.BroErrorType;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.util.BroRuntimeLog;

public class ModuleRudder {

    private HashMap<String, BroProperties> moduleMap;
    private Map<String, ModuleEntity> moduleInstanceMap;

    public ModuleRudder() {
        this.moduleMap = Bro.getBroMap().getBroModuleMap();
        initModuleClasses();
    }

    public void initModuleClasses() {
        moduleInstanceMap = new HashMap<>();
        for (Map.Entry<String, BroProperties> entry : moduleMap.entrySet()) {
            String name = entry.getValue().clazz;
            try {
                IBroModule instance = (IBroModule) Class.forName(name).newInstance();
                instance.onCreate();
                ModuleEntity bean = new ModuleEntity();
                bean.nick = entry.getKey(); // class canonical name
                bean.instance = instance;
                bean.properties = entry.getValue();
                moduleInstanceMap.put(entry.getKey(), bean);
            } catch (Exception e) {
                BroRuntimeLog.e("Bro Module named " + name + " init failed : " + e.getMessage());
                Bro.getBroMonitor().onModuleException(BroErrorType.MODULE_INIT_ERROR);
            }
        }
    }

    public <T extends IBroModule> T getModule(Class<T> moduleClass) {
        IBroModule broModule = null;
        BroProperties properties = null;
        for (Map.Entry<String, ModuleEntity> entry : moduleInstanceMap.entrySet()) {
            if (entry.getKey().equals(moduleClass.getCanonicalName())) {
                broModule = entry.getValue().instance;
                properties = entry.getValue().properties;
                break;
            }
        }

        if (Bro.getBroInterceptor().onGetModule(Bro.appContext,
                moduleClass.getCanonicalName(),
                broModule,
                properties)) {
            return null;
        }
        if (broModule == null) {
            BroRuntimeLog.e("The Module \"" + moduleClass + "\" is not found by Bro!");
            Bro.getBroMonitor().onModuleException(BroErrorType.MODULE_CANT_FIND_TARGET);
            return null;
        }
        return (T) broModule;
    }

}
