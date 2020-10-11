package com.rmarioo.checkout

import java.math.BigDecimal

class CheckoutUseCase(
    val paymentGateway: PaymentGateway,
    val supplierManager: SupplierManager,
    val deliveryManager: DeliveryManager,
    val notificationSender: NotificationManager,
) {
    fun checkout(checkoutData: CheckoutInfo) {
        val paymentOk = paymentGateway.create(checkoutData.paymentInfo)
        val pricedProduct = supplierManager.buy(checkoutData.product)
        val delivery: DeliveryInfo = deliveryManager.scheduleDelivery(pricedProduct, checkoutData.user)

        val receipt =
            """dear ${checkoutData.user.name} 
 you just bought ${pricedProduct.product.name} at price ${pricedProduct.price} 
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

interface SupplierManager {
    fun buy(product: Product): PricedProduct
}

interface DeliveryManager {
    fun scheduleDelivery(pricedProduct: PricedProduct, user: User): DeliveryInfo
}

data class PricedProduct(val product: Product, val price: BigDecimal)
data class Receipt(val pricedProduct: PricedProduct, val delivery:  DeliveryInfo, val user: User)
