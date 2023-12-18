package net.thiki.crypt

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileZipperTest{

    @Test
    @Disabled("manual test")
    fun testZipFolder(){
        val cut = FileZipper()
        cut.zipFolder("bundles.zip", "src/test/resources/bundles", "hello kitty".toCharArray())
    }

    @Test
    @Disabled("manual test")
    fun testExtractAll(){
        val cut = FileZipper()
        cut.extract("bundles.zip", "test", "hello kitty".toCharArray())
    }

}