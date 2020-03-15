package me.xx2bab.bro.complier.classloader

import me.xx2bab.bro.compiler.classloader.GradleClassLoader
import org.junit.Assert
import org.junit.Test

class GradleClassLoaderTest {

    @Test
    fun load_success() {
        val loader = GradleClassLoader(arrayOf("https://github.com/2BAB/Bro/releases/download/" +
                "1.0.1/bro-annotations-1.0.1.jar"))
        try {
            loader.load("me.xx2bab.bro.annotations.BroActivity")
        } catch (e: Exception) {
            Assert.fail()
        }
    }

    @Test
    fun load_failed() {
        val loader = GradleClassLoader(arrayOf("https://github.com/2BAB/Bro/releases/download/" +
                "1.0.1/bro-annotations-1.0.1.jar"))
        try {
            loader.load("me.xx2bab.bro.annotations.BroActivitysss")
            Assert.fail()
        } catch (e: Exception) {
            // Throw
            Assert.assertTrue(e is ClassNotFoundException)
        }
    }

}