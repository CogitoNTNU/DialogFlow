package com.example

import com.beust.klaxon.Klaxon

// Data classes for storing drinks and side dishes
data class Extra(val name: String, val price: Double)

data class Order(var pizzas: MutableList<Pizza> = mutableListOf(),
                 var extras: MutableList<Extra> = mutableListOf(),
                 var name: String = "",
                 var phone: String = "",
                 var delivery: Boolean = false,
                 var address: String = "") {

    fun addPizza(pizza: Pizza) = pizzas.add(pizza)

    fun addPizza(pizza: List<Pizza>) = pizzas.addAll(pizza)

    fun addExtra(side: Extra) = extras.add(side)

    fun addExtra(itemName: String, price: Double) = extras.add(Extra(itemName, price))

    fun changePizza(pizza: Pizza, remove: List<String>, add: List<String>) = pizzas.find { it == pizza }?.change(remove, add)

    fun removePizza(pizza: Pizza) = pizzas.remove(pizza)

    fun removeExtras(item: Extra) = extras.remove(item)

    fun removeExtras(itemName: String) = extras.removeIf { it.name == itemName }

    fun deliver(status: Boolean) {delivery = status}

    fun addAddress(address_: String) {address = address_}

    fun toJson(): String = Klaxon().toJsonString(this)
    fun toMap(): Map<String, Any> = mapOf("order" to toJson())

    companion object {
        fun fromJson(string: String): Order? = Klaxon().parse<Order>(string)
        fun fromMap(map: MutableMap<String, Any>): Order {
            return if ("order" in map) {
                fromJson(map["order"] as String) ?: Order()
            } else Order()
        }
    }
}

