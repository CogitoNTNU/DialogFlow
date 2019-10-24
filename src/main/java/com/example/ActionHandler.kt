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



    fun removePizza(order : Order,types : List<Int>, amount : List<Int>): Boolean {

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
        }
        else {
            return false
        }
        return true
    }

}