package net.thiki.crypt

import java.util.concurrent.Callable

@CommandLine.Command(name = "init-keys", description = ["initial the key pair."], mixinStandardHelpOptions = true )
class InitKeysCommand : Callable<Int> {
    @CommandLine.Option(
        names = ["--public-key", "-pu"],
        defaultValue = "public.key",
        description = ["public key file"]
    )
    lateinit var publicKeyFile: String

    @CommandLine.Option(
        names = ["--private-key", "-pr"],
        defaultValue = "private.key",
        description = ["private key file"]
    )
    lateinit var privateKeyFile: String

    @CommandLine.Option(
        names = ["--password"],
        arity = "0..1",
        interactive = true,
        defaultValue = "",
        description = ["password to encrypt the private key file"]
    )
    lateinit var password: CharArray

    override fun call(): Int {
        val crypter = Crypter()
        crypter.initKeys(publicKeyFile, privateKeyFile, password)
        return 0
    }
}