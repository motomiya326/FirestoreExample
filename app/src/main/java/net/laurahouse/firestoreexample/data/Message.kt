package net.laurahouse.firestoreexample.data

import java.util.*

data class Message(
    var name: String? = null,
    var message: String? = null,
    var documentId: String? = null,
    var updateAt: Date? = null
)