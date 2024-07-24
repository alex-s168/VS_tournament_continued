package org.valkyrienskies.tournament.util

import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

enum class TitleType(val packet: (Component) -> Packet<*>) {
    TITLE(::ClientboundSetTitleTextPacket),
    SUB_TITLE(::ClientboundSetSubtitleTextPacket),
    ACTION_BAR_TEXT(::ClientboundSetActionBarTextPacket),
}

fun ServerLevel.sendTitle(to: Player, type: TitleType, msg: Component) {
    to as ServerPlayer

    to.connection.send(type.packet(msg))
}