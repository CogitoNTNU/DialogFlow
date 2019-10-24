package com.example

class ActionHandler {


    fun removePizza(types : IntArray, amount : IntArray): String {

        var response  = ""

        val order: Order = orderManager[request]

        if (order.pizzas.size > 0) {
            for (pizza: Pizza in order.pizzas) {
                for (i in types.indices) {
                    if (pizza.nr == types[i]) {
                        for (j in 0 until amount[i]) {
                            order.removePizza(pizza)
                        }
                        response += "Fjernet" + amount[i] + pizza.name
                    }
                }
            }
        }
        else {
            response = "Du har ikke bestilt noen pizzaer enda."
        }
        return response
    }

}