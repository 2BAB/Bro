package me.xx2bab.bro.annotations

import me.xx2bab.bro.common.IBroModule
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class BroActivity(val alias: String, val module: KClass<out IBroModule>)