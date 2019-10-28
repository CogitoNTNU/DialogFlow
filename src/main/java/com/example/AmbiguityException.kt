package com.example

import kotlin.reflect.KClass

/**
 * Thrown whenever it's not clear which entity the user is referring to.
 * NOT thrown when we have enough information, but for some reason can't process it.
 */
class AmbiguityException(unresolvedEntity: KClass<*>) :
        RuntimeException("Cannot figure out which ${unresolvedEntity.simpleName} the user is talking about") {
    val userFacingEntityType:String = when(unresolvedEntity.simpleName) {
        "Pizza" -> "pizza"
        // TODO Add more stuff here
        else -> "ting (${unresolvedEntity.qualifiedName})"
    }
}
