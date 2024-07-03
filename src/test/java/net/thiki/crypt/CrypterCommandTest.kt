package net.thiki.crypt

import net.thiki.crypt.command.DecryptCommand
import net.thiki.crypt.command.EncryptCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.pathString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrypterCommandTest {
    @Test
    fun `show help`(){
        val cutCommand = CommandLine(CrypterCommand())
        val exitCode = cutCommand.execute("init-keys", "--help")
        assertEquals(0, exitCode)
    }

    @Test
    fun `test no command specified`(){
        val cutCommand = CommandLine(CrypterCommand())
        val exitCode = cutCommand.execute()
        assertEquals(-1, exitCode)
    }
        @Test
    @Disabled("manual test")
    fun `test init-keys command`(){
        val cutCommand = CommandLine(CrypterCommand())
        val exitCode = cutCommand.execute(
            "init-keys",
            "-pu=target/test/public.key",
            "-pr=target/test/private.key",
            "--password=hellokitty",
        )
        assertEquals(0, exitCode)
        assertTrue(File("target/test/public.key").exists())
        assertTrue(File("target/test/private.key").exists())
    }

    /**
     * must run after `test init-keys command`
     */
    @Test
    @Disabled("manual test")
    fun `test encrypt command`(){
        val cutCommand = CommandLine(CrypterCommand())
        /*
          crypter encrypt -pu="$public_key_file" --zip-file="$bundles_root/$inner_zip_file_name" \
           --bundle-folder="$workspace_home/$datestr"
         */
        val exitCode = cutCommand.execute(
            "encrypt",
            "-pu=target/test/public.key",
            "--zip-file=target/test/bundles.zip",
            "--bundle-folder=src/test/resources/bundles",
        )
        assertEquals(0, exitCode)
        assertTrue(File("target/test/bd-bundles.zip").exists())
    }


    @Test
    fun `test encrypt command outerZipFileName with parent`(){
        val outerZipFileName = EncryptCommand().outerZipFileName("target/xxx.zip", "bd-")
        assertEquals("target/bd-xxx.zip", outerZipFileName)
    }

    @Test
    fun `test encrypt command outerZipFileName without parent`(){
        val outerZipFileName = EncryptCommand().outerZipFileName("xxx.zip", "bd-")
        assertEquals("bd-xxx.zip", outerZipFileName)
    }

    @Test
    fun `test decrypt command innerZipFileName with parent`(){
        val innerZipFileName = DecryptCommand().innerZipFileName("target/bd-xxx.zip", "bd-")
        assertEquals("xxx.zip", innerZipFileName)
    }

    @Test
    fun `test decrypt command innerZipFileName without parent`(){
        val innerZipFileName = DecryptCommand().innerZipFileName("bd-xxx.zip", "bd-")
        assertEquals("xxx.zip", innerZipFileName)
    }
    @Test
    fun `test file's parent not exist`(){
        val file = Paths.get("target/not-exist-${Instant.now().toEpochMilli()}/aFile")
        assertFalse(file.parent.toFile().exists())
    }


    @Test
    fun `test trim single fileName`(){
        val file = "abc.key.zip"
        val trimmed = file.substring(0, file.length - ".zip".length)
        assertEquals("abc.key", trimmed)
    }
    @Test
    fun `test trim fileName with directory`(){
        val file = "aaa/abc.key.zip"
        val trimmed = file.substring(0, file.length - ".zip".length)
        assertEquals("aaa/abc.key", trimmed)
    }
    @Test
    fun `test path parent`(){
        val parentPath = Paths.get("abc/def/ghi.key.zip").parent.pathString
        assertEquals("abc${File.separator}def", parentPath)
    }

    /**
     * must run after `test encrypt command`
     */
    @Test
    @Disabled("manual test")
    fun `test decrypt command`(){
        val cutCommand = CommandLine(CrypterCommand())
        /*
         crypter decrypt -pr="target/test/private.key.zip" --zip-file="target/test/bd-bundles.zip" \
          --target="$workspace_home/$datestr"
         */
        val exitCode = cutCommand.execute(
            "decrypt",
            "--target=target/test/extracted-bundles",
            "-pr=target/test/private.key.zip",
            "--zip-file=target/test/bd-bundles.zip",
            "--password=hellokitty",
        )
        assertEquals(0, exitCode)
        assertTrue(File("target/test/extracted-bundles/bundles").exists())
        assertFalse(File("target/test/extracted-bundles/bundles.zip").exists())
        assertFalse(File("target/test/extracted-bundles/token.txt").exists())
    }
}
