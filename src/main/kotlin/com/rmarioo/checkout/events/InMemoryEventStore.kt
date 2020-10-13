package com.rmarioo.checkout.events

class InMemoryEventStore(private var events: MutableList<Event> = mutableListOf()) : EventStore {

    override fun readEvents(): List<Event> = events
    fun addEvent(event: Event) {
        events.add(event)
    }

}
