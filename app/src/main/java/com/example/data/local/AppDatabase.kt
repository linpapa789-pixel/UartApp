package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AiBrainDao
import com.example.data.local.dao.CurrentMeasurementDao
import com.example.data.local.dao.DeviceDatabaseDao
import com.example.data.local.dao.FaultLogDao
import com.example.data.local.dao.GoodLogDao
import com.example.data.local.dao.RepairCaseDao
import com.example.data.local.dao.UartSessionDao
import com.example.data.local.entity.AiBrainVersionEntity
import com.example.data.local.entity.AiKeywordEntity
import com.example.data.local.entity.AiMemoryEntity
import com.example.data.local.entity.AiPromptEntity
import com.example.data.local.entity.AiRepairKnowledgeEntity
import com.example.data.local.entity.AiRoleEntity
import com.example.data.local.entity.AiRuleEntity
import com.example.data.local.entity.AiSettingsEntity
import com.example.data.local.entity.CurrentMeasurementEntity
import com.example.data.local.entity.DeviceDatabaseEntity
import com.example.data.local.entity.FaultLogEntity
import com.example.data.local.entity.GoodLogReferenceEntity
import com.example.data.local.entity.RepairCaseEntity
import com.example.data.local.entity.UartSessionEntity

@Database(
    entities = [
        UartSessionEntity::class,
        GoodLogReferenceEntity::class,
        FaultLogEntity::class,
        DeviceDatabaseEntity::class,
        RepairCaseEntity::class,
        CurrentMeasurementEntity::class,
        AiPromptEntity::class,
        AiRoleEntity::class,
        AiRuleEntity::class,
        AiKeywordEntity::class,
        AiRepairKnowledgeEntity::class,
        AiMemoryEntity::class,
        AiSettingsEntity::class,
        AiBrainVersionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun uartSessionDao(): UartSessionDao
    abstract fun goodLogDao(): GoodLogDao
    abstract fun faultLogDao(): FaultLogDao
    abstract fun deviceDatabaseDao(): DeviceDatabaseDao
    abstract fun repairCaseDao(): RepairCaseDao
    abstract fun currentMeasurementDao(): CurrentMeasurementDao
    abstract fun aiBrainDao(): AiBrainDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uart_pro_ai_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
