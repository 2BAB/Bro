package me.xx2bab.bro.common.gen.anno

import me.xx2bab.bro.common.BroProperties

interface IBroAliasRoutingTable {
    fun getRoutingMapByAnnotation(
            annotation: Class<out kotlin.Annotation>
    ): Map<String, BroProperties>
}