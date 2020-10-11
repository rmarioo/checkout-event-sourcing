package com.rmarioo.checkout

import java.math.BigDecimal

data class CheckoutInfo(
    val user: User,
    val paymentInfo: PaymentInfo,
    val product: Product)

data class User(val name: String,val address: String )

data class DeliveryInfo(val productName: String,val address: String,val type: DeliveryType)

enum class DeliveryType {
  STANDARD_DELIVER,
  SAME_DAY
}

data class Payment(val fee: BigDecimal)

data class PaymentInfo (val paymentType: String = "crekitCard")

data class Product(val name: String)
