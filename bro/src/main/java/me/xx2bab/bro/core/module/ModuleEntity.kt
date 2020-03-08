package me.xx2bab.bro.core.module

import me.xx2bab.bro.common.BroProperties
import me.xx2bab.bro.common.IBroModule

data class ModuleEntity(val clazz: String,
                        val instance: IBroModule,
                        var properties: BroProperties)
