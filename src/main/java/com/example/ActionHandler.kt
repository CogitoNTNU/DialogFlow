package com.example

import com.google.actions.api.ActionRequest
import java.util.*

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(request: ActionRequest){

        val rb = ResourceBundle.getBundle("resources")
        val user = request.user
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
}