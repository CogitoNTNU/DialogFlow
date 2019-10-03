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
import getPizzaMenu
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements all intent handlers for this Action. Note that your App must extend from DialogflowApp
 * if using Dialogflow or ActionsSdkApp for ActionsSDK based Actions.
 */
class MyActionsApp : DialogflowApp() {

    val pizzaMenu = getPizzaMenu()
    val order = Order()

    @ForIntent("Legg til pizza intent")
    fun bestill(request: ActionRequest): ActionResponse {
        LOGGER.info("Bestill pizza start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val type = request.getParameter("Type").toString().toInt()

        var pizza = pizzaMenu.getPizza(type)
        if(pizza != null)
        {
            order.addPizza(pizza)
        }
        else
        {
            // pizza finnes ikke
        }

        responseBuilder.add("Brukeren ville ha $type, og får ${pizza?.name}")

        LOGGER.info("Bestill pizza slutt")
        return responseBuilder.build()
    }

    @ForIntent("Fjern ingredients intent")
    fun remove_ingredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Fjern ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val rm_i = request.getParameter("rm_i").toString()

        responseBuilder.add("Removed $rm_i, u little B*tch")

        LOGGER.info("Fjern ingredients slutt")
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

        private val LOGGER = LoggerFactory.getLogger(MyActionsApp::class.java)
    }
}
