package me.xx2bab.bro.compiler.classloader

import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.util.*

class GradleClassLoader(classPaths: Array<String>) {

    private val urlClassLoader: URLClassLoader

    @Throws(ClassNotFoundException::class)
    fun load(clazz: String?): Class<*> {
        return urlClassLoader.loadClass(clazz)
    }

    init {
        val urls: MutableList<URL> = ArrayList()
        for (classPath in classPaths) {
            try {
                urls.add(URL(classPath))
            } catch (e: MalformedURLException) {
                throw IllegalArgumentException("Classloader URL " + classPath
                        + "is invalid.")
            }
        }
        urlClassLoader = URLClassLoader(urls.toTypedArray(), this.javaClass.classLoader)
    }
}