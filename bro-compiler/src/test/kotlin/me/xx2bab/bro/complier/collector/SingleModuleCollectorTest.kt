package me.xx2bab.bro.complier.collector

import me.xx2bab.bro.common.Constants
import me.xx2bab.bro.common.gen.anno.IBroAnnoProcessor
import me.xx2bab.bro.common.util.FileUtils
import me.xx2bab.bro.compiler.collector.SingleModuleCollector
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import javax.annotation.processing.ProcessingEnvironment

@RunWith(MockitoJUnitRunner::class)
class SingleModuleCollectorTest {

    @Mock
    private lateinit var processor: IBroAnnoProcessor

    private lateinit var processorList: List<IBroAnnoProcessor>

    @Mock
    private lateinit var processingEnvironment: ProcessingEnvironment

    @Mock
    private lateinit var fileUtils: FileUtils

    private val moduleName = "test-module"
    private val libMetaDataOutputPath = "libMetaDataOutputPath"
    private val moduleBroBuildDir = "moduleBroBuildDir"
    private lateinit var singleModuleCollector: SingleModuleCollector

    @Captor
    private lateinit var contentCaptor: ArgumentCaptor<String>
    @Captor
    private lateinit var filePathCaptor: ArgumentCaptor<String>
    @Captor
    private lateinit var fileNameCaptor: ArgumentCaptor<String>

    @Before
    fun setup() {
        processorList = listOf(processor)
        singleModuleCollector = Mockito.spy(SingleModuleCollector(processorList,
                processingEnvironment,
                fileUtils,
                moduleName,
                libMetaDataOutputPath,
                moduleBroBuildDir))
    }

    @Test
    fun generate_Successful() {
        Mockito.`when`(fileUtils.filterIllegalCharsForResFileName(moduleName))
                .thenReturn(FileUtils.default.filterIllegalCharsForResFileName(moduleName))
        val fileName = (FileUtils.default.filterIllegalCharsForResFileName(moduleName)
                + Constants.MODULE_META_INFO_FILE_SUFFIX)
        val result = "{\"${processor.javaClass.canonicalName}\":[]}"
        singleModuleCollector.generate()
        Mockito.verify(fileUtils, Mockito.times(2))
                .writeFile(contentCaptor.capture(),
                        filePathCaptor.capture(),
                        fileNameCaptor.capture())
        Assert.assertTrue(contentCaptor.allValues[0] == result)
        Assert.assertTrue(filePathCaptor.allValues[0] == libMetaDataOutputPath)
        Assert.assertTrue(filePathCaptor.allValues[1] == moduleBroBuildDir)
        Assert.assertTrue(fileNameCaptor.allValues[0] == fileName)
    }

}