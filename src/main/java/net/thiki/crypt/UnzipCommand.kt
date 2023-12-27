package net.thiki.crypt

import java.util.concurrent.Callable

/**
 * A single unzip tool for the alternative of some old unzip command which is not compatible with zip, in target os.
 */
@CommandLine.Command(name = "unzip", description = ["unzip the zip file."], mixinStandardHelpOptions = true )
class UnzipCommand : Callable<Int> {
    @CommandLine.Option(names = ["--target", "-t"], description = ["bundle folder name"])
    lateinit var target: String

    @CommandLine.Option(names = ["--password", "-p"], arity = "0..1", interactive = true, description = ["if private key file is a zip, need password."])
    lateinit var password: CharArray

    @CommandLine.Option(names = ["--zip-file", "-f"], description = ["zip file name"])
    lateinit var zipFileName: String

    override fun call(): Int {
        FileZipper(zipFileName, password).extractTo(target)
        return 0
    }
}