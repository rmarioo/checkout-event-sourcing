package com.rmarioo.checkout



interface Executable {
   fun execute()
}

sealed class Command  {
    class Pay(val paymentInfo: PaymentInfo) : Command()
    class Buy(val product: Product) : Command()
    class ScheduleDelivery(val pricedProduct: PricedProduct,val user: User) : Command()
    class SendNotification(val checkoutData: CheckoutInfo,
                           val pricedProduct: PricedProduct,
                           val payment: Payment,
                           val delivery: DeliveryInfo) : Command()
}
