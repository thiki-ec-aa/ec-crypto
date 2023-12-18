package net.thiki.crypt

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileZipperTest{

    @Test
    @Disabled("manual test")
    fun testZipFolder(){
        val cut = FileZipper("bundles.zip", "hello kitty".toCharArray())
        cut.zipFolder("src/test/resources/bundles")
    }

    @Test
    @Disabled("manual test")
    fun testExtractAll(){
        val cut = FileZipper("bundles.zip", "hello kitty".toCharArray())
        cut.extractTo("test")
    }

}