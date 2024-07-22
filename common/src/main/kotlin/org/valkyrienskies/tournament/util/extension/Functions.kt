package org.valkyrienskies.tournament.util.extension

import net.minecraft.resources.ResourceLocation
import java.io.File

fun void() =
    Unit

fun Any?.void() =
    Unit

fun String.toBoolean() =
    when (this.lowercase()) {
        "1",
        "on",
        "true" -> true

        "0",
        "off",
        "false" -> false

        else -> error("Invalid boolean value")
    }

fun String.resLoc() =
    ResourceLocation(this)

fun File.contentsRecOnlyFiles(): Sequence<File> =
    sequence {
        listFiles()?.forEach {
            if (it.isDirectory)
                yieldAll(it.contentsRecOnlyFiles())
            else
                yield(it)
        }
    }