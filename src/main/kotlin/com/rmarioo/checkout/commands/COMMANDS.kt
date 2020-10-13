package com.rmarioo.checkout.commands

import com.rmarioo.checkout.CheckoutInfo
import com.rmarioo.checkout.DeliveryInfo
import com.rmarioo.checkout.Payment
import com.rmarioo.checkout.PaymentInfo
import com.rmarioo.checkout.Product
import com.rmarioo.checkout.User


sealed class Command  {
    class Pay(val paymentInfo: PaymentInfo) : Command()
    class Buy(val product: Product) : Command()
    class ScheduleDelivery(val pricedProduct: PricedProduct, val user: User) : Command()
    class SendNotification(val checkoutData: CheckoutInfo,
                           val pricedProduct: PricedProduct,
                           val payment: Payment,
                           val delivery: DeliveryInfo
    ) : Command()
}
