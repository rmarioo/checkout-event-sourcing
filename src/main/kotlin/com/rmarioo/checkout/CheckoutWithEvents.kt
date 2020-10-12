package com.rmarioo.checkout

import com.rmarioo.checkout.Event.DELIVERED
import com.rmarioo.checkout.Event.NOTIFICATION_CREATED
import com.rmarioo.checkout.Event.NOTIFICATION_SENT
import com.rmarioo.checkout.Event.PAID
import com.rmarioo.checkout.Event.PURCHASED


class CheckoutWithEvents {




}

fun retrieveCurrentState(eventStore: EventStore): CheckoutState {


    val events = eventStore.readEvents()

    val state: CheckoutState = events.fold(CheckoutState.TO_PAY,::onEvent)

    return state
}

fun onEvent(currentState: CheckoutState,event: Event): CheckoutState {

    return when(event)
    {
        is PAID -> when (currentState) {
            is CheckoutState.TO_PAY -> CheckoutState.PAID(event.payment)
            else -> logErrorAndReturn(event, currentState, "PAID")
        }
        is PURCHASED -> CheckoutState.PURCHASED(event.pricedProduct)
        is DELIVERED -> CheckoutState.DELIVERED(event.deliveryInfo)
        is NOTIFICATION_CREATED -> CheckoutState.NOTIFICATION_CREATED(event.receipt)
        is NOTIFICATION_SENT -> CheckoutState.NOTIFICATION_SENT
    }


}

private fun logErrorAndReturn(
    event: Event,
    currentState: CheckoutState,
    expectedEvent: String
): CheckoutState {
    System.err.println("error received event ${event} but not $expectedEvent "); return currentState
}

private fun receivedEvents(): List<Event> {
    TODO("not implemented retrieveEvents") //To change body of created functions use File | Settings | File Templates.
}
