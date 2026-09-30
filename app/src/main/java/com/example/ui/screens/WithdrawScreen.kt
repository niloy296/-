package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkDark
import com.example.ui.theme.CoinGold
import com.example.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawScreen(
    profile: UserProfile?,
    withdrawalHistory: List<Transaction>,
    onSubmitWithdrawal: (method: String, accountNumber: String, amountTaka: Double) -> Unit,
    onApprovePayout: (Long) -> Unit = {}
) {
    val isBn = profile?.languageBn ?: true
    val userCoins = profile?.coins ?: 0
    val userTaka = profile?.takaEquivalent ?: 0.0

    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountType by remember { mutableStateOf("Personal") }
    var accountNumber by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableDoubleStateOf(50.0) }
    var showOwnerGuide by remember { mutableStateOf(false) }

    val methods = listOf(
        PaymentMethodItem("bKash", "বিকাশ", BkashPink, "সর্বাধিক জনপ্রিয়"),
        PaymentMethodItem("Nagad", "নগদ", Color(0xFFF97316), "০% চার্জ"),
        PaymentMethodItem("Rocket", "রকেট", Color(0xFF8B5CF6), "ডিবিবিএল"),
        PaymentMethodItem("Recharge", "রিচার্জ", IncomeGreen, "সকল সিম")
    )

    val presetAmounts = listOf(50.0, 100.0, 200.0, 500.0)
    val requiredCoins = (selectedAmount * 100).toInt()
    val hasEnoughCoins = userCoins >= requiredCoins

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("withdraw_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Balance Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BkashPinkDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "উত্তোলনযোগ্য ব্যালেন্স" else "Withdrawable Balance",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isBn) "১০০০ কয়েন = ৳১০" else "1,000 Coins = ৳10",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "৳" + String.format("%.2f", userTaka),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "($userCoins কয়েন)",
                            color = CoinGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Method Selector
        item {
            Text(
                text = if (isBn) "পেমেন্ট মেথড নির্বাচন করুন" else "Select Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                methods.forEach { method ->
                    val isSelected = selectedMethod == method.id
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = method.id }
                            .testTag("method_${method.id.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) method.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) BorderStroke(2.dp, method.color) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(method.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method.id.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBn) method.nameBn else method.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) method.color else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Account Details Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "$selectedMethod অ্যাকাউন্ট নম্বর" else "$selectedMethod Account Number",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { if (it.length <= 11) accountNumber = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_number_input"),
                        placeholder = { Text("01XXXXXXXXX (১১ ডিজিট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BkashPink,
                            cursorColor = BkashPink
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Phone",
                                tint = BkashPink
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBn) "উত্তোলনের পরিমাণ নির্বাচন করুন" else "Select Amount to Withdraw",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Amount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetAmounts.forEach { amount ->
                            val isSelected = selectedAmount == amount
                            val coinsForAmount = (amount * 100).toInt()
                            Surface(
                                onClick = { selectedAmount = amount },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BkashPink else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("amount_chip_${amount.toInt()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "৳${amount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${coinsForAmount / 1000}k কয়েন",
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Coin requirement message
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (hasEnoughCoins) IncomeGreen.copy(alpha = 0.12f) else BkashPink.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasEnoughCoins) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                                contentDescription = "Status",
                                tint = if (hasEnoughCoins) IncomeGreen else BkashPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasEnoughCoins) {
                                    if (isBn) "আপনার পর্যাপ্ত কয়েন রয়েছে! (প্রয়োজন $requiredCoins কয়েন)" else "Sufficient balance! (Requires $requiredCoins coins)"
                                } else {
                                    val needMore = requiredCoins - userCoins
                                    if (isBn) "আরও $needMore কয়েন প্রয়োজন (৳${needMore / 100.0})" else "Need $needMore more coins"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (hasEnoughCoins) IncomeGreen else BkashPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            onSubmitWithdrawal(selectedMethod, accountNumber, selectedAmount)
                        },
                        enabled = hasEnoughCoins && accountNumber.length == 11,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_withdraw_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BkashPink,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Withdraw",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "টাকা উত্তোলন করুন (৳${selectedAmount.toInt()})" else "Withdraw ৳${selectedAmount.toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Owner Business Model & Earnings Guide
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showOwnerGuide = !showOwnerGuide },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BkashPink.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, BkashPink.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Guide",
                                tint = BkashPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "💡 আপনি (মালিক) কীভাবে টাকা পাবেন ও দিবেন?" else "💡 How you earn & pay via bKash",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BkashPink
                            )
                        }
                        Text(
                            text = if (showOwnerGuide) "▲" else "▼",
                            color = BkashPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (showOwnerGuide) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBn)
                                "১. আপনি টাকা কোথা থেকে পাবেন?\n• Google AdMob / Unity Ads / Start.io আপনার অ্যাপে আসল বিজ্ঞাপন দেখাবে। প্রতি ১,০০০ ভিডিও ভিউতে তারা আপনাকে প্রায় $১-$৩ ডলার (১১০–৩৩০ টাকা) দিবে, যা প্রতি মাসে সরাসরি আপনার ব্যাংক অ্যাকাউন্টে জমা হবে।\n\n২. ব্যবহারকারীকে কীভাবে টাকা দিবেন?\n• অ্যাড থেকে আপনার যে মোট আয় হবে, তার ৬০% টাকা ইউজারদের বিকাশে Send Money করবেন, আর বাকি ৪০% আপনার নিট প্রফিট (লাভ) থাকবে।\n\n৩. প্রসেসিং সিস্টেম:\n• ইউজার উইথড্র দিলে আপনার লিস্টে আসবে। আপনি আপনার বিকাশ থেকে তার নম্বরে টাকা পাঠিয়ে নিচে 'পেমেন্ট সফল করুন' বাটনে ক্লিক করবেন।"
                            else
                                "1. Where do you get money?\n• Ad networks (Google AdMob, Unity Ads) pay you $1-$3 per 1000 video ad views directly to your bank account.\n2. How to pay users?\n• Distribute 60% of ad revenue to users via bKash, and keep 40% as your net profit.\n3. Send money from your bKash app to the user's number, then approve the request.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Withdrawal Rules
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = BkashPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "পেমেন্ট নীতিমালা ও সময়সূচী" else "Payment Policy & Timeline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBn)
                            "• সাধারণত ৫ মিনিট থেকে ২৪ ঘণ্টার মধ্যে বিকাশ অ্যাকাউন্টে টাকা পাঠানো হয়।\n• সর্বনিম্ন উত্তোলন ৫০ টাকা (৫,০০০ কয়েন)।\n• কোনো ভুল বিকাশ নম্বর দিলে অ্যাডমিন দায়ী থাকবে না, তাই নম্বর সতর্কভাবে দিন।"
                        else
                            "• Payments are sent via bKash usually within 5 min to 24 hours.\n• Minimum withdrawal is ৳50 (5,000 coins).\n• Double check your bKash mobile number.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Withdrawal History
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBn) "উত্তোলন হিস্টোরি" else "Withdrawal History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${withdrawalHistory.size} টি লেনদেন",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (withdrawalHistory.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "এখনও কোনো উত্তোলন রিকোয়েস্ট নেই।" else "No withdrawal requests yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(withdrawalHistory) { tx ->
                WithdrawalHistoryCard(
                    tx = tx,
                    isBn = isBn,
                    onApprove = { onApprovePayout(tx.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

data class PaymentMethodItem(
    val id: String,
    val nameBn: String,
    val color: Color,
    val subtitle: String
)

@Composable
fun WithdrawalHistoryCard(
    tx: Transaction,
    isBn: Boolean,
    onApprove: () -> Unit = {}
) {
    val timeFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(tx.timestamp))
    val isPending = tx.status == "PENDING"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BkashPink.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tx.paymentMethod.ifEmpty { "bKash" },
                            color = BkashPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tx.accountNumber.ifEmpty { "01812345678" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "৳${tx.takaAmount.toInt()}",
                    color = BkashPink,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isPending) Color(0xFFF59E0B).copy(alpha = 0.2f) else IncomeGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isPending) {
                            if (isBn) "অপেক্ষমান (Processing)" else "Pending"
                        } else {
                            if (isBn) "সফল (Paid)" else "Paid"
                        },
                        color = if (isPending) Color(0xFFD97706) else IncomeGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // If pending, allow admin simulation to approve payout
            if (isPending) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onApprove,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBn) "✓ বিকাশ পেমেন্ট সম্পন্ন হয়েছে (Approve Payout)" else "✓ Mark as Paid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
