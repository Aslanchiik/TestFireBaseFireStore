package com.example.model

import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.*

data class TaskModel(
    var name: String,
    var time: String
)

