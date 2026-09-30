importage com.example.ui.screens

importimport android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardUser
import com.example.data.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkDark
import com.example.ui.theme.CoinGold
import com.example.ui.theme.IncomeGreen

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    leaderboard: List<LeaderboardUser>,
    onToggleLanguage: () -> Unit,
    onSubmitReferralCode: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val isBn = profile?.languageBn ?: true
    var selectedTab by remember { mutableIntStateOf(0) }
    var inputReferralCode by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BkashPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (profile?.userName?.take(1) ?: "ইউ"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = profile?.userName ?: "ব্যবহারকারী",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = profile?.phoneNumber ?: "01812345678",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Language Toggle
                        Surface(
                            onClick = onToggleLanguage,
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.testTag("profile_lang_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = BkashPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBn) "বাংলা" else "English",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BkashPink
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid (3 columns)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserStatBox(
                            title = if (isBn) "মোট আয়" else "Total Earned",
                            value = "৳" + String.format("%.1f", profile?.totalEarnedTaka ?: 0.0),
                            color = IncomeGreen,
                            modifier = Modifier.weight(1f)
                        )
                        UserStatBox(
                            title = if (isBn) "মোট উত্তোলন" else "Withdrawn",
                            value = "৳" + String.format("%.0f", profile?.totalWithdrawnTaka ?: 0.0),
                            color = BkashPink,
                            modifier = Modifier.weight(1f)
                        )
                        UserStatBox(
                            title = if (isBn) "দেখা অ্যাড" else "Ads Watched",
                            value = "${profile?.adsWatchedToday ?: 0}টি",
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Sub Tabs: Referral vs Leaderboard vs Owner Admin Guide
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = BkashPink
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isBn) "রেফার" else "Refer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isBn) "র‍্যাঙ্ক" else "Ranking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Bank Guide",
                                tint = BkashPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) "ব্যাংক ও আয়" else "Bank & Income",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BkashPink
                            )
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Refer & Earn Tab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Referral Gift",
                                tint = BkashPink,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "বন্ধুদের আমন্ত্রণ জানিয়ে আয় করুন!" else "Invite Friends & Earn Cash!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn)
                                "আপনার রেফার কোড দিয়ে বন্ধু জয়েন করলে আপনি পাবেন ২০০০ কয়েন (৳২০) এবং বন্ধু পাবে ৫০০ কয়েন (৳৫) বোনাস!"
                            else
                                "Share your referral code! You get 2,000 coins (৳20) and your friend gets 500 coins (৳5) bonus.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // User's Referral Code Box
                        val myCode = profile?.referralCode ?: "TAKA88"
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isBn) "আপনার রেফার কোড:" else "Your Referral Code:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = myCode,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = BkashPink
                                    )
                                }

                                Row {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Referral Code", myCode)
                                            clipboard.setPrimaryClip(clip)
                                            onShowMessage(if (isBn) "রেফার কোড কপি করা হয়েছে!" else "Code copied!")
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBn) "কপি" else "Copy",
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Taka Reward অ্যাপে ভিডিও দেখে টাকা আয় করুন এবং বিকাশে পেমেন্ট নিন! আমার রেফার কোড ব্যবহার করুন: $myCode এবং পান ৫০০ কয়েন ফ্রি বোনাস!"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Referral Code"))
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBn) "শেয়ার" else "Share",
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Input Friend's referral code
                        Text(
                            text = if (isBn) "বন্ধুর রেফারেল কোড দিন (৫০০ কয়েন বোনাস)" else "Enter Friend's Code (Get 500 Coins)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputReferralCode,
                                onValueChange = { inputReferralCode = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("referral_code_input"),
                                placeholder = { Text("যেমন: TAKA99") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BkashPink,
                                    cursorColor = BkashPink
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (inputReferralCode.isNotBlank()) {
                                        onSubmitReferralCode(inputReferralCode)
                                        inputReferralCode = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                modifier = Modifier.testTag("submit_referral_btn")
                            ) {
                                Text(
                                    text = if (isBn) "ক্লেম" else "Claim",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        } else if (selectedTab == 1) {
            // Leaderboard Tab
            items(leaderboard) { user ->
                LeaderboardUserCard(user = user, isBn = isBn)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        } else {
            // Tab 2: Bank & Owner Income Guide
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "৳",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBn) "মালিকের ব্যাংক ও আয়ের হিসেব" else "Owner Bank & Income Model",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isBn) "আপনি কীভাবে টাকা পাবেন বিস্তারিত" else "How money flows to your bank",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Flow Chart Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isBn) "🔄 টাকা কীভাবে আসবে ও যাবে?" else "🔄 Payment Flowchart",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BkashPink
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBn)
                                        "১️⃣ ইউজাররা অ্যাপে অ্যাড দেখবে (আপনার কোনো কাজ নেই)\n⬇️\n২️⃣ গুগল অ্যাডমব (Google AdMob) আপনাকে ডলার ($) দিবে\n⬇️\n৩️⃣ প্রতি মাসের ২১ তারিখ গুগল সরাসরি আপনার ডাচ-বাংলা/ইসলামী ব্যাংক অ্যাকাউন্টে টাকা পাঠিয়ে দিবে\n⬇️\n৪️⃣ ব্যাংকের সেই টাকার একটা অংশ দিয়ে আপনি ইউজারদের বিকাশে পেমেন্ট করবেন এবং বাকিটা আপনার নিট লাভ (Profit)!"
                                    else
                                        "1. Users watch ads on their phones\n2. Google AdMob credits USD to your account\n3. Google transfers BDT to your local bank account on the 21st\n4. You payout user rewards via bKash and pocket the net profit!",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBn) "🏦 Google-এ ব্যাংক অ্যাকাউন্ট যেভাবে যুক্ত করবেন:" else "🏦 How to Add Bank to Google:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isBn)
                                        "১. মোবাইলের Chrome ব্রাউজারে admob.google.com-এ আপনার Gmail দিয়ে লগইন করুন।\n\n২. বাম পাশের মেনু থেকে Payments ➔ Payments Info-তে ক্লিক করুন।\n\n৩. 'Add payment method' ➔ 'Add wire transfer details' চাপুন।\n\n৪. সেখানে নিচের ৪টি তথ্য পূরণ করুন:"
                                    else
                                        "1. Login to admob.google.com using your Gmail on browser.\n2. Go to Payments -> Payments Info.\n3. Click 'Add payment method' -> 'Add wire transfer details'.\n4. Fill in the following 4 fields:",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isBn) "• Account Holder Name: আপনার ব্যাংকে ব্যবহৃত সঠিক ইংরেজি নাম" else "• Account Holder Name: Your name on bank account",
                                        fontSize = 11.sp,
                                        color = CoinGold
                                    )
                                    Text(
                                        text = if (isBn) "• Bank Name: ব্যাংকের নাম (যেমন: Islami Bank Bangladesh Limited)" else "• Bank Name: e.g. Islami Bank Bangladesh Limited",
                                        fontSize = 11.sp,
                                        color = CoinGold
                                    )
                                    Text(
                                        text = if (isBn) "• Account Number: আপনার সম্পূর্ণ ব্যাংক হিসাব নম্বর" else "• Account Number: Your complete bank account number",
                                        fontSize = 11.sp,
                                        color = CoinGold
                                    )
                                    Text(
                                        text = if (isBn) "• SWIFT Code: ব্যাংকের আন্তর্জাতিক কোড (যেমন ডাচ-বাংলা: DBBLBDDH, ইসলামী ব্যাংক: IBBLBDDH)" else "• SWIFT Code: International code (e.g. DBBL: DBBLBDDH, IBBL: IBBLBDDH)",
                                        fontSize = 11.sp,
                                        color = CoinGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://admob.google.com")
                                )
                                context.startActivity(browserIntent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBn) "🌐 Google AdMob ওয়েবসাইটে যান" else "🌐 Open Google AdMob",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun UserStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = color
            )
        }
    }
}

@Composable
fun LeaderboardUserCard(user: LeaderboardUser, isBn: Boolean) {
    val rankBadgeColor = when (user.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isCurrentUser) BkashPink.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (user.isCurrentUser) androidx.compose.foundation.BorderStroke(1.5.dp, BkashPink) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${user.rank}",
                        color = if (user.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = user.phoneMasked,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${user.totalWithdrawnTaka.toInt()}",
                    color = BkashPink,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${user.totalCoins / 1000}k Coins",
                    color = CoinGold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
