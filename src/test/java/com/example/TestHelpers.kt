package com.example

import org.json.JSONObject
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Throws(IOException::class)
fun fromFile(fileName: String): String {
    val absolutePath = Paths.get("src", "test", "resources", fileName)
    return String(Files.readAllBytes(absolutePath))
}

fun extractTextToSpeech(responseJson: JSONObject): String {
    val array = responseJson
            .getJSONObject("payload")
            .getJSONObject("google")
            .getJSONObject("richResponse")
            .getJSONArray("items")
    val list = List(array.length()) { i ->
        array
                .optJSONObject(i)
                ?.optJSONObject("simpleResponse")
                ?.optString("textToSpeech")
                ?: ""
    }
    return list.joinToString(".")
}

fun extractConversationData(responseJson: JSONObject): JSONObject {
    val conversationDataStrings = responseJson
            .getJSONArray("outputContexts")
            .getJSONObject(0)
            .getJSONObject("parameters")
            .getString("data")
    return JSONObject(conversationDataStrings)
}