package com.example

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(order: Order, method: String, address: String): String {
        var response = ""

        if (method == "deliver") {
            order.deliver(true)

            order.addAddress(address)
            response += "Pizzaen vil bli levert til $address"
        } else {
            order.deliver(false)
            response += "Pizzaen kan hentes oss hos" // butikk adresse?
        }
        response += " "+ method

        return response
    }



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