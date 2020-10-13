package com.rmarioo.checkout.events

interface EventStore {
    fun readEvents(): List<Event>

}
