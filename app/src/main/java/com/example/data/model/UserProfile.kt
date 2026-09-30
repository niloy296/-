package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "ইউজার",
    val phoneNumber: String = "01700000000",
    val coins: Int = 1500, // Starting bonus: 1500 coins = 15 BDT
    val totalEarnedCoins: Int = 1500,
    val totalWithdrawnTaka: Double = 0.0,
    val dailyStreak: Int = 1,
    val lastCheckInDate: Long = 0L,
    val adsWatchedToday: Int = 0,
    val maxDailyAds: Int = 25,
    val spinsLeftToday: Int = 5,
    val scratchLeftToday: Int = 5,
    val quizzesLeftToday: Int = 5,
    val referralCode: String = "TAKA88",
    val referredCount: Int = 0,
    val languageBn: Boolean = true
) {
    // 1000 Coins = 10 BDT (100 coins = 1 BDT)
    val takaEquivalent: Double
        get() = coins / 100.0

    val totalEarnedTaka: Double
        get() = totalEarnedCoins / 100.0
}
