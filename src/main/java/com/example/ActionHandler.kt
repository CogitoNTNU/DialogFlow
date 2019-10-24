package com.example

class ActionHandler {

    val orderManager = OrderManager()

    fun addPizza(types: List<Int>, amount: List<Int>, pizzaMenu: PizzaMenu, order: Order): List<Pizza> {
        val pizzas: MutableList<Pizza> = mutableListOf()
        for (i in types.indices) {
            val type = types[i]
            for (j in 0 until amount[i]) {
                val pizza = pizzaMenu.getPizza(type)
                if (pizza != null) pizzas.add(pizza)
            }
        }
        order.addPizza(pizzas)
        return pizzas
    }


    fun setDeliveryAddress(deliver: Boolean, address: String, order: Order): Boolean {
        if (deliver) {
            order.deliver(true)
            order.addAddress(address)
            return true
        } else {
            order.deliver(false)
            return false
        }
    }


    fun findPizza(requestedIngredients: List<String>, pizzaMenu: PizzaMenu): List<Pizza> {
        return pizzaMenu.pizzaList
                .sortedByDescending { pizza -> pizza.ingredients.count { it in requestedIngredients } }
                .take(3)
    }


    fun removePizza(order: Order, types: List<Int>, amount: List<Int>): Boolean {

        if (order.pizzas.size > 0) {
            for (pizza: Pizza in order.pizzas) {
                for (i in types.indices) {
                    if (pizza.nr == types[i]) {
                        for (j in 0 until amount[i]) {
                            order.removePizza(pizza)
                        }
                    }
                }
            }
        } else {
            return false
        }
        return true
    }


    fun addIngredient(order : Order, addedIngredients : List<String>): Boolean {

        if(order.pizzas.size > 0) {
            order.changePizza(order.pizzas.last(), emptyList(), addedIngredients)
        }
        else {
            return false
        }
        return true
    }

}