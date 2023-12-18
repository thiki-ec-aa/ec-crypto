package net.thiki.crypt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("manual test")
class CrypterTest{


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
}