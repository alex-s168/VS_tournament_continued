package org.valkyrienskies.tournament.item

import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.tournamentItems

class ThrusterUpgrade  : Item(
        Properties().stacksTo(16).tab(tournamentItems.getTab())
){

}