package com.rmarioo.checkout.events

import com.rmarioo.checkout.events.Event.DELIVERED
import com.rmarioo.checkout.events.Event.NOTIFICATION_CREATED
import com.rmarioo.checkout.events.Event.NOTIFICATION_SENT
import com.rmarioo.checkout.events.Event.PAID
import com.rmarioo.checkout.events.Event.PURCHASED


class EventHandler {

    fun retrieveCurrentState(eventStore: EventStore): CheckoutState {

        val events = eventStore.readEvents()

        val state: CheckoutState = events.fold(CheckoutState.WISH_LIST,::onEvent)

        return state
    }

    private fun onEvent(currentState: CheckoutState, event: Event): CheckoutState {

        return when(event)
        {
            is PAID -> when (currentState) {
                is CheckoutState.WISH_LIST -> CheckoutState.ORDER(event.payment)
                else -> logErrorAndReturn(event, currentState, "PAID")
            }
            is PURCHASED -> CheckoutState.BOOKING(event.pricedProduct)
            is DELIVERED -> CheckoutState.DELIVERED(event.deliveryInfo)
            is NOTIFICATION_CREATED -> CheckoutState.NOTIFICATION_CREATED(event.receipt)
            is NOTIFICATION_SENT -> CheckoutState.BOOKING_COMPLETED
        }


    }

    private fun logErrorAndReturn(
        event: Event,
        currentState: CheckoutState,
        expectedEvent: String
    ): CheckoutState {
        System.err.println("error received event ${event} but not $expectedEvent "); return currentState
    }
}


