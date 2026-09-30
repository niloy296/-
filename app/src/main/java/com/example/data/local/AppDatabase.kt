package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserProfile::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taka_reward_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        // Seed initial profile
                        database.userDao().insertOrUpdate(
                            UserProfile(
                                id = 1,
                                userName = "সফল আর্নার",
                                phoneNumber = "01812345678",
                                coins = 1500, // 15 taka welcome bonus
                                totalEarnedCoins = 1500,
                                totalWithdrawnTaka = 0.0,
                                dailyStreak = 1,
                                referralCode = "TAKA88",
                                referredCount = 1
                            )
                        )
                        // Seed welcome bonus transaction
                        database.transactionDao().insertTransaction(
                            Transaction(
                                title = "ওয়েলকাম বোনাস (Welcome Bonus)",
                                type = "EARN_BONUS",
                                coins = 1500,
                                takaAmount = 15.0,
                                status = "SUCCESS",
                                timestamp = System.currentTimeMillis() - 3600000,
                                referenceId = "BONUS-START"
                            )
                        )
                    }
                }
            }
        }
    }
}
