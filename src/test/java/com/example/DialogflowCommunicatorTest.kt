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

import com.google.actions.api.test.MockRequestBuilder
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class DialogflowCommunicatorTest {

    @Throws(IOException::class)
    private fun fromFile(fileName: String): String {
        val absolutePath = Paths.get("src", "test", "resources", fileName)
        return String(Files.readAllBytes(absolutePath))
    }

    @Test
    @Throws(Exception::class)
    fun testWelcomeUsingRawRequest() {
        val app = DialogflowCommunicator()
        val requestBody = fromFile("request_welcome.json")
        val expectedResponse = fromFile("response_welcome.json")

        val future = app.handleRequest(requestBody, null /* headers */)

        val responseJson = future.get()
        Assert.assertEquals(expectedResponse, responseJson)
    }

    @Test
    fun testWelcomeUsingMockRequestBuilder() {
        val app = DialogflowCommunicator()
        val rb = MockRequestBuilder.welcome("welcome", true)
        val request = rb.build()

        val response = app.welcome(request)
        Assert.assertTrue(response.expectUserResponse!!)
        Assert.assertEquals(1, response.richResponse!!.items.size)
    }

    @Test
    fun testBye() {
        val app = DialogflowCommunicator()
        val rb = MockRequestBuilder()
        rb.setIntent("bye")
        rb.setUsesDialogflow(true)

        val request = rb.build()
        val response = app.bye(request)

        assertFalse(response.expectUserResponse!!)
        Assert.assertEquals(1, response.richResponse!!.items.size)
    }
}
