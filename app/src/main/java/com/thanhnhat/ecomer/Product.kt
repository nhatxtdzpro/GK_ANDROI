package com.thanhnhat.ecomer

import com.google.firebase.firestore.DocumentId

/** Four fields required by the assignment; id is Firestore metadata, not user input. */
data class Product(
    @DocumentId var id: String = "",
    var name: String = "",
    var price: Long = 0,
    var category: String = "",
    var imageUrl: String = ""
)
