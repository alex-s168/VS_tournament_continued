package org.valkyrienskies.tournament.api.debug

import de.m_marvin.univec.impl.Vec3d
import java.awt.Color

abstract class DebugObject

class DebugLine(val a : Vec3d, val b : Vec3d, val color : Color) : DebugObject()

class DebugRect(val a : Vec3d, val b : Vec3d, val color : Color) : DebugObject()

class Debug4CR(val a : Vec3d, val b : Vec3d, val c : Vec3d, val d : Vec3d, val color : Color) : DebugObject()