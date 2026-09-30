package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.LeaderboardUser
import com.example.data.model.MathQuizItem
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.VideoAdItem
import com.example.data.repository.RewardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class RewardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = RewardRepository(database.userDao(), database.transactionDao())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<Transaction>> = repository.withdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentEarnings: StateFlow<List<Transaction>> = repository.recentEarnings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation Tab (0: Home, 1: Watch, 2: Tasks/Games, 3: Withdraw, 4: Profile)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Notification toast/snackbar
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    // Video Ads list
    val videoAds: List<VideoAdItem> = repository.getVideoAds()

    // Active Ad State
    private val _activeAd = MutableStateFlow<VideoAdItem?>(null)
    val activeAd: StateFlow<VideoAdItem?> = _activeAd.asStateFlow()

    private val _adTimeRemaining = MutableStateFlow(0)
    val adTimeRemaining: StateFlow<Int> = _adTimeRemaining.asStateFlow()

    private val _isAdCompleted = MutableStateFlow(false)
    val isAdCompleted: StateFlow<Boolean> = _isAdCompleted.asStateFlow()

    private var adTimerJob: Job? = null

    // Lucky Spin Wheel state
    private val _spinAngle = MutableStateFlow(0f)
    val spinAngle: StateFlow<Float> = _spinAngle.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _spinResultCoins = MutableStateFlow<Int?>(null)
    val spinResultCoins: StateFlow<Int?> = _spinResultCoins.asStateFlow()

    // Scratch card state
    private val _scratchPrizeCoins = MutableStateFlow(50)
    val scratchPrizeCoins: StateFlow<Int> = _scratchPrizeCoins.asStateFlow()

    private val _isScratchCompleted = MutableStateFlow(false)
    val isScratchCompleted: StateFlow<Boolean> = _isScratchCompleted.asStateFlow()

    // Math Quiz state
    private val _currentQuiz = MutableStateFlow<MathQuizItem?>(null)
    val currentQuiz: StateFlow<MathQuizItem?> = _currentQuiz.asStateFlow()

    private val _quizSelectedAnswer = MutableStateFlow<Int?>(null)
    val quizSelectedAnswer: StateFlow<Int?> = _quizSelectedAnswer.asStateFlow()

    private val _quizAnswerStatus = MutableStateFlow<Boolean?>(null) // true if correct, false if wrong
    val quizAnswerStatus: StateFlow<Boolean?> = _quizAnswerStatus.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getOrCreateProfile()
            generateNewQuiz()
            resetScratchPrize()
        }
    }

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _message.emit(msg)
        }
    }

    // --- REWARDED VIDEO AD LOGIC ---
    fun startWatchingAd(ad: VideoAdItem) {
        val profile = userProfile.value
        if (profile != null && profile.adsWatchedToday >= profile.maxDailyAds) {
            showMessage("আজকের অ্যাড দেখার সীমা পূর্ণ হয়েছে! আগামীকাল আবার চেষ্টা করুন।")
            return
        }

        _activeAd.value = ad
        _adTimeRemaining.value = ad.durationSeconds
        _isAdCompleted.value = false

        adTimerJob?.cancel()
        adTimerJob = viewModelScope.launch {
            for (sec in ad.durationSeconds downTo 1) {
                _adTimeRemaining.value = sec
                delay(1000L)
            }
            _adTimeRemaining.value = 0
            _isAdCompleted.value = true
        }
    }

    fun claimAdReward() {
        val ad = _activeAd.value ?: return
        if (!_isAdCompleted.value) return

        viewModelScope.launch {
            repository.earnFromAd(ad)
            showMessage("অভিনন্দন! আপনি ${ad.rewardCoins} কয়েন (৳${ad.rewardCoins / 100.0}) পেয়েছেন!")
            closeAd()
        }
    }

    fun closeAd() {
        adTimerJob?.cancel()
        _activeAd.value = null
        _adTimeRemaining.value = 0
        _isAdCompleted.value = false
    }

    // --- DAILY CHECK-IN BONUS ---
    fun claimDailyBonus() {
        viewModelScope.launch {
            val (success, coins) = repository.claimDailyBonus()
            if (success) {
                showMessage("অভিনন্দন! দৈনিক বোনাস হিসেবে $coins কয়েন যোগ হয়েছে!")
            } else {
                showMessage("আজকের দৈনিক বোনাস আপনি ইতিমধ্যে গ্রহণ করেছেন!")
            }
        }
    }

    // --- LUCKY SPIN WHEEL ---
    fun spinWheel() {
        val profile = userProfile.value ?: return
        if (profile.spinsLeftToday <= 0) {
            showMessage("আজকের স্পিন সীমা শেষ! আগামীকাল নতুন ৫টি স্পিন পাবেন।")
            return
        }
        if (_isSpinning.value) return

        _isSpinning.value = true
        _spinResultCoins.value = null

        // 8 slices on the wheel: 10, 20, 50, 80, 100, 150, 200, 300
        val prizeList = listOf(20, 50, 30, 80, 40, 100, 60, 150)
        val selectedIndex = Random.nextInt(prizeList.size)
        val prize = prizeList[selectedIndex]

        // 360 / 8 = 45 degrees per slice
        val baseRotations = 360f * 5 // 5 full turns
        val sliceAngle = 45f
        val targetAngle = baseRotations + (selectedIndex * sliceAngle) + (sliceAngle / 2f)

        viewModelScope.launch {
            _spinAngle.value = _spinAngle.value + targetAngle
            delay(3500L) // Wait for spin animation
            _isSpinning.value = false
            _spinResultCoins.value = prize
            repository.earnFromSpin(prize)
            showMessage("দারুণ! আপনি লাকি স্পিনে $prize কয়েন জিতেছেন!")
        }
    }

    fun dismissSpinResult() {
        _spinResultCoins.value = null
    }

    // --- SCRATCH CARD ---
    fun resetScratchPrize() {
        val prizes = listOf(30, 40, 50, 60, 75, 90, 100, 120)
        _scratchPrizeCoins.value = prizes.random()
        _isScratchCompleted.value = false
    }

    fun claimScratchReward() {
        if (_isScratchCompleted.value) return
        val profile = userProfile.value ?: return
        if (profile.scratchLeftToday <= 0) {
            showMessage("আজকের স্ক্র্যাচ কার্ড সীমা শেষ!")
            return
        }

        viewModelScope.launch {
            _isScratchCompleted.value = true
            val prize = _scratchPrizeCoins.value
            repository.earnFromScratch(prize)
            showMessage("অভিনন্দন! স্ক্র্যাচ করে $prize কয়েন লাভ করলেন!")
        }
    }

    // --- MATH QUIZ ---
    fun generateNewQuiz() {
        val a = Random.nextInt(5, 50)
        val b = Random.nextInt(3, 30)
        val isAdd = Random.nextBoolean()
        val questionStr = if (isAdd) "$a + $b = ?" else "$a - $b = ?"
        val answer = if (isAdd) a + b else a - b

        val distractors = mutableSetOf<Int>()
        while (distractors.size < 3) {
            val d = answer + Random.nextInt(-8, 9)
            if (d != answer && d >= 0) {
                distractors.add(d)
            }
        }
        val options = (distractors.toList() + answer).shuffled()
        val correctIndex = options.indexOf(answer)

        _currentQuiz.value = MathQuizItem(
            id = Random.nextInt(1000),
            question = questionStr,
            options = options,
            correctIndex = correctIndex,
            rewardCoins = 40
        )
        _quizSelectedAnswer.value = null
        _quizAnswerStatus.value = null
    }

    fun submitQuizAnswer(selectedIndex: Int) {
        val quiz = _currentQuiz.value ?: return
        if (_quizAnswerStatus.value != null) return // Already submitted

        _quizSelectedAnswer.value = selectedIndex
        val isCorrect = selectedIndex == quiz.correctIndex
        _quizAnswerStatus.value = isCorrect

        if (isCorrect) {
            viewModelScope.launch {
                val profile = userProfile.value
                if (profile != null && profile.quizzesLeftToday > 0) {
                    repository.earnFromQuiz(quiz.rewardCoins)
                    showMessage("সঠিক উত্তর! +${quiz.rewardCoins} কয়েন পেয়েছেন!")
                } else {
                    showMessage("আজকের কুইজ সীমা শেষ!")
                }
            }
        } else {
            showMessage("ভুল উত্তর! সঠিক উত্তর ছিল: ${quiz.options[quiz.correctIndex]}")
        }
    }

    // --- WITHDRAWAL REQUEST ---
    fun submitWithdrawal(
        method: String,
        accountNumber: String,
        amountTaka: Double,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val (success, reply) = repository.requestWithdrawal(method, accountNumber, amountTaka)
            showMessage(reply)
            onComplete(success)
        }
    }

    fun approveWithdrawal(txId: Long) {
        viewModelScope.launch {
            repository.approveWithdrawal(txId)
            showMessage("পেমেন্ট অনুমোদিত ও সফল হিসেবে চিহ্নিত করা হয়েছে!")
        }
    }

    // --- REFERRAL CODE ---
    fun submitReferralCode(code: String) {
        viewModelScope.launch {
            val (success, reply) = repository.applyReferralCode(code)
            showMessage(reply)
        }
    }

    // --- LANGUAGE TOGGLE ---
    fun toggleLanguage() {
        val current = userProfile.value?.languageBn ?: true
        viewModelScope.launch {
            repository.updateLanguage(!current)
        }
    }

    fun getLeaderboard(): List<LeaderboardUser> {
        return repository.getLeaderboard(userProfile.value)
    }
}
