package net.thiki.crypt

import org.slf4j.LoggerFactory
import java.util.concurrent.Callable


/**
 *
 */
@CommandLine.Command(
    name = "CrypterApp",
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    description = [""],
    subcommands = [
        EncryptCommand::class,
        DecryptCommand::class,
        InitKeysCommand::class,
        UnzipCommand::class,
    ]

)
class CrypterCommand : Callable<Int> {

//    @CommandLine.Parameters(index = "0", description = ["init-keys, encrypt, decrypt"])
//    lateinit var command: String

    init {
        logger.info("initial CrypterApp")
    }
    override fun call(): Int {
        logger.warn("must specify command: init-keys, encrypt, decrypt")
        CommandLine(this).usage(System.out)
        return -1
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CrypterCommand::class.java)
    }

}

