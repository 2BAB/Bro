package me.xx2bab.bro.core.module;

import java.util.HashMap;
import java.util.Map;

import me.xx2bab.bro.core.Bro;
import me.xx2bab.bro.core.base.BroErrorType;
import me.xx2bab.bro.common.BroProperties;
import me.xx2bab.bro.common.IBroModule;
import me.xx2bab.bro.core.util.BroRuntimeLog;

public class ModuleRudder {

    private HashMap<String, BroProperties> moduleMap;
    private Map<String, ModuleEntity> moduleInstanceMap;

    public ModuleRudder(HashMap<String, BroProperties> moduleMap) {
        this.moduleMap = moduleMap;
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
                bean.nick = entry.getKey();
                bean.instance = instance;
                bean.properties = entry.getValue();
                moduleInstanceMap.put(entry.getKey(), bean);
            } catch (Exception e) {
                BroRuntimeLog.e("Bro Module named " + name + " init failed : " + e.getMessage());
                Bro.getBroMonitor().onModuleException(BroErrorType.MODULE_INIT_ERROR);
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

        if (Bro.getBroInterceptor().beforeGetModule(Bro.appContext,
                moduleNick,
                broModule,
                properties)) {
            return null;
        }
        if (broModule == null) {
            BroRuntimeLog.e("The Module Nick \"" + moduleNick + "\" is not found by Bro!");
            Bro.getBroMonitor().onModuleException(BroErrorType.MODULE_CANT_FIND_TARGET);
            return null;
        }
        return broModule;
    }

}
