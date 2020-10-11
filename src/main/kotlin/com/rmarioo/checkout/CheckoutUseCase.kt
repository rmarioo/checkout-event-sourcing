package com.rmarioo.checkout

import java.math.BigDecimal

class CheckoutUseCase(
    val paymentGateway: PaymentGateway,
    val orderManager: OrderManager,
    val deliveryManager: DeliveryManager,
    val notificationSender: NotificationManager,
) {
    fun checkout(checkoutData: CheckoutInfo) {
        val paymentOk = paymentGateway.create(checkoutData.paymentInfo)
        val order = orderManager.create(checkoutData.product)
        val delivery: DeliveryInfo = deliveryManager.scheduleDelivery(order, checkoutData.user)

        val receipt =
            """dear ${checkoutData.user.name} 
 you just bought ${order.product.name} at price ${order.price} 
 it will be delivered with ${delivery.type.name} to your address ${delivery.address}"""

        notificationSender.send(receipt)
    }
}

interface NotificationManager {
    fun send(receipt: String)

}

interface PaymentGateway {
    fun create(paymentInfo: PaymentInfo): Boolean
}

interface OrderManager {
    fun create(product: Product): Order
}

interface DeliveryManager {
    fun scheduleDelivery(order: Order, user: User): DeliveryInfo
}

data class Order(val product: Product, val price: BigDecimal)
data class Receipt(val order: Order,val delivery:  DeliveryInfo ,val user: User)
