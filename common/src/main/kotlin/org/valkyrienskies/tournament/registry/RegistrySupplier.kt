package org.valkyrienskies.Tournament.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}