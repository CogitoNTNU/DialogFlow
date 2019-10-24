package com.example

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(order: Order, method: String, address: String){
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

}