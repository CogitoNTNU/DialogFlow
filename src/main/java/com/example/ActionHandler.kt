package com.example

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(order: Order, method: String, address: String){
        if (method == "deliver") {
            order.deliver(true)
            order.addAddress(address)
        } else {
            order.deliver(false)
        }
        return
    }
}