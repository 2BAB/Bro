package me.xx2bab.bro.compiler.util

import java.io.File
import java.nio.file.Paths
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class AptGenLocationProvider(private val processingEnv: ProcessingEnvironment) {

    private var dir: File? = null

    fun get(): File {
        if (dir != null) {
            return dir!!
        }
        try {
            val filer = processingEnv.filer
            val originatingElements: List<Element> = listOf()
            val javaFileObject = filer.createSourceFile("me.xx2bab.bro.dummy.DummyClass",
                    *originatingElements.toTypedArray())
            val projectPath = Paths.get(javaFileObject.toUri()).parent // to get the parent
            javaFileObject.delete()
            dir = projectPath.toFile()
            return dir!!
        } catch (e: Exception) {
            // kapt.kotlin.generated -> /path-to-your-module/build/generated/source/kaptKotlin/debug
            val kaptGenerateingDir = processingEnv.options["kapt.kotlin.generated"]
            if (kaptGenerateingDir == null || !File(kaptGenerateingDir).exists()) {
                throw e
            }
            dir = File(kaptGenerateingDir)
            return dir!!
        }
    }

}