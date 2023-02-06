package org.valkyrienskies.tournament.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}