package com.rmarioo.checkout

import com.rmarioo.checkout.DeliveryType.STANDARD_DELIVER
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal.TEN


class CheckoutUseCaseTest
{

    val paymentGateway  = mockk<PaymentGateway>()
    val orderManager  = mockk<OrderManager>()
    val deliveryManager  = mockk<DeliveryManager>()
    val notificationManager  = mockk<NotificationManager>()

    val checkoutUseCase = CheckoutUseCase(paymentGateway, orderManager, deliveryManager,notificationManager)

    val checkoutData = CheckoutInfo(User("Mario", "Napoli"), PaymentInfo(), Product("Pizza"))

    @Test
    fun doCheckout() {

        val product = checkoutData.product
        val user = checkoutData.user

        val order = Order(product, TEN)

        every { paymentGateway.create(checkoutData.paymentInfo) } returns true
        every { orderManager.create(product) }                    returns order
        every { deliveryManager.scheduleDelivery(order, user) }   returns DeliveryInfo(product.name, user.address, STANDARD_DELIVER)
        every { notificationManager.send(any()) }   just runs

        checkoutUseCase.checkout(checkoutData)

        verify { notificationManager.send("""dear Mario 
 you just bought Pizza at price 10 
 it will be delivered with STANDARD_DELIVER to your address Napoli""") }

    }



}
