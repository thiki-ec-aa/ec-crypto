package net.thiki.crypt

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileZipperTest{

    @Test
    @Disabled("manual test")
    fun test(){
        val cut = FileZipper()
        cut.zipFile("bundles.zip", "src/test/resources/bundles")
    }
}