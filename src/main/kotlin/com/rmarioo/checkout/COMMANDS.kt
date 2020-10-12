package com.rmarioo.checkout



interface Executable {
   fun execute(eventStore: EventStore)
}

sealed class Command : Executable  {

    class Pay(val paymentInfo: PaymentInfo,val paymentGateway: PaymentGateway) : Command() {
        override fun execute(eventStore: EventStore) {
            val payment = paymentGateway.create(paymentInfo)
            eventStore.addEvent(Event.PAID(payment))
        }
    }

    class Buy(val product: Product,val supplierManager: SupplierManager) : Command() {
        override fun execute(eventStore: EventStore) {
            val pricedProduct = supplierManager.buy(product)
            eventStore.addEvent(Event.PURCHASED(pricedProduct))
        }
    }

    class ScheduleDelivery(val pricedProduct: PricedProduct,val user: User,val deliveryManager: DeliveryManager) : Command() {
        override fun execute(eventStore: EventStore) {
            val deliveryInfo = deliveryManager.scheduleDelivery(pricedProduct, user)
            eventStore.addEvent(Event.DELIVERED(deliveryInfo))

        }
    }

    class SendNotification(val checkoutData: CheckoutInfo,
                           val pricedProduct: PricedProduct,
                           val payment: Payment,
                           val delivery: DeliveryInfo,
                           val notificationSender: NotificationManager) : Command() {
        override fun execute(eventStore: EventStore) {
            notificationSender.send(createReceipt())

            eventStore.addEvent(
                Event.NOTIFICATION_CREATED(
                    Receipt(
                        pricedProduct,
                        delivery,
                        checkoutData.user
                    )
                )
            )
            eventStore.addEvent(Event.NOTIFICATION_SENT)
        }

        private fun createReceipt(): String {
            return """dear ${checkoutData.user.name} 
         you just bought ${pricedProduct.product.name} at price ${
                pricedProduct.price.add(
                    payment.fee
                )
            } 
         it will be delivered with ${delivery.type.name} to your address ${delivery.address}"""
        }
    }
}
