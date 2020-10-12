package com.rmarioo.checkout

interface EventStore {
    fun readEvents(): List<Event>

    fun addEvent(event: Event)

}
