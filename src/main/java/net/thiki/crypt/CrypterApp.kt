package net.thiki.crypt

import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = exitProcess(CommandLine(CrypterCommand()).execute(*args))
