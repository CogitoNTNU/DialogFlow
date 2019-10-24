package com.example

import java.util.function.Predicate

enum class Question {
    // TODO does this make sense?
}

interface ConversationHistory {
    fun <T : Any> findCurrentEntity(): T
    fun <T : Any> findEntity(predicate: Predicate<T>): T
    fun <T : Any> findAllEntities(predicate: Predicate<T>): List<T>

    fun addEntity(entity: Any)
    fun addEntities(entity: Collection<Any>)
}
