package com.example.data.model

data class VideoAdItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val sponsor: String,
    val durationSeconds: Int,
    val rewardCoins: Int,
    val category: String,
    val videoType: String = "REWARDED", // "REWARDED", "INTERSTITIAL"
    val descriptionBn: String,
    val descriptionEn: String,
    val badge: String = "HD VIDEO"
)

data class MathQuizItem(
    val id: Int,
    val question: String,
    val options: List<Int>,
    val correctIndex: Int,
    val rewardCoins: Int = 40
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val phoneMasked: String,
    val totalCoins: Int,
    val totalWithdrawnTaka: Double,
    val isCurrentUser: Boolean = false
)
