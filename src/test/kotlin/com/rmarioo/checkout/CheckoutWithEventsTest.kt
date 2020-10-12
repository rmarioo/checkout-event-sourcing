package com.rmarioo.checkout

import com.rmarioo.checkout.CheckoutState.NOTIFICATION_SENT
import com.rmarioo.checkout.Command.Buy
import com.rmarioo.checkout.Command.Pay
import com.rmarioo.checkout.Command.ScheduleDelivery
import com.rmarioo.checkout.Command.SendNotification
import com.rmarioo.checkout.DeliveryType.STANDARD_DELIVER
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.math.BigDecimal



class CheckoutWithEventsTest {

    val product = Product("Pizza")
    val payment = Payment(BigDecimal.TEN)
    val price = BigDecimal.ONE
    val pricedProduct = PricedProduct(product, price)
    val user = User("Mario", "Napoli")
    val paymentInfo = PaymentInfo("creditCard123456")
    val checkoutData = CheckoutInfo(user, paymentInfo, product)

    val deliveryInfo = DeliveryInfo(product.name, user.address, STANDARD_DELIVER)

    val paymentGateway  = PaymentGateway  { payment }
    val supplierManager = SupplierManager { pricedProduct }
    val deliveryManager = DeliveryManager { pricedProduct, user -> deliveryInfo }
    val notificationSender = NotificationManager {}

    val eventStore = InMemoryEventStore()

    val commandHandler = CommandHandler(eventStore, paymentGateway,supplierManager,deliveryManager,notificationSender)

    @Test
    fun handleCommands() {

        val commands: List<Command> = listOf(
            Pay(paymentInfo)
            , Buy(product)
            , ScheduleDelivery(pricedProduct, user)
            , SendNotification(checkoutData,pricedProduct,payment,deliveryInfo)
        )

        commandHandler.handleCommands(commands)

        val currentState = retrieveCurrentState(eventStore)

        assertThat(currentState, `is`(NOTIFICATION_SENT))
    }
}

