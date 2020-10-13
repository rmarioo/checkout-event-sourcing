package com.rmarioo.checkout.events

import com.rmarioo.checkout.DeliveryInfo
import com.rmarioo.checkout.Payment
import com.rmarioo.checkout.commands.PricedProduct
import com.rmarioo.checkout.commands.Receipt

sealed class Event {
    class PAID(val payment: Payment): Event()
    class PURCHASED(val pricedProduct: PricedProduct): Event()
    class DELIVERED(val deliveryInfo: DeliveryInfo): Event()
    class NOTIFICATION_CREATED(val receipt: Receipt): Event()
    object NOTIFICATION_SENT: Event()
}

sealed class CheckoutState {
    object WISH_LIST: CheckoutState()
    data class ORDER(val payment: Payment): CheckoutState()
    data class BOOKING(val pricedProduct: PricedProduct): CheckoutState()
    data class DELIVERED(val deliveryInfo: DeliveryInfo): CheckoutState()
    data class NOTIFICATION_CREATED(val receipt: Receipt): CheckoutState()
    object BOOKING_COMPLETED: CheckoutState()
}
