package com.example

import com.google.actions.api.ActionRequest
import java.util.*

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(request: ActionRequest){
        val order: Order = orderManager[request]
        val delivery = request.getParameter("Deliver") as String
        var address = request.getParameter("Address") as String

        if (delivery == "deliver") {
            order.deliver(true)
            order.addAddress(address)
        } else {
            order.deliver(false)
        }
        return
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