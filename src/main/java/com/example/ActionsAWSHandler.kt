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

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.*

/**
 * Handles request received via AWS - API Gateway [proxy integration](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html)
 * and delegates to your Actions App.
 */
class ActionsAWSHandler : RequestStreamHandler {
    // Replace this with your webhook.
    private val actionsApp = DialogflowReceiver()
    private val parser = JSONParser()

    @Throws(IOException::class)
    override fun handleRequest(inputStream: InputStream,
                               outputStream: OutputStream,
                               context: Context) {
        val reader = BufferedReader(
                InputStreamReader(inputStream))
        val awsResponse = JSONObject()
        val logger = context.logger
        try {
            val awsRequest = parser.parse(reader) as JSONObject
            val headers = awsRequest["headers"] as JSONObject
            val body = awsRequest["body"] as String
            logger.log("AWS request body = $body")

            actionsApp.handleRequest(body, headers)
                    .thenAccept { webhookResponseJson ->
                        logger.log("Generated json = $webhookResponseJson")

                        val responseHeaders = JSONObject()
                        responseHeaders["Content-Type"] = "application/json"

                        awsResponse["statusCode"] = "200"
                        awsResponse["headers"] = responseHeaders
                        awsResponse["body"] = webhookResponseJson
                        writeResponse(outputStream, awsResponse)
                    }.exceptionally { throwable ->
                        awsResponse["statusCode"] = "500"
                        awsResponse["exception"] = throwable
                        writeResponse(outputStream, awsResponse)
                        null
                    }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun writeResponse(outputStream: OutputStream, responseJson: JSONObject) {
        try {
            val writer = OutputStreamWriter(outputStream, "UTF-8")
            writer.write(responseJson.toJSONString())
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
