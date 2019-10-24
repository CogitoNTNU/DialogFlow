/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example

import com.google.actions.api.ActionRequest
import com.google.actions.api.ActionResponse
import com.google.actions.api.DialogflowApp
import com.google.actions.api.ForIntent
import com.google.actions.api.response.ResponseBuilder
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements all intent handlers for this Action. Note that your App must extend from DialogflowApp
 * if using Dialogflow or ActionsSdkApp for ActionsSDK based Actions.
 */
class DialogflowCommunicator : DialogflowApp() {
    val orderManager = OrderManager()
    val actionHandler = ActionHandler();
    val pizzaMenu = getPizzaMenu()

    private fun completeIntent(
            request: ActionRequest,
            order: Order = orderManager[request],
            responseBuilder: ResponseBuilder
    ): ActionResponse {
        // if (askForMore) responseBuilder.add("Skal det være noe mer?")
        orderManager[request] = order
        return responseBuilder.build()
    }

    @ForIntent("Legg til pizza intent")
    fun bestill(request: ActionRequest): ActionResponse {
        LOGGER.info("Bestill pizza start")
        val responseBuilder = getResponseBuilder(request)
        val user = request.user
        val order: Order = orderManager[request]

        var types = (request.getParameter("Type") as List<String>).map { it.toFloat().toInt() }
        var amount = (request.getParameter("Amount") as List<Any>?)
                ?.map { if (it is Int) it else it.toString().toFloat().toInt() }
                ?: emptyList()

        if (amount.size != types.size) {
            amount = mutableListOf()
            for (i in amount.size until types.size) {
                amount.add(1)
            }
        }

        var pizzas: MutableList<Pizza> = mutableListOf()
        var counter = 0
        for (i in types.indices) {
            var type = types[i]
            for (j in 0 until amount[i]) {
                var pizza = pizzaMenu.getPizza(type)
                if (pizza != null) {
                    counter++
                    pizzas.add(pizza)
                }
            }
        }

        if (pizzas.size > 0) {
            order.addPizza(pizzas)
            val speakList = pizzas
                    .groupBy { it }
                    .map { (pizza, amount) ->
                        "${amount.size} ${pizza.describeChangesToUser()}"
                    }
            responseBuilder.add("Klart det! Du har bestilt ${spokenList(speakList)}")
        } else {
            responseBuilder.add("Den pizzaen har jeg ikke hørt om. Hvilke ingredienser vil du ha på pizzaen din?")
        }
        LOGGER.info(responseBuilder.toString())

        orderManager[request] = order
        LOGGER.info("Bestill pizza slutt")

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("List bestilling")
    fun listOrder(request: ActionRequest): ActionResponse {
        val responseBuilder = getResponseBuilder(request)
        val order: Order = orderManager[request]

        val outString = StringBuilder("Du har bestilt ${order.pizzas.size} pizza")
        if (order.pizzas.size != 1) outString.append("er")
        outString.append(". ")
        val spokenPizzas = order.pizzas
                .groupBy { it }
                .map { (pizza, amounts) ->
                    "${amounts.size} nr. ${pizza.nr} ${pizza.describeChangesToUser()}"
                }
        outString.append(spokenList(spokenPizzas))

        responseBuilder.add(outString.toString())

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("Pizza ingredient listing")
    fun listIngredients(request: ActionRequest): ActionResponse {
        val responseBuilder = getResponseBuilder(request)
        val type = request.getParameter("Type").let {
            when (it) {
                is String -> it.toInt()
                is Int -> it
                is Float -> it.toInt()
                is Double -> it.toInt()
                else -> -1
            }
        }
        val pizza = pizzaMenu.getPizza(type)
        if (pizza != null) {
            responseBuilder.add("På ${pizza.name} er det ${spokenList(pizza.ingredients)}")
        } else {
            responseBuilder.add("Den pizzaen har jeg ikke hørt om. Hva vil du ha på pizzaen din?")
        }
        return completeIntent(request = request, responseBuilder = responseBuilder)
    }

    @ForIntent("Fjern ingredients intent")
    fun removeIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Fjern ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val order: Order = orderManager[request]

        if (order.pizzas.size > 0) {

            //val rm_i = request.getParameter("rm_i") as List<String>
            val rm_i = request.getParameter("rm_i") as List<String>
            val lastPizza = order.pizzas.last()

            order.changePizza(lastPizza, rm_i, emptyList())

            responseBuilder.add("Fjernet ${spokenList(rm_i)}, fra ${lastPizza.name}\n" +
                    "Den siste pizzaen i ordren din er nå en ${lastPizza.describeChangesToUser()}")
        } else {
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Fjern ingredients slutt")

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("Legg til ingredients intent")
    fun addIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Legg til ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val order: Order = orderManager[request]

        if (order.pizzas.size > 0) {

            //val rm_i = request.getParameter("rm_i") as List<String>
            val add_i = request.getParameter("add_i") as List<String>

            val lastPizza = order.pizzas.last()
            order.changePizza(lastPizza, emptyList(), add_i)

            responseBuilder.add("La til ${spokenList(add_i)}. Siste pizza i bestillingen er nå en ${lastPizza.describeChangesToUser()}")
        } else {
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Legg til ingredients slutt")

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("Finn pizza intent")
    fun findPizza(request: ActionRequest): ActionResponse {
        LOGGER.info("Finn pizza start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user
        val order: Order = orderManager[request]
        val requestedIngredients = request.getParameter("Ingredient") as List<String>

        responseBuilder.add(actionHandler.findPizza(requestedIngredients, pizzaMenu))

        LOGGER.info("Finn pizza slutt")

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("Delivery")
    fun delivery(request: ActionRequest): ActionResponse {
        LOGGER.info("Delivery start")
        val responseBuilder = getResponseBuilder(request)
        val order: Order = orderManager[request]
        val delivery = actionHandler.delivery(request)

        if(order.delivery == true){
            responseBuilder.add("Pizzaen vil bli levert til ${order.address}")
        } else {
            responseBuilder.add("Pizzan kan hentes hos oss")
        }
        responseBuilder.add(" $delivery")

        return completeIntent(request, order, responseBuilder)
    }

    @ForIntent("Default Welcome Intent")
    fun welcome(request: ActionRequest): ActionResponse {
        LOGGER.info("Welcome intent start.")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        if (user != null && user.lastSeen != null) {
            responseBuilder.add(rb.getString("welcome_back"))
        } else {
            responseBuilder.add(rb.getString("welcome"))
        }

        LOGGER.info("Welcome intent end.")
        return responseBuilder.build()
    }

    @ForIntent("Fjern pizza") //
    fun removePizza(request: ActionRequest): ActionResponse {

        LOGGER.info("Starter fjerning av pizza")

        val responseBuilder = getResponseBuilder(request)

        var types = (request.getParameter("Type") as List<String>).map { it.toFloat().toInt() }
        var amount = (request.getParameter("Amount") as List<Any>?)
                ?.map { if (it is Int) it else it.toString().toFloat().toInt() }
                ?: emptyList()

        val order: Order = orderManager[request]

        if(actionHandler.removePizza(order, types, amount)) {
            for (i in types.indices) {
                responseBuilder.add("Fjernet" + amount[i] + getPizzaMenu().getPizza(types[i]))
            }
        }
        else {
            responseBuilder.add("Du har ikke bestilt noen pizzaer enda.")
        }
        return completeIntent(request,order, responseBuilder)
    }

    @ForIntent("bye")
    fun bye(request: ActionRequest): ActionResponse {
        LOGGER.info("Bye intent start.")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")

        responseBuilder.add(rb.getString("bye")).endConversation()
        LOGGER.info("Bye intent end.")
        return responseBuilder.build()
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(DialogflowCommunicator::class.java)
    }
}
