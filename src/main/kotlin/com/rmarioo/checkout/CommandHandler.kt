package com.rmarioo.checkout

import com.rmarioo.checkout.Command.Buy
import com.rmarioo.checkout.Command.Pay
import com.rmarioo.checkout.Command.ScheduleDelivery
import com.rmarioo.checkout.Command.SendNotification

class CommandHandler(
    val eventStore: InMemoryEventStore
) {

    fun handleCommand(command: Command) = when (command) {
        is Pay              -> command.execute(eventStore)
        is Buy              -> command.execute(eventStore)
        is ScheduleDelivery -> command.execute(eventStore)
        is SendNotification -> command.execute(eventStore)
    }


    fun handleCommands(commands: List<Command>) {
        commands.forEach {c -> handleCommand(c)}
    }
}
