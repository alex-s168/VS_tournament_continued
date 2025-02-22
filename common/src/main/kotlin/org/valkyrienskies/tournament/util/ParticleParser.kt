package org.valkyrienskies.tournament.util

import com.mojang.brigadier.StringReader
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import org.valkyrienskies.tournament.util.extension.resLoc

object ParticleParser {
    @Suppress("UNCHECKED_CAST")
    fun parse(part: String): ParticleOptions {
        // TODO: different in 1.20

        val partLoc = part.substringBefore(' ').resLoc()
        val partSettings = part.substringAfter(' ', missingDelimiterValue = "")

        val type = Registry.PARTICLE_TYPE.get(partLoc)!!
        val fn = type.deserializer::fromCommand as ((ParticleType<*>, StringReader) -> Any?)
        val opt = fn(type, StringReader(partSettings)) as ParticleOptions

        return opt
    }
}