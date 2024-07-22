package org.valkyrienskies.tournament

import net.minecraft.world.item.ItemStack
import dev.architectury.platform.Platform
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.tournament.util.ParticleParser
import org.valkyrienskies.tournament.util.extension.contentsRecOnlyFiles
import org.valkyrienskies.tournament.util.extension.resLoc
import java.io.File
import java.io.Reader
import java.util.Properties
import kotlin.math.round

data class FuelType(
    // when off
    val standbyBurnRate: Float,

    // when on
    val baseBurnRate: Float,
    val burnRatePerThrottle: Float,

    // when on
    val basePower: Float,
    val powerPerThrottle: Float,

    val particles: ParticleOptions?,
    val particleVelocity: Float,
    val particleSpread: Float,
    val particleCount: Int,
) {

    fun write(p: Properties) {
        p.setProperty("standbyBurnRate", standbyBurnRate.toString())
        p.setProperty("baseBurnRate", baseBurnRate.toString())
        p.setProperty("burnRatePerThrottle", burnRatePerThrottle.toString())
        p.setProperty("basePower", basePower.toString())
        p.setProperty("powerPerThrottle", powerPerThrottle.toString())
        p.setProperty("particles", particles?.writeToString() ?: "")
        p.setProperty("particleVelocity", particleVelocity.toString())
        p.setProperty("particleSpread", particleSpread.toString())
        p.setProperty("particleCount", particleCount.toString())
    }

    fun calcBurnRate(throttle: Float) =
        if (throttle == 0.0f)
            standbyBurnRate
        else
            baseBurnRate + burnRatePerThrottle * throttle

    fun calcPower(throttle: Float) =
        basePower + powerPerThrottle * throttle

    companion object {
        fun read(cfg: Properties) =
            FuelType(
                standbyBurnRate = cfg.getProperty("standbyBurnRate", "0").toFloat(),
                baseBurnRate = cfg.getProperty("baseBurnRate", "0").toFloat(),
                burnRatePerThrottle  = cfg.getProperty("burnRatePerThrottle").toFloat(),
                basePower = cfg.getProperty("basePower", "0").toFloat(),
                powerPerThrottle = cfg.getProperty("powerPerThrottle").toFloat(),
                particles = cfg.getProperty("particleType", "").let {
                    if (it.isEmpty()) null
                    else ParticleParser.parse(it)
                },
                particleVelocity = cfg.getProperty("particleVelocity", "0.4").toFloat(),
                particleSpread = cfg.getProperty("particleSpread", "0.0").toFloat(),
                particleCount = cfg.getProperty("particleCount", "1.0").toFloat().toInt(),
            )
    }

}

object TournamentFuelManager {
    private val fuelConfigDir: File = Platform.getConfigFolder()
        .resolveSibling("vs_tournament")
        .resolveSibling("fuels")
        .toFile()

    val fuels = mutableMapOf<ResourceLocation, FuelType>()

    fun getKey(fuelType: FuelType?): ResourceLocation? =
        fuelType?.let { ty ->
            fuels
            .filterValues { it == ty }
            .keys
            .firstOrNull()
        }

    init {
        fuelConfigDir.mkdirs()
        val noupdate = File(fuelConfigDir, "noupdate")
        if (!noupdate.exists()) {
            println("[Tournament] updating fuels ; create ${noupdate.absolutePath} if you want to prevent this in the future")
            defaultFuels.forEach { (k, v) ->
                val file = File(fuelConfigDir, "$k.properties")

                val cfg = Properties()
                cfg.setProperty("create", "true")
                cfg.setProperty("path", "${TournamentMod.MOD_ID}:$k")
                v.write(cfg)
                cfg.store(file.writer(), "autogenerated by tournament")
            }
        }
    }

    fun register(path: String, reader: Reader) {
        try {
            val cfg = Properties()
            cfg.load(reader)

            val create = cfg.getProperty("create").toBoolean()
            val path = cfg.getProperty("path").resLoc().also {
                if (create)
                    require(it.namespace == TournamentMod.MOD_ID) {
                        "registration of custom fuel only allowed in namespace: ${TournamentMod.MOD_ID}"
                    }
            }
            val settings = FuelType.read(cfg)

            if (create)
                TournamentItems.fuelItems.add(path.path to settings)

            fuels[path] = settings
        } catch (e: Exception) {
            println("[Tournament] failed to register fuel $path:")
            e.printStackTrace()
        }
    }

    fun register(file: File) {
        if (file.name == "noupdate") return
        register(file.absolutePath, file.bufferedReader())
    }

    fun registerDirRec(dir: File) {
        dir.contentsRecOnlyFiles()
            .forEach(::register)
    }

    fun registerTournamentConfigDir() {
        registerDirRec(fuelConfigDir)
    }

    init {
        TournamentEvents.itemHoverText.on { (stack, _, tooltipComponents, _) ->
            val fuel = stack.tournamentFuel() ?: return@on

            fun num(num: Float): String =
                (round(num * 1_000_000f) / 1_000_000f).toString()

            fun tc(key: String, vararg args: Any, styleMod: (Style) -> Style = { it }): MutableComponent =
                TranslatableComponent("tooltip.vs_tournament.fuel.$key", *args)
                    .withStyle(styleMod)

            fun t(key: String, vararg args: Any, styleMod: (Style) -> Style = { it }) {
                tooltipComponents += tc(key, *args, styleMod)
            }

            fun separator() {
                tooltipComponents += TextComponent("")
            }

            // TODO: merge with vs mass tooltips code (also has pounds cfg and conversion)

            t("title") { it.withUnderlined(true) }

            separator()

            t("standbyBurnRate", num(fuel.standbyBurnRate), num(fuel.standbyBurnRate * 20))
            t("baseBurnRate", num(fuel.baseBurnRate), num(fuel.baseBurnRate * 20))
            t("burnRatePerThrottle", num(fuel.burnRatePerThrottle), num(fuel.burnRatePerThrottle * 20)) {
                it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, tc("infoThrottle")))
            }

            separator()

            t("basePower", num(fuel.basePower))
            t("powerPerThrottle", num(fuel.powerPerThrottle)) {
                it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, tc("infoThrottle")))
            }
        }
    }
}

fun ItemStack.tournamentFuel(): FuelType? =
    TournamentFuelManager.fuels[Registry.ITEM.getKey(item)]

private val defaultFuels = mapOf(
    "basic_fuel_powder" to FuelType(
        standbyBurnRate = 0.00001f,
        baseBurnRate = 0.001f,
        burnRatePerThrottle = 0.002f,
        basePower = 0.33f,
        powerPerThrottle = 666.666f,
        particles = ParticleTypes.ASH,
        particleCount = 2,
        particleSpread = 0f,
        particleVelocity = 0.2f,
    ),

    "basic_fuel_paste" to FuelType(
        standbyBurnRate = 0.00001f,
        baseBurnRate = 0.0002f,
        burnRatePerThrottle = 0.0017f,
        basePower = 0.34f,
        powerPerThrottle = 777f,
        particles = ParticleTypes.ASH,
        particleCount = 2,
        particleSpread = 0f,
        particleVelocity = 0.23f,
    ),
)