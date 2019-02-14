package net.laurahouse.firestoreexample

import net.laurahouse.firestoreexample.data.Message


class UpdateMessagesEvent(
    var messages: MutableList<Message>? = null
)