package me.xx2bab.bro.common.util

import org.junit.Assert
import org.junit.Test

class FileUtilsTest {

    @Test
    fun filterIllegalCharsForResFileName_Regular() {
        val originName = "abc:def-ghi.jkl"
        val filteredName = FileUtils.default.filterIllegalCharsForResFileName(originName)
        Assert.assertFalse(filteredName.contains(":"))
        Assert.assertFalse(filteredName.contains("-"))
        Assert.assertFalse(filteredName.contains("."))
    }

}