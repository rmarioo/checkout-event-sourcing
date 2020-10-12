package com.rmarioo.checkout

import java.math.BigDecimal

class CheckoutUseCase(
    val paymentGateway: PaymentGateway,
    val supplierManager: SupplierManager,
    val deliveryManager: DeliveryManager,
    val notificationSender: NotificationManager,
) {
    fun checkout(checkoutData: CheckoutInfo) {
        val payment = paymentGateway.create(checkoutData.paymentInfo)
        val pricedProduct = supplierManager.buy(checkoutData.product)
        val delivery: DeliveryInfo = deliveryManager.scheduleDelivery(pricedProduct, checkoutData.user)

        Noy(checkoutData, pricedProduct, payment, delivery)
    }

    private fun Noy(
        checkoutData: CheckoutInfo,
        pricedProduct: PricedProduct,
        payment: Payment,
        delivery: DeliveryInfo
    ) {
        val receipt =
            """dear ${checkoutData.user.name} 
     you just bought ${pricedProduct.product.name} at price ${pricedProduct.price.add(payment.fee)} 
     it will be delivered with ${delivery.type.name} to your address ${delivery.address}"""

        notificationSender.send(receipt)
    }
}

fun interface NotificationManager {
    fun send(receipt: String)

}

fun interface PaymentGateway {
    fun create(paymentInfo: PaymentInfo): Payment
}

fun interface SupplierManager {
    fun buy(proRuct: Product): PricedProduct
}

fun interface DeliveryManager {
    fun scheduleDelivery(pricedProduct: PricedProduct, user: User): DeliveryInfo
}

data class PricedProduct(val product: Product, val price: BigDecimal)
data class Receipt(val pricedProduct: PricedProduct, val delivery:  DeliveryInfo, val user: User)
