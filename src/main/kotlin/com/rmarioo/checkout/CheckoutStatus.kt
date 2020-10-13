package com.rmarioo.checkout

import com.rmarioo.checkout.commands.PricedProduct
import com.rmarioo.checkout.commands.Receipt

sealed class CheckoutStatus {
    object WISH_LIST: CheckoutStatus()
    data class ORDER(val payment: Payment): CheckoutStatus()
    data class BOOKING(val pricedProduct: PricedProduct): CheckoutStatus()
    data class DELIVERED(val deliveryInfo: DeliveryInfo): CheckoutStatus()
    data class NOTIFICATION_CREATED(val receipt: Receipt): CheckoutStatus()
    object BOOKING_COMPLETED: CheckoutStatus()
}
