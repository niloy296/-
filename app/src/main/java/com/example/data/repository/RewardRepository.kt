package com.example.data.repository

import com.example.data.local.TransactionDao
import com.example.data.local.UserDao
import com.example.data.model.LeaderboardUser
import com.example.data.model.MathQuizItem
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.VideoAdItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RewardRepository(
    private val userDao: UserDao,
    private val transactionDao: TransactionDao
) {
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val withdrawals: Flow<List<Transaction>> = transactionDao.getWithdrawalTransactions()
    val recentEarnings: Flow<List<Transaction>> = transactionDao.getRecentEarnings()

    suspend fun getOrCreateProfile(): UserProfile {
        val existing = userDao.getUserProfileOnce()
        if (existing != null) {
            return existing
        }
        val newProfile = UserProfile()
        userDao.insertOrUpdate(newProfile)
        return newProfile
    }

    suspend fun earnFromAd(ad: VideoAdItem): Boolean {
        val profile = getOrCreateProfile()
        val updated = profile.copy(
            coins = profile.coins + ad.rewardCoins,
            totalEarnedCoins = profile.totalEarnedCoins + ad.rewardCoins,
            adsWatchedToday = profile.adsWatchedToday + 1
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "অ্যাড রিওয়ার্ড: ${ad.sponsor}",
                type = "EARN_AD",
                coins = ad.rewardCoins,
                takaAmount = ad.rewardCoins / 100.0,
                status = "SUCCESS",
                referenceId = "AD-${UUID.randomUUID().toString().take(6).uppercase()}"
            )
        )
        return true
    }

    suspend fun earnFromSpin(coins: Int): Boolean {
        val profile = getOrCreateProfile()
        if (profile.spinsLeftToday <= 0) return false

        val updated = profile.copy(
            coins = profile.coins + coins,
            totalEarnedCoins = profile.totalEarnedCoins + coins,
            spinsLeftToday = profile.spinsLeftToday - 1
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "লাকি স্পিন পুরষ্কার (Lucky Spin)",
                type = "EARN_SPIN",
                coins = coins,
                takaAmount = coins / 100.0,
                status = "SUCCESS",
                referenceId = "SPIN-${UUID.randomUUID().toString().take(6).uppercase()}"
            )
        )
        return true
    }

    suspend fun earnFromScratch(coins: Int): Boolean {
        val profile = getOrCreateProfile()
        if (profile.scratchLeftToday <= 0) return false

        val updated = profile.copy(
            coins = profile.coins + coins,
            totalEarnedCoins = profile.totalEarnedCoins + coins,
            scratchLeftToday = profile.scratchLeftToday - 1
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "স্ক্র্যাচ কার্ড পুরষ্কার (Scratch Card)",
                type = "EARN_SCRATCH",
                coins = coins,
                takaAmount = coins / 100.0,
                status = "SUCCESS",
                referenceId = "SCRATCH-${UUID.randomUUID().toString().take(6).uppercase()}"
            )
        )
        return true
    }

    suspend fun earnFromQuiz(coins: Int): Boolean {
        val profile = getOrCreateProfile()
        if (profile.quizzesLeftToday <= 0) return false

        val updated = profile.copy(
            coins = profile.coins + coins,
            totalEarnedCoins = profile.totalEarnedCoins + coins,
            quizzesLeftToday = profile.quizzesLeftToday - 1
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "গণিত কুইজ রিওয়ার্ড (Math Quiz)",
                type = "EARN_QUIZ",
                coins = coins,
                takaAmount = coins / 100.0,
                status = "SUCCESS",
                referenceId = "QUIZ-${UUID.randomUUID().toString().take(6).uppercase()}"
            )
        )
        return true
    }

    suspend fun claimDailyBonus(): Pair<Boolean, Int> {
        val profile = getOrCreateProfile()
        val now = System.currentTimeMillis()
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val todayStr = df.format(Date(now))
        val lastStr = df.format(Date(profile.lastCheckInDate))

        if (todayStr == lastStr && profile.lastCheckInDate != 0L) {
            return Pair(false, 0) // Already claimed today
        }

        val nextStreak = if (profile.dailyStreak >= 7) 1 else profile.dailyStreak + 1
        val bonusCoins = when (nextStreak) {
            1 -> 100
            2 -> 150
            3 -> 200
            4 -> 250
            5 -> 350
            6 -> 500
            else -> 1000
        }

        val updated = profile.copy(
            coins = profile.coins + bonusCoins,
            totalEarnedCoins = profile.totalEarnedCoins + bonusCoins,
            dailyStreak = nextStreak,
            lastCheckInDate = now,
            spinsLeftToday = 5,
            scratchLeftToday = 5,
            quizzesLeftToday = 5,
            adsWatchedToday = 0
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "দৈনিক বোনাস - দিন $nextStreak (Daily Bonus)",
                type = "EARN_BONUS",
                coins = bonusCoins,
                takaAmount = bonusCoins / 100.0,
                status = "SUCCESS",
                referenceId = "BONUS-D$nextStreak"
            )
        )
        return Pair(true, bonusCoins)
    }

    suspend fun requestWithdrawal(
        method: String, // "bKash", "Nagad", "Rocket", "Recharge"
        accountNumber: String,
        amountTaka: Double
    ): Pair<Boolean, String> {
        val profile = getOrCreateProfile()
        val requiredCoins = (amountTaka * 100).toInt()

        if (accountNumber.length != 11 || !accountNumber.startsWith("01")) {
            return Pair(false, "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন 017xxxxxxxx)")
        }

        if (profile.coins < requiredCoins) {
            return Pair(false, "পর্যাপ্ত কয়েন নেই! আপনার প্রয়োজন $requiredCoins কয়েন")
        }

        val updated = profile.copy(
            coins = profile.coins - requiredCoins,
            totalWithdrawnTaka = profile.totalWithdrawnTaka + amountTaka
        )
        userDao.update(updated)

        val txId = "BKSH-" + UUID.randomUUID().toString().take(8).uppercase()
        val tx = Transaction(
            title = "$method ক্যাশআউট ৳${amountTaka.toInt()}",
            type = "WITHDRAW",
            coins = requiredCoins,
            takaAmount = amountTaka,
            paymentMethod = method,
            accountNumber = accountNumber,
            status = "PENDING",
            timestamp = System.currentTimeMillis(),
            referenceId = txId
        )
        transactionDao.insertTransaction(tx)

        return Pair(true, "আপনার ৳${amountTaka.toInt()} টাকা উত্তোলনের আবেদন গৃহীত হয়েছে! বিকাশ/পেমেন্ট অ্যাকাউন্টে দ্রুত পাঠানো হবে।")
    }

    suspend fun applyReferralCode(code: String): Pair<Boolean, String> {
        val profile = getOrCreateProfile()
        val trimmed = code.trim().uppercase()

        if (trimmed == profile.referralCode) {
            return Pair(false, "নিজের রেফারেল কোড ব্যবহার করা যাবে না!")
        }
        if (trimmed.length < 5) {
            return Pair(false, "অবৈধ রেফারেল কোড!")
        }

        val bonus = 500 // 5 taka bonus
        val updated = profile.copy(
            coins = profile.coins + bonus,
            totalEarnedCoins = profile.totalEarnedCoins + bonus
        )
        userDao.update(updated)

        transactionDao.insertTransaction(
            Transaction(
                title = "রেফারেল বোনাস ($trimmed)",
                type = "EARN_REFERRAL",
                coins = bonus,
                takaAmount = 5.0,
                status = "SUCCESS",
                referenceId = "REF-$trimmed"
            )
        )
        return Pair(true, "অভিনন্দন! আপনি ৫০০ কয়েন (৳৫) বোনাস পেয়েছেন!")
    }

    suspend fun updateLanguage(isBn: Boolean) {
        val profile = getOrCreateProfile()
        userDao.update(profile.copy(languageBn = isBn))
    }

    suspend fun approveWithdrawal(txId: Long) {
        transactionDao.approveWithdrawal(txId)
    }

    // Static catalog of video ads
    fun getVideoAds(): List<VideoAdItem> {
        return listOf(
            VideoAdItem(
                id = "ad_1",
                titleBn = "দারাজ গ্র্যান্ড সেল মেগা ডিল",
                titleEn = "Daraz Grand Sale Mega Deal",
                sponsor = "Daraz Bangladesh",
                durationSeconds = 15,
                rewardCoins = 150,
                category = "E-Commerce",
                descriptionBn = "দারাজ অ্যাপে ৫০% পর্যন্ত বিশাল ডিসকাউন্টে কেনাকাটা করুন।",
                descriptionEn = "Shop on Daraz app with huge discounts up to 50% off.",
                badge = "জনপ্রিয় (HOT)"
            ),
            VideoAdItem(
                id = "ad_2",
                titleBn = "বিকাশ ক্যাশব্যাক ও রিচার্জ অফার",
                titleEn = "bKash Cashback & Recharge Offer",
                sponsor = "bKash Limited",
                durationSeconds = 20,
                rewardCoins = 200,
                category = "FinTech",
                descriptionBn = "যেকোনো নম্বরে মোবাইল রিচার্জে পান নিশ্চিত ক্যাশব্যাক!",
                descriptionEn = "Get instant cashback on mobile recharges to any number!",
                badge = "হাই রিওয়ার্ড (৳২)"
            ),
            VideoAdItem(
                id = "ad_3",
                titleBn = "ফুডপ্যান্ডা ৬০% ফুড ভাউচার",
                titleEn = "Foodpanda 60% Food Voucher",
                sponsor = "Foodpanda BD",
                durationSeconds = 15,
                rewardCoins = 120,
                category = "Food Delivery",
                descriptionBn = "আজই সুস্বাদু খাবার অর্ডার করুন ফ্রি ডেলিভারিতে।",
                descriptionEn = "Order delicious meals today with free home delivery.",
                badge = "দ্রুত আয়"
            ),
            VideoAdItem(
                id = "ad_4",
                titleBn = "পাঠাও কার ও বাইক রাইড সেভিংস",
                titleEn = "Pathao Car & Bike Ride Savings",
                sponsor = "Pathao Rides",
                durationSeconds = 20,
                rewardCoins = 180,
                category = "Ride Sharing",
                descriptionBn = "রাইড শেয়ার করুন নিরাপদ ও সাশ্রয়ী ভাড়ায় পাঠাও এর সাথে।",
                descriptionEn = "Share rides safely and affordably with Pathao.",
                badge = "বোনাস অ্যাড"
            ),
            VideoAdItem(
                id = "ad_5",
                titleBn = "গ্রামীণফোন সুপার ৪জি ইন্টারনেট অফার",
                titleEn = "Grameenphone Super 4G Internet Deal",
                sponsor = "Grameenphone GP",
                durationSeconds = 25,
                rewardCoins = 250,
                category = "Telecom",
                descriptionBn = "দেশজুড়ে সবচেয়ে দ্রুতগতির ৪জি নেটওয়ার্ক অভিজ্ঞতা নিন।",
                descriptionEn = "Experience the country's fastest 4G data network.",
                badge = "মেগা কয়েন (৳২.৫)"
            ),
            VideoAdItem(
                id = "ad_6",
                titleBn = "নগদ ইসলামিক অ্যাকাউন্ট ও বিল পে",
                titleEn = "Nagad Islamic Account & Bill Pay",
                sponsor = "Nagad Digital",
                durationSeconds = 15,
                rewardCoins = 140,
                category = "FinTech",
                descriptionBn = "বিনা খরচে সব ইউটিলিটি বিল পরিশোধ করুন নগদ দিয়ে।",
                descriptionEn = "Pay all utility bills without extra charge via Nagad.",
                badge = "নতুন অফার"
            ),
            VideoAdItem(
                id = "ad_7",
                titleBn = "ফ্রি ফায়ার ও পাবজি ডায়মন্ড টপআপ",
                titleEn = "Gaming Diamond Topup & Passes",
                sponsor = "BD Game Bazar",
                durationSeconds = 20,
                rewardCoins = 190,
                category = "Gaming",
                descriptionBn = "সবচেয়ে কম মূল্যে গেম টপআপ ও এলিট পাস কিনুন বিকাশে।",
                descriptionEn = "Top up diamonds and elite passes at lowest prices with bKash.",
                badge = "গেমিং রিওয়ার্ড"
            ),
            VideoAdItem(
                id = "ad_8",
                titleBn = "চলতি বিশ্বকাপ লাইভ ও ক্রিকেট আপডেট",
                titleEn = "Cricket Live Streaming & Highlights",
                sponsor = "Toffee App",
                durationSeconds = 15,
                rewardCoins = 130,
                category = "Streaming",
                descriptionBn = "লাইভ ক্রিকেট ম্যাচ উপভোগ করুন কোনো বাফারিং ছাড়া।",
                descriptionEn = "Enjoy live cricket matches without any buffering.",
                badge = "স্পেশাল"
            )
        )
    }

    // Static leaderboard entries with realistic Bangladeshi names
    fun getLeaderboard(userProfile: UserProfile?): List<LeaderboardUser> {
        val userCoins = userProfile?.totalEarnedCoins ?: 1500
        val userWithdrawn = userProfile?.totalWithdrawnTaka ?: 0.0

        val list = mutableListOf(
            LeaderboardUser(1, "তানভীর আহমেদ (Tanvir)", "017***9421", 124500, 1200.0),
            LeaderboardUser(2, "সালমান ফারসি (Salman)", "019***5812", 98200, 950.0),
            LeaderboardUser(3, "মেহেদী হাসান (Mehedi)", "018***3309", 84500, 800.0),
            LeaderboardUser(4, "রাকিবুল ইসলাম (Rakib)", "016***8123", 69000, 650.0),
            LeaderboardUser(5, "নুসরাত জাহান (Nusrat)", "013***1944", 57200, 550.0),
            LeaderboardUser(6, "শাকিব চৌধুরী (Sakib)", "017***7712", 48100, 450.0),
            LeaderboardUser(7, "আরিফুল হক (Arif)", "019***2290", 39400, 380.0),
            LeaderboardUser(8, "ফাতেমা আক্তার (Fatema)", "018***6041", 31200, 300.0)
        )

        // Add current user if not at top
        list.add(
            LeaderboardUser(
                rank = 9,
                name = userProfile?.userName ?: "আপনি (You)",
                phoneMasked = "018***4567",
                totalCoins = userCoins,
                totalWithdrawnTaka = userWithdrawn,
                isCurrentUser = true
            )
        )
        return list
    }
}
