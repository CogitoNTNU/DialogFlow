package com.example

import com.google.actions.api.ActionRequest

typealias SessionId = String

const val ORDER_LIFETIME_MILLIS: Long = 10 * 60 * 1000

class SessionManager<T>(private val create: () -> T) {
    internal val items: MutableMap<SessionId, T> = mutableMapOf()

    private val lastAccess: MutableMap<SessionId, Long> = mutableMapOf()

    operator fun get(session: SessionId): T {
        lastAccess[session] = System.currentTimeMillis()
        return items[session] ?: create()
    }

    operator fun set(session: SessionId, item: T) {
        items[session] = item
        lastAccess[session] = System.currentTimeMillis()
    }

    fun remove(session: SessionId) {
        items.remove(session)
        lastAccess.remove(session)
    }

    fun cleanup() {
        val removeOlderThan = System.currentTimeMillis() - ORDER_LIFETIME_MILLIS
        lastAccess.filterValues { it < removeOlderThan }.keys.forEach(::remove)
    }

    operator fun set(request: ActionRequest, item: T) = set(request.sessionId!!, item)
    operator fun get(request: ActionRequest) = get(request.sessionId!!)
    fun getAll() = items.toMap()

}
