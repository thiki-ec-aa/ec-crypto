package net.thiki.crypt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("This test is for PoC only.")
class PoCTest{

    @Test
    fun testZipCommand(){
        val processBuilder = ProcessBuilder()
        val p = "hello kitty"
        // 7z a -r -tzip -Paaa hxlc-cone$(date +%Y-%m-%d).zip * -xr\!.git -x\!ar.sh
        processBuilder.command("c:\\Program Files\\7-Zip\\7z", "a", "-r", "-tzip",  "-P$p", "bundles.zip", "src/resource/bundles")
//        processBuilder.command("zip","-r", "-P '$p'", "bundles.zip", "src/resource/bundles")

        try {

            val process: Process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line = reader.readText();
            println(line)

            val exitVal = process.waitFor()
            if (exitVal == 0) {
                println("Success!")
                //exitProcess(0)
            } else {
                //else...
                println("Failed!")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @Test
    fun testFile(){

        // init the key pair
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val pair = generator.generateKeyPair()

        println("public.algorithm:" + pair.public.algorithm)
        println("public.format:" + pair.public.format)
        println("public.encoded:" + java.lang.String(pair.public.encoded, "UTF-8"))
        println("private.algorithm:" + pair.private.algorithm)
        println("private.format:" + pair.private.format)
        println("private.encoded:" + java.lang.String(pair.private.encoded, "UTF-8"))

        FileOutputStream("public.key").use { fos -> fos.write(pair.public.encoded) }
        FileOutputStream("private.key").use { fos -> fos.write(pair.private.encoded) }
        val cut = Crypter()
        val publicKeyInFile = cut.readKeyFromFile("public.key")
        assertEquals(pair.public.encoded.toList(), publicKeyInFile.encoded.toList())
        val privateKeyInFile = cut.readKeyFromFile("private.key", false)
        assertEquals(pair.private.encoded.toList(), privateKeyInFile.encoded.toList())
    }
    @Test
    fun testInitKeys() {
        val cut = Crypter()
        cut.initKeys()
    }

    @Test
    fun testEncryptAndDecrypt() {

        val cut = Crypter()
        // init the key pair
//        cut.initKeys()

        //encrypt
        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l:$t"
        println("token:$token")

        val encodedMessage = cut.encrypt(token, "public.key")
        println("encodedMessage:$encodedMessage")

        // decrypt
        val decryptedMsg = cut.decrypt(encodedMessage, "private.key")
        val split = decryptedMsg.split(":")
        assertEquals(2, split.size)
        assertEquals(l, split[0].toLong())
        assertEquals(t, split[1].toLong())

    }

    @Test
    fun testBase64EncodedMessage(){
        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l:$t"

        println("token:$token")
        val tokenBA = token.toByteArray(StandardCharsets.UTF_8)
        val encodedMessage: String = Base64.getEncoder().encodeToString(tokenBA)

        println("encodedMessage:$encodedMessage")

        val decodedBA = Base64.getDecoder().decode(encodedMessage)
        assertEquals(tokenBA.asList(), decodedBA.asList())

        val decodedStr = decodedBA.toString(StandardCharsets.UTF_8)
        println("decodedStr:$decodedStr")

        assertEquals(token, decodedStr)

    }
}