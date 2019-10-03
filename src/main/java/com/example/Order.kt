package com.example

// Data classes for storing drinks and side dishes
data class Extra(val name: String, val price: Double)

class Order
{
    private var pizzas = mutableListOf<Pizza>()
    private var extras = mutableListOf<Extra>()
    var name = ""
    var phone = ""

    fun addPizza(pizza: Pizza) = pizzas.add(pizza)

    fun addPizza(pizza: List<Pizza>) = pizzas.addAll(pizza)

    fun addExtra(side: Extra) = extras.add(side)

    fun addExtra(itemName: String, price: Double) = extras.add(Extra(itemName, price))

    fun changePizza(pizza: Pizza, remove: List<String>, add: List<String>) = pizzas.find { it == pizza }?.change(remove, add)

    fun removePizza(pizza: Pizza) = pizzas.remove(pizza)

    fun removeExtras(item: Extra) = extras.remove(item)

    fun removeExtras(itemName: String) = extras.removeIf { it.name == itemName }
}

