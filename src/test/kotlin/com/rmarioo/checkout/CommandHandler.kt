package com.rmarioo.checkout

import com.rmarioo.checkout.Command.Buy
import com.rmarioo.checkout.Command.Pay
import com.rmarioo.checkout.Command.ScheduleDelivery
import com.rmarioo.checkout.Command.SendNotification
import com.rmarioo.checkout.Event.DELIVERED
import com.rmarioo.checkout.Event.NOTIFICATION_CREATED
import com.rmarioo.checkout.Event.PAID
import com.rmarioo.checkout.Event.PURCHASED

class CommandHandler(val eventStore: InMemoryEventStore,
                     val paymentGateway: PaymentGateway,
                     val supplierManager: SupplierManager,
                     val deliveryManager: DeliveryManager,
                     val notificationSender: NotificationManager
) {

    fun handleCommand(command: Command) = when (command) {
        is Pay -> {
            val payment = paymentGateway.create(command.paymentInfo)
            eventStore.addEvent(PAID(payment))
        }
        is Buy -> {
            val pricedProduct = supplierManager.buy(command.product)
            eventStore.addEvent(PURCHASED(pricedProduct))
        }
        is ScheduleDelivery -> {
            val deliveryInfo =
                deliveryManager.scheduleDelivery(command.pricedProduct, command.user)
            eventStore.addEvent(DELIVERED(deliveryInfo))
        }
        is SendNotification -> {
            notificationSender.send(receiptFrom(command))

            eventStore.addEvent(
                NOTIFICATION_CREATED(
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

    private fun receiptFrom(command: SendNotification): String {
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
