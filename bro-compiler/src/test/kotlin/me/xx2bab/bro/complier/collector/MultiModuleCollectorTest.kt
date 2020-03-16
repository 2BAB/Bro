package me.xx2bab.bro.complier.collector

import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.gen.GenOutputs
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor
import me.xx2bab.bro.common.util.FileUtils
import me.xx2bab.bro.compiler.collector.MultiModuleCollector
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class MultiModuleCollectorTest {

    private val mockJsonString = "{\"me.xx2bab.bro.gradle.processor.BroApiInterfaceAndAliasMapAnnoProcessor\":[\"{\\\"me.xx2bab.bro.sample.common.api.ISettingsApi\\\":\\\"me.xx2bab.bro.sample.profile.SettingsApiImpl\\\"}\"],\"me.xx2bab.bro.gradle.processor.BroRoutingTableAnnoProcessor\":[\"{\\\"annotations\\\":[{\\\"name\\\":\\\"me.xx2bab.bro.annotations.BroApi\\\",\\\"values\\\":{\\\"module\\\":\\\"me.xx2bab.bro.sample.profile.SettingsModule.class\\\"}},{\\\"name\\\":\\\"me.xx2bab.bro.annotations.BroSingleton\\\",\\\"values\\\":{}}],\\\"clazz\\\":\\\"me.xx2bab.bro.sample.profile.SettingsApiImpl\\\",\\\"name\\\":\\\"me.xx2bab.bro.sample.profile.SettingsApiImpl\\\",\\\"type\\\":\\\"TYPE\\\"}\",\"{\\\"annotations\\\":[{\\\"name\\\":\\\"me.xx2bab.bro.annotations.BroModule\\\",\\\"values\\\":{}}],\\\"clazz\\\":\\\"me.xx2bab.bro.sample.profile.SettingsModule\\\",\\\"name\\\":\\\"me.xx2bab.bro.sample.profile.SettingsModule\\\",\\\"type\\\":\\\"TYPE\\\"}\"]}"

    private val processor = Mockito.mock(IBroAnnoProcessor::class.java)
    private val fileUtils = Mockito.mock(FileUtils::class.java)
    private val processingEnvironment = Mockito.mock(ProcessingEnvironment::class.java)
    private val processorList = listOf(processor)
    private val genOutputs = Mockito.mock(GenOutputs::class.java)
    private lateinit var multiModuleCollector: MultiModuleCollector

    @Before
    fun setup() {
        multiModuleCollector = Mockito.spy(MultiModuleCollector(processorList,
                processingEnvironment,
                fileUtils,
                genOutputs))
    }

    private fun addMetaDataToMap() {
        val folder = Mockito.mock(File::class.java)
        val jsonFile = Mockito.mock(File::class.java)
        Mockito.`when`(folder.exists()).thenReturn(true)
        Mockito.`when`(folder.isDirectory).thenReturn(true)
        Mockito.`when`(folder.listFiles()).thenReturn(arrayOf(jsonFile))
        Mockito.`when`(jsonFile.name).thenReturn("abc"
                + Constants.MODULE_META_INFO_FILE_SUFFIX)
        Mockito.`when`(fileUtils.readFile(jsonFile)).thenReturn(mockJsonString)

        multiModuleCollector.loadFile(folder)
    }

    @Test
    fun loadFile_Successful() {
        addMetaDataToMap()
        Assert.assertTrue(multiModuleCollector.getMap().isNotEmpty())
    }

    @Test
    fun addMetaRecord_Successful() {
        val singleModuleMetaData = mapOf("test" to listOf("value1", "value2"))
        multiModuleCollector.addMetaRecord(singleModuleMetaData)
        Assert.assertTrue(multiModuleCollector.getMap().isNotEmpty())
    }

    @Test
    fun generate_Successful() {
        val list = mutableListOf("value1", "value2")
        list.sortWith(Comparator { t1, t2 -> t1.compareTo(t2) })
        val singleModuleMetaData = mapOf(processor.javaClass.canonicalName to list)
        multiModuleCollector.addMetaRecord(singleModuleMetaData)
        multiModuleCollector.generate()
        Mockito.verify(processor).onGenerate(list, genOutputs, processingEnvironment)
    }

}