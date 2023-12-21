package net.thiki.crypt

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("manual test")
class BundlesToolAppTest{

    @Test
    fun testDecrypt() {
        CommandLine(BundlesToolApp()).execute(
            "--private-key-file=private.key",
            "--zip-file-name=bundles.zip",
            "--target=bundles",
            "decrypt"
        )
    }
}