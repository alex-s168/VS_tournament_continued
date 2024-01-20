package org.valkyrienskies.tournament.storage

val NBTInt = NBTSerializer.new(
    write = { nbt, i -> nbt.putInt("value", i) },
    read = { nbt -> nbt.getInt("value") }
)

val NBTLong = NBTSerializer.new(
    write = { nbt, i -> nbt.putLong("value", i) },
    read = { nbt -> nbt.getLong("value") }
)

val NBTFloat = NBTSerializer.new(
    write = { nbt, i -> nbt.putFloat("value", i) },
    read = { nbt -> nbt.getFloat("value") }
)

val NBTDouble = NBTSerializer.new(
    write = { nbt, i -> nbt.putDouble("value", i) },
    read = { nbt -> nbt.getDouble("value") }
)

val NBTBool = NBTSerializer.new(
    write = { nbt, i -> nbt.putBoolean("value", i) },
    read = { nbt -> nbt.getBoolean("value") }
)

val NBTString = NBTSerializer.new(
    write = { nbt, i -> nbt.putString("value", i) },
    read = { nbt -> nbt.getString("value") }
)