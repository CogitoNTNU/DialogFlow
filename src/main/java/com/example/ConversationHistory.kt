package com.example

class ConversationHistory<S:Any> {
    /**
     * Do not use directly!
     */
    val stack = mutableListOf<S>()

    /**
     * Returns all items of the specified type, in the order they were mentioned.
     * Most recently mentioned last.
     */
    inline fun <reified T : S> findAllOfType(): List<T> = stack.filterIsInstance<T>()

    /**
     * Most recently mentioned item of the given type
     */
    inline fun <reified T : S> findCurrentEntity(): T = stack.last { it is T } as T

    /**
     * Find the most recently mentioned entity matching the given predicate, if it exists
     */
    inline fun <reified T : S> findEntity(predicate: (T)->Boolean): T? =
            findAllOfType<T>().findLast(predicate)

    /**
     * Find all entities matching the given predicate.
     * Most recently mentioned last.
     */
    inline fun <reified T : S> findAllEntities(predicate: (T)->Boolean): List<T> = findAllOfType<T>().filter(predicate)

    /**
     * Add the given entity
     */
    fun <T:S>add(entity: T) :T {
        stack.add(entity)
        return entity
    }

    /**
     * Add the given entities. Most recently mentioned last.
     */
    fun <T:S>addAll(entities: Collection<T>) :Collection<T>{
        stack.addAll(entities)
        return entities
    }
}