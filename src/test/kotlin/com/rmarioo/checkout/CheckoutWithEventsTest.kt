package com.rmarioo.checkout

import com.rmarioo.checkout.DeliveryType.STANDARD_DELIVER
import com.rmarioo.checkout.commands.Command.Buy
import com.rmarioo.checkout.commands.Command.Pay
import com.rmarioo.checkout.commands.Command.ScheduleDelivery
import com.rmarioo.checkout.commands.Command.SendNotification
import com.rmarioo.checkout.commands.CommandHandler
import com.rmarioo.checkout.commands.DeliveryManager
import com.rmarioo.checkout.commands.NotificationManager
import com.rmarioo.checkout.commands.PaymentGateway
import com.rmarioo.checkout.commands.PricedProduct
import com.rmarioo.checkout.commands.SupplierManager
import com.rmarioo.checkout.CheckoutStatus.BOOKING_COMPLETED
import com.rmarioo.checkout.CheckoutStatus.BOOKING
import com.rmarioo.checkout.CheckoutStatus.ORDER
import com.rmarioo.checkout.CheckoutStatus.WISH_LIST
import com.rmarioo.checkout.events.EventHandler
import com.rmarioo.checkout.events.InMemoryEventStore
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.math.BigDecimal


class CheckoutWithEventsTest {

    val eventStore = InMemoryEventStore()

    val commandHandler = CommandHandler(eventStore,
        PaymentGateway  { payment },
        SupplierManager { pricedProduct },
        DeliveryManager { pricedProduct, user -> deliveryInfo },
        NotificationManager {})

    val eventHandler = EventHandler()

    @Test
    fun `wishlist`() {

        commandHandler.handleCommands(listOf())

        val currentState = eventHandler.retrieveCurrentState(eventStore)

        assertThat(currentState, `is`(WISH_LIST))
    }

    @Test
    fun `order`() {

        commandHandler.handleCommands(listOf(Pay(paymentInfo)))

        val currentState = eventHandler.retrieveCurrentState(eventStore)

        assertThat(currentState, `is`(ORDER(payment)))
    }

    @Test
    fun `booking`() {

        commandHandler.handleCommands(listOf(
            Pay(paymentInfo),
            Buy(product))
        )

        val currentState = eventHandler.retrieveCurrentState(eventStore)

        assertThat(currentState, `is`(BOOKING(pricedProduct)))
    }

    @Test
    fun `booking with all post process completed`() {

        commandHandler.handleCommands(
            listOf(
                Pay(paymentInfo)
                , Buy(product)
                , ScheduleDelivery(pricedProduct, user)
                , SendNotification(checkoutData, pricedProduct, payment, deliveryInfo)
            )
        )

        val currentState = eventHandler.retrieveCurrentState(eventStore)

        assertThat(currentState, `is`(BOOKING_COMPLETED))
    }



    val product = Product("Pizza")
    val payment = Payment(BigDecimal.TEN)
    val price = BigDecimal.ONE
    val pricedProduct = PricedProduct(product, price)
    val user = User("Mario", "Napoli")
    val paymentInfo = PaymentInfo("creditCard123456")
    val checkoutData = CheckoutInfo(user, paymentInfo, product)

    val deliveryInfo = DeliveryInfo(product.name, user.address, STANDARD_DELIVER)

}

