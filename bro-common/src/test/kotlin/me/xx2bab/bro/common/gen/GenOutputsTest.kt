package me.xx2bab.bro.common.gen

import org.junit.Assert
import org.junit.Test

class GenOutputsTest {

    @Test
    fun generateClassNameForImplementation_Regular() {
        val name = GenOutputs.generateClassNameForImplementation(GenOutputsTest::class.java)
        Assert.assertEquals("meXx2babBroCommonGenGenOutputsTestImpl", name)
    }

}