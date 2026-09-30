package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MathQuizDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.ScratchCardDialog
import com.example.ui.components.SpinWheelDialog
import com.example.ui.components.TakaHeader
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WatchEarnScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.BkashPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RewardViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TakaRewardApp()
            }
        }
    }
}

@Composable
fun TakaRewardApp(viewModel: RewardViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val recentEarnings by viewModel.recentEarnings.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val activeAd by viewModel.activeAd.collectAsStateWithLifecycle()
    val adTimeRemaining by viewModel.adTimeRemaining.collectAsStateWithLifecycle()
    val isAdCompleted by viewModel.isAdCompleted.collectAsStateWithLifecycle()

    val spinAngle by viewModel.spinAngle.collectAsStateWithLifecycle()
    val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()
    val spinResultCoins by viewModel.spinResultCoins.collectAsStateWithLifecycle()

    val scratchPrizeCoins by viewModel.scratchPrizeCoins.collectAsStateWithLifecycle()
    val isScratchCompleted by viewModel.isScratchCompleted.collectAsStateWithLifecycle()

    val currentQuiz by viewModel.currentQuiz.collectAsStateWithLifecycle()
    val quizSelectedAnswer by viewModel.quizSelectedAnswer.collectAsStateWithLifecycle()
    val quizAnswerStatus by viewModel.quizAnswerStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val isBn = userProfile?.languageBn ?: true

    // Dialog visibility states for tasks
    var showSpinDialog by remember { mutableStateOf(false) }
    var showScratchDialog by remember { mutableStateOf(false) }
    var showQuizDialog by remember { mutableStateOf(false) }

    // Handle back press
    if (currentTab != 0) {
        BackHandler {
            viewModel.selectTab(0)
        }
    }

    // Collect snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.message.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Tab 0: Home
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBn) "হোম" else "Home",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BkashPink,
                        selectedTextColor = BkashPink,
                        indicatorColor = BkashPink.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                // Tab 1: Watch & Earn
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 1) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                            contentDescription = "Watch Ads"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBn) "অ্যাড দেখুন" else "Watch",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BkashPink,
                        selectedTextColor = BkashPink,
                        indicatorColor = BkashPink.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_watch")
                )

                // Tab 2: Tasks & Games
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                            contentDescription = "Tasks"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBn) "টাস্ক" else "Tasks",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BkashPink,
                        selectedTextColor = BkashPink,
                        indicatorColor = BkashPink.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_tasks")
                )

                // Tab 3: Withdraw
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 3) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Withdraw"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBn) "উইথড্র" else "Withdraw",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BkashPink,
                        selectedTextColor = BkashPink,
                        indicatorColor = BkashPink.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_withdraw")
                )

                // Tab 4: Profile
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 4) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(
                            text = if (isBn) "প্রোফাইল" else "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BkashPink,
                        selectedTextColor = BkashPink,
                        indicatorColor = BkashPink.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Top Header showing user stats and quick withdraw
            TakaHeader(
                profile = userProfile,
                onWithdrawClick = { viewModel.selectTab(3) },
                onLanguageToggle = { viewModel.toggleLanguage() }
            )

            // Screen Content based on Current Tab
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    0 -> HomeScreen(
                        profile = userProfile,
                        recentTransactions = recentEarnings,
                        onWatchClick = { viewModel.selectTab(1) },
                        onSpinClick = { showSpinDialog = true },
                        onScratchClick = {
                            viewModel.resetScratchPrize()
                            showScratchDialog = true
                        },
                        onQuizClick = {
                            viewModel.generateNewQuiz()
                            showQuizDialog = true
                        },
                        onWithdrawClick = { viewModel.selectTab(3) },
                        onClaimDaily = { viewModel.claimDailyBonus() }
                    )

                    1 -> WatchEarnScreen(
                        profile = userProfile,
                        videoAds = viewModel.videoAds,
                        onWatchAd = { ad -> viewModel.startWatchingAd(ad) }
                    )

                    2 -> TasksScreen(
                        profile = userProfile,
                        onOpenSpin = { showSpinDialog = true },
                        onOpenScratch = {
                            viewModel.resetScratchPrize()
                            showScratchDialog = true
                        },
                        onOpenQuiz = {
                            viewModel.generateNewQuiz()
                            showQuizDialog = true
                        },
                        onOpenAd = { viewModel.selectTab(1) },
                        onSocialBonusClick = { title, coins ->
                            viewModel.showMessage("টাস্ক সফল! +$coins কয়েন যোগ হয়েছে!")
                        }
                    )

                    3 -> WithdrawScreen(
                        profile = userProfile,
                        withdrawalHistory = withdrawals,
                        onSubmitWithdrawal = { method, accountNum, amount ->
                            viewModel.submitWithdrawal(method, accountNum, amount) { success ->
                                // handled in viewmodel
                            }
                        },
                        onApprovePayout = { txId ->
                            viewModel.approveWithdrawal(txId)
                        }
                    )

                    4 -> ProfileScreen(
                        profile = userProfile,
                        leaderboard = viewModel.getLeaderboard(),
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onSubmitReferralCode = { code -> viewModel.submitReferralCode(code) },
                        onShowMessage = { msg -> viewModel.showMessage(msg) }
                    )
                }
            }
        }
    }

    // --- REWARDED AD VIDEO PLAYER SIMULATOR DIALOG ---
    val currentAd = activeAd
    if (currentAd != null) {
        RewardedAdDialog(
            ad = currentAd,
            timeRemaining = adTimeRemaining,
            isCompleted = isAdCompleted,
            isBn = isBn,
            onClaim = { viewModel.claimAdReward() },
            onClose = { viewModel.closeAd() }
        )
    }

    // --- SPIN WHEEL DIALOG ---
    if (showSpinDialog) {
        SpinWheelDialog(
            spinsLeft = userProfile?.spinsLeftToday ?: 0,
            isSpinning = isSpinning,
            spinAngle = spinAngle,
            spinResultCoins = spinResultCoins,
            isBn = isBn,
            onSpinClick = { viewModel.spinWheel() },
            onDismissResult = {
                viewModel.dismissSpinResult()
                showSpinDialog = false
            },
            onClose = {
                viewModel.dismissSpinResult()
                showSpinDialog = false
            }
        )
    }

    // --- SCRATCH CARD DIALOG ---
    if (showScratchDialog) {
        ScratchCardDialog(
            prizeCoins = scratchPrizeCoins,
            scratchLeft = userProfile?.scratchLeftToday ?: 0,
            isCompleted = isScratchCompleted,
            isBn = isBn,
            onClaimReward = {
                viewModel.claimScratchReward()
                showScratchDialog = false
            },
            onClose = { showScratchDialog = false }
        )
    }

    // --- MATH QUIZ DIALOG ---
    if (showQuizDialog) {
        MathQuizDialog(
            quiz = currentQuiz,
            quizzesLeft = userProfile?.quizzesLeftToday ?: 0,
            selectedIndex = quizSelectedAnswer,
            isCorrect = quizAnswerStatus,
            isBn = isBn,
            onSelectOption = { idx -> viewModel.submitQuizAnswer(idx) },
            onNextQuiz = { viewModel.generateNewQuiz() },
            onClose = { showQuizDialog = false }
        )
    }
}
