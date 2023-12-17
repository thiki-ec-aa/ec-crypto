package net.thiki.crypt

import java.security.KeyPairGenerator


class Encrypter {

    fun test(){

    }

    fun encrypt(text: String): String {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val pair = generator.generateKeyPair()
        println("public.algorithm" + pair.public.algorithm)
        println("public.format" + pair.public.format)
        println("public.encoded" + pair.public.encoded)
        println("private.algorithm" + pair.private.algorithm)
        println("private.format" + pair.private.format)
        println("private.encoded" + pair.private.encoded)


        return text
    }
}