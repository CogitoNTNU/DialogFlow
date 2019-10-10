package com.example

import com.google.actions.api.ActionRequest

typealias SessionId = String

const val ORDER_LIFETIME_MILLIS: Long = 10 * 60 * 1000

class OrderManager {
    internal val orders: MutableMap<SessionId, Order> = mutableMapOf()

    private val lastAccess: MutableMap<SessionId, Long> = mutableMapOf()

    operator fun get(session: SessionId): Order {
        lastAccess[session] = System.currentTimeMillis()
        return orders[session] ?: Order()
    }

    operator fun set(session: SessionId, order: Order) {
        orders[session] = order
        lastAccess[session] = System.currentTimeMillis()
    }

    fun remove(session: SessionId) {
        orders.remove(session)
        lastAccess.remove(session)
    }

    fun cleanup() {
        val removeOlderThan = System.currentTimeMillis() - ORDER_LIFETIME_MILLIS
        lastAccess.filterValues { it < removeOlderThan }.keys.forEach(::remove)
    }

    operator fun set(request: ActionRequest, order: Order) = set(request.sessionId!!, order)
    operator fun get(request: ActionRequest) = get(request.sessionId!!)

}
