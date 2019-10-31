package com.example

fun main() {
    val handler = ActionHandler()
    val pizzaMenu = getPizzaMenu()
    println("Hei!")
    try {
        while (true) {
            println("Hva vil du gjøre? (add, remove, list, change, pizza)")
            when (readLine()!!.trim()) {
                "a", "add" -> {
                    println("Hva skal legges til?")
                    // val items =  readLine()!!.split(",").mapNotNull { pizzaMenu.getPizza(it.toInt()) }
                    val items = readLine()!!.split(",").map { it.toInt() }
                    println(handler.addPizza(items, items.map { 1 }, pizzaMenu))
                }
                "r", "remove" -> {
                    println("Hva skal fjernes?")
                    // val items =  readLine()!!.split(",").mapNotNull { pizzaMenu.getPizza(it.toInt()) }
                    val items = readLine()!!.split(",").map { it.toInt() }
                    println(handler.removePizza(items, items.map { 1 }))
                }
                "l", "list" -> {
                    println(handler.order.toString())
                }
                "c", "change" -> {
                    println(
                            handler.order.pizzas
                                    .mapIndexed { i, pizza -> "${i}\t${pizza.describeChangesToUser()}" }
                                    .joinToString("\n")
                    )
                    println("Hva skal endres?")
                    // val item = readLine()!!.toIntOrNull()
                    TODO("Not implemented yet")
                }
                "p", "pizza" -> println(pizzaMenu.pizzaList.map { "${it.nr}\t${it.name}" }.joinToString("\n"))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    println("Avslutter")
    println("Din bestilling:")
    println(handler.order)
}