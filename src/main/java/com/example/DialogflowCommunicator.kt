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
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements all intent handlers for this Action. Note that your App must extend from DialogflowApp
 * if using Dialogflow or ActionsSdkApp for ActionsSDK based Actions.
 */
class DialogflowCommunicator : DialogflowApp() {
    val sessionManager = SessionManager { ActionHandler() }
    val pizzaMenu = getPizzaMenu()

    @ForIntent("Legg til pizza intent")
    fun addPizza(request: ActionRequest): ActionResponse {
        LOGGER.info("Bestill pizza start")
        val responseBuilder = getResponseBuilder(request)
        val user = request.user
        val handler = sessionManager[request]

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

        val pizzas = handler.addPizza(types, amount, pizzaMenu)
        if (pizzas.isNotEmpty()) {
            val speakList = pizzas
                    .groupBy { it }
                    .map { (pizza, amount) ->
                        "${amount.size} ${pizza.describeChangesToUser()}"
                    }
            responseBuilder.add("Klart det! Jeg har lagt til ${spokenList(speakList)}")
        } else {
            responseBuilder.add("Den pizzaen har jeg ikke hørt om. Hvilke ingredienser vil du ha på pizzaen din?")
        }

        LOGGER.info(responseBuilder.toString())

        sessionManager[request] = handler
        LOGGER.info("Bestill pizza slutt")

        sessionManager[request] = handler
        return responseBuilder.build()
    }

    @ForIntent("List bestilling")
    fun listOrder(request: ActionRequest): ActionResponse {
        val responseBuilder = getResponseBuilder(request)
        val handler = sessionManager[request]

        val outString = StringBuilder("Du har bestilt ${handler.order.pizzas.size} pizza")
        if (handler.order.pizzas.size != 1) outString.append("er")
        outString.append(". ")
        val spokenPizzas = handler.order.pizzas
                .groupBy { it }
                .map { (pizza, amounts) ->
                    "${amounts.size} nr. ${pizza.nr} ${pizza.describeChangesToUser()}"
                }
        outString.append(spokenList(spokenPizzas))

        responseBuilder.add(outString.toString())

        sessionManager[request] = handler
        return responseBuilder.build()
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
        return responseBuilder.build()
    }

    @ForIntent("Fjern ingredients intent")
    fun removeIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Fjern ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val handler = sessionManager[request]

        if (handler.order.pizzas.size > 0) {

            //val rm_i = request.getParameter("rm_i") as List<String>
            val rm_i = request.getParameter("rm_i") as List<String>
            val lastPizza = handler.order.pizzas.last()

            handler.order.changePizza(lastPizza, rm_i, emptyList())

            responseBuilder.add("Fjernet ${spokenList(rm_i)}, fra ${lastPizza.name}\n" +
                    "Den siste pizzaen i ordren din er nå en ${lastPizza.describeChangesToUser()}")
        } else {
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Fjern ingredients slutt")

        sessionManager[request] = handler
        return responseBuilder.build()
    }

    @ForIntent("Legg til ingredients intent")
    fun addIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Legg til ingredients start")
        val responseBuilder = getResponseBuilder(request)

        val handler = sessionManager[request]

        val add_i = request.getParameter("add_i") as List<String>

        if (handler.addIngredient(add_i)) {
            responseBuilder.add("La til ${spokenList(add_i)}. Siste pizza i bestillingen er nå en " +
                    "${handler.order.pizzas.last().describeChangesToUser()}")
        }
        else {
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Legg til ingredients slutt")

        sessionManager[request] = handler
        return responseBuilder.build()
    }

    @ForIntent("Finn pizza intent")
    fun findPizza(request: ActionRequest): ActionResponse {
        LOGGER.info("Finn pizza start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user
        val handler = sessionManager[request]
        val requestedIngredients = request.getParameter("Ingredient") as List<String>

        val pizzaList = handler.findPizza(requestedIngredients, pizzaMenu)

        responseBuilder.add(if (pizzaList.isNotEmpty()) {
            "Hva med " + spokenList(pizzaList.map { it.name })
        } else {
            "Beklager, vi har ingen pizzaer med dette på. Vil du ha noe annet?"
        })

        LOGGER.info("Finn pizza slutt")

        sessionManager[request] = handler
        return responseBuilder.build()
    }

    @ForIntent("Delivery")
    fun delivery(request: ActionRequest): ActionResponse {
        LOGGER.info("Delivery start")
        val responseBuilder = getResponseBuilder(request)

        val handler = sessionManager[request]
        val delivery = request.getParameter("Deliver") as String == "deliver"
        val address = request.getParameter("Address") as String

        handler.setDeliveryAddress(delivery, address)

        if (handler.order.delivery) {
            responseBuilder.add("Pizzaen vil bli levert til ${handler.order.address}")
        } else {
            responseBuilder.add("Pizzan kan hentes hos oss")
        }
        sessionManager[request] = handler
        return responseBuilder.build()
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

        val handler = sessionManager[request]

        if (handler.removePizza(types, amount)) {
            for (i in types.indices) {
                responseBuilder.add("Fjernet " + amount[i] + getPizzaMenu().getPizza(types[i]))
            }
        } else {
            responseBuilder.add("Du har ikke bestilt noen pizzaer enda.")
        }
        sessionManager[request] = handler
        return responseBuilder.build()
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
