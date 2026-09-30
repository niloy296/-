package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "EARN_AD", "EARN_SPIN", "EARN_SCRATCH", "EARN_QUIZ", "EARN_BONUS", "EARN_REFERRAL", "WITHDRAW"
    val coins: Int,
    val takaAmount: Double,
    val paymentMethod: String = "", // "bKash", "Nagad", "Rocket", "Recharge"
    val accountNumber: String = "",
    val status: String = "SUCCESS", // "SUCCESS", "PENDING", "APPROVED"
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = ""
)
