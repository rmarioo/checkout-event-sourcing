package com.rmarioo.checkout.commands

import com.rmarioo.checkout.commands.Command.Buy
import com.rmarioo.checkout.commands.Command.Pay
import com.rmarioo.checkout.commands.Command.ScheduleDelivery
import com.rmarioo.checkout.commands.Command.SendNotification
import com.rmarioo.checkout.DeliveryInfo
import com.rmarioo.checkout.events.Event
import com.rmarioo.checkout.events.Event.DELIVERED
import com.rmarioo.checkout.events.Event.NOTIFICATION_CREATED
import com.rmarioo.checkout.events.Event.PAID
import com.rmarioo.checkout.events.Event.PURCHASED
import com.rmarioo.checkout.Payment
import com.rmarioo.checkout.PaymentInfo
import com.rmarioo.checkout.Product
import com.rmarioo.checkout.User
import com.rmarioo.checkout.events.InMemoryEventStore
import java.math.BigDecimal

class CommandHandler(val eventStore: InMemoryEventStore,
                     val paymentGateway: PaymentGateway,
                     val supplierManager: SupplierManager,
                     val deliveryManager: DeliveryManager,
                     val notificationSender: NotificationManager
) {

    fun handleCommands(commands: List<Command>) {
        commands.forEach {c -> handleCommand(c)}
    }
    
    private fun handleCommand(command: Command) = when (command) {
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
data class Receipt(val pricedProduct: PricedProduct, val delivery: DeliveryInfo, val user: User)

