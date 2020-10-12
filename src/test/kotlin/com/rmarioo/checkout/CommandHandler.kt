package com.rmarioo.checkout

class CommandHandler(val eventStore: InMemoryEventStore,
                     val paymentGateway: PaymentGateway,
                     val supplierManager: SupplierManager,
                     val deliveryManager: DeliveryManager,
                     val notificationSender: NotificationManager
) {

    fun handleCommand(command: Command) = when (command) {
        is Command.Pay -> {
            val payment = paymentGateway.create(command.paymentInfo)
            eventStore.addEvent(Event.PAID(payment))
        }
        is Command.Buy -> {
            val pricedProduct = supplierManager.buy(command.product)
            eventStore.addEvent(Event.PURCHASED(pricedProduct))
        }
        is Command.ScheduleDelivery -> {
            val deliveryInfo =
                deliveryManager.scheduleDelivery(command.pricedProduct, command.user)
            eventStore.addEvent(Event.DELIVERED(deliveryInfo))
        }
        is Command.SendNotification -> {
            notificationSender.send(receiptFrom(command))

            eventStore.addEvent(
                Event.NOTIFICATION_CREATED(
                    Receipt(
                        command.pricedProduct,
                        command.delivery,
                        command.checkoutData.user
                    )
                )
            )
            eventStore.addEvent(Event.NOTIFICATION_SENT)
        }
    }

    private fun receiptFrom(command: Command.SendNotification): String {
        return """dear ${command.checkoutData.user.name} 
         you just bought ${command.pricedProduct.product.name} at price ${
            command.pricedProduct.price.add(
                command.payment.fee
            )
        } 
         it will be delivered with ${command.delivery.type.name} to your address ${command.delivery.address}"""
    }

    fun handleCommands(commands: List<Command>) {
        commands.forEach {c -> handleCommand(c)}
    }
}
