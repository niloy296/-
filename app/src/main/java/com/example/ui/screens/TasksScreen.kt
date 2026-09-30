package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.CoinGold
import com.example.ui.theme.IncomeGreen

@Composable
fun TasksScreen(
    profile: UserProfile?,
    onOpenSpin: () -> Unit,
    onOpenScratch: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenAd: () -> Unit,
    onSocialBonusClick: (String, Int) -> Unit
) {
    val isBn = profile?.languageBn ?: true
    val spinsLeft = profile?.spinsLeftToday ?: 5
    val scratchLeft = profile?.scratchLeftToday ?: 5
    val quizzesLeft = profile?.quizzesLeftToday ?: 5

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasks_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section: Interactive Mini Games
        item {
            Text(
                text = if (isBn) "গেম ও কুইজ খেলে আয়" else "Play Games & Quizzes",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        item {
            TaskGameCard(
                title = if (isBn) "লাকি স্পিন হুইল (Lucky Spin)" else "Lucky Spin Wheel",
                description = if (isBn) "চাকা ঘুরিয়ে সর্বোচ্চ ৩০০ কয়েন পর্যন্ত জিতে নিন।" else "Spin the wheel to win up to 300 coins.",
                badge = if (isBn) "বাকি $spinsLeft বার" else "$spinsLeft left",
                icon = Icons.Default.Casino,
                accentColor = Color(0xFF3B82F6),
                btnText = if (isBn) "খেলুন" else "Play",
                onClick = onOpenSpin,
                testTag = "spin_task_card"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TaskGameCard(
                title = if (isBn) "স্ক্র্যাচ ও উইন (Scratch Card)" else "Scratch & Win",
                description = if (isBn) "কার্ড ঘষে তাত্ক্ষণিক ৫০-১২০ কয়েন রিওয়ার্ড সংগ্রহ করুন।" else "Scratch the card to uncover 50-120 coins.",
                badge = if (isBn) "বাকি $scratchLeft বার" else "$scratchLeft left",
                icon = Icons.Default.AutoAwesome,
                accentColor = Color(0xFF8B5CF6),
                btnText = if (isBn) "স্ক্র্যাচ করুন" else "Scratch",
                onClick = onOpenScratch,
                testTag = "scratch_task_card"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            TaskGameCard(
                title = if (isBn) "ম্যাথ কুইজ চ্যালেঞ্জ (Math Quiz)" else "Math Quiz Challenge",
                description = if (isBn) "সহজ যোগ ও বিয়োগের সঠিক উত্তর দিয়ে কয়েন আয় করুন।" else "Answer simple math questions to earn coins.",
                badge = if (isBn) "বাকি $quizzesLeft বার" else "$quizzesLeft left",
                icon = Icons.Default.Quiz,
                accentColor = IncomeGreen,
                btnText = if (isBn) "শুরু করুন" else "Start",
                onClick = onOpenQuiz,
                testTag = "quiz_task_card"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Sponsored Offerwall & Social Channels
        item {
            Text(
                text = if (isBn) "অফারওয়াল ও স্পনসরড বোনাস" else "Sponsored Tasks & Offers",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        item {
            SocialBonusCard(
                title = if (isBn) "অফিশিয়াল টেলিগ্রাম চ্যানেলে যুক্ত হোন" else "Join Telegram Channel",
                rewardCoins = 100,
                icon = Icons.Default.Send,
                accentColor = Color(0xFF0088CC),
                onClick = { onSocialBonusClick("Telegram Channel Bonus", 100) },
                isBn = isBn
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            SocialBonusCard(
                title = if (isBn) "অফিশিয়াল ফেসবুক কমিউনিটিতে যুক্ত হোন" else "Join Facebook Community",
                rewardCoins = 100,
                icon = Icons.Default.ThumbUp,
                accentColor = Color(0xFF1877F2),
                onClick = { onSocialBonusClick("Facebook Group Bonus", 100) },
                isBn = isBn
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            SocialBonusCard(
                title = if (isBn) "স্পনসরড নিউজ সাইট ভিজিট (৩০ সেকেন্ড)" else "Visit Sponsored Web Page (30s)",
                rewardCoins = 80,
                icon = Icons.Default.Language,
                accentColor = BkashPink,
                onClick = { onSocialBonusClick("Web Visit Bonus", 80) },
                isBn = isBn
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            SocialBonusCard(
                title = if (isBn) "ইউটিউব পেমেন্ট প্রুফ ভিডিও দেখুন" else "Watch YouTube Payment Proof",
                rewardCoins = 150,
                icon = Icons.Default.OndemandVideo,
                accentColor = Color(0xFFFF0000),
                onClick = { onSocialBonusClick("Payment Proof Video Bonus", 150) },
                isBn = isBn
            )
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun TaskGameCard(
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    btnText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = badge,
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = btnText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SocialBonusCard(
    title: String,
    rewardCoins: Int,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    isBn: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "+$rewardCoins Coins (৳${rewardCoins / 100.0})",
                        color = CoinGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isBn) "সম্পন্ন করুন" else "Complete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
