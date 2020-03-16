package me.xx2bab.bro.common.gen.anno

import java.lang.annotation.ElementType

/**
 * Created on 2019-07-17
 */
data class AnnotatedElement(val name: String,
                            val type: ElementType,
                            val clazz: String,
                            val annotations: Set<Annotation>)