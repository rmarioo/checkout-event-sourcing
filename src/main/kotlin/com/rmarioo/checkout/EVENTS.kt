package com.rmarioo.checkout

sealed class Event {
    class PAID(val payment: Payment): Event()
    class PURCHASED(val pricedProduct: PricedProduct): Event()
    class DELIVERED(val deliveryInfo: DeliveryInfo): Event()
    class NOTIFICATION_CREATED(val receipt: Receipt): Event()
    object NOTIFICATION_SENT: Event()
}

sealed class CheckoutState {
    object TO_PAY: CheckoutState()
    data class PAID(val payment: Payment): CheckoutState()
    data class PURCHASED(val pricedProduct: PricedProduct): CheckoutState()
    data class DELIVERED(val deliveryInfo: DeliveryInfo): CheckoutState()
    data class NOTIFICATION_CREATED(val receipt: Receipt): CheckoutState()
    object NOTIFICATION_SENT: CheckoutState()
}
