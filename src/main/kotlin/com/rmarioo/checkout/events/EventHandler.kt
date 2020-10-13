package com.rmarioo.checkout.events

import com.rmarioo.checkout.CheckoutStatus
import com.rmarioo.checkout.events.Event.DELIVERED
import com.rmarioo.checkout.events.Event.NOTIFICATION_CREATED
import com.rmarioo.checkout.events.Event.NOTIFICATION_SENT
import com.rmarioo.checkout.events.Event.PAID
import com.rmarioo.checkout.events.Event.PURCHASED


class EventHandler {

    fun retrieveCurrentState(eventStore: EventStore): CheckoutStatus {

        val events = eventStore.readEvents()

        val status: CheckoutStatus = events.fold(CheckoutStatus.WISH_LIST,::onEvent)

        return status
    }

    private fun onEvent(currentStatus: CheckoutStatus, event: Event): CheckoutStatus {

        return when(event)
        {
            is PAID -> when (currentStatus) {
                is CheckoutStatus.WISH_LIST -> CheckoutStatus.ORDER(event.payment)
                else -> logErrorAndReturn(event, currentStatus, "PAID")
            }
            is PURCHASED -> CheckoutStatus.BOOKING(event.pricedProduct)
            is DELIVERED -> CheckoutStatus.DELIVERED(event.deliveryInfo)
            is NOTIFICATION_CREATED -> CheckoutStatus.NOTIFICATION_CREATED(event.receipt)
            is NOTIFICATION_SENT -> CheckoutStatus.BOOKING_COMPLETED
        }


    }

    private fun logErrorAndReturn(
        event: Event,
        currentStatus: CheckoutStatus,
        expectedEvent: String
    ): CheckoutStatus {
        System.err.println("error received event ${event} but not $expectedEvent "); return currentStatus
    }
}


