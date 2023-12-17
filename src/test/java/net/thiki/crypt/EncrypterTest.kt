package net.thiki.crypt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.*
import javax.crypto.Cipher





@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EncrypterTest{

    fun testZip(){

    }

    @Test
    fun testEncryptAndDecrypt() {

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

        //encrypt
        val publ = pair.public

        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l-$t"


        println("token:$token")
        val encryptCipher = Cipher.getInstance("RSA")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publ)
        val encryptedBA = encryptCipher.doFinal(token.toByteArray(StandardCharsets.UTF_8))

        val encodedMessage: String = Base64.getEncoder().encodeToString(encryptedBA)
        //save and send the encodedMessage
        println("encodedMessage:$encodedMessage")


        // decrypt
        val priv = pair.private

        val decodedBA = Base64.getDecoder().decode(encodedMessage)
        assertEquals(encryptedBA.toList(), decodedBA.toList())

        val decryptCipher = Cipher.getInstance("RSA")
        decryptCipher.init(Cipher.DECRYPT_MODE, priv)
        val decryptedMsg = decryptCipher.doFinal(decodedBA).toString(StandardCharsets.UTF_8)

        val split = decryptedMsg.split("-")

        assertEquals(2, split.size)
        assertEquals(l, split[0].toLong())
        assertEquals(t, split[1].toLong())

    }

    @Test
    fun testBase64EncodedMessage(){
        val r = java.security.SecureRandom()
        val l = r.nextLong()
        val t = Instant.now().toEpochMilli()
        val token: String = "$l-$t"

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