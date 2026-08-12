package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CurrentMeasurementEntity
import com.example.data.local.entity.DeviceDatabaseEntity
import com.example.data.local.entity.FaultLogEntity
import com.example.data.local.entity.GoodLogReferenceEntity
import com.example.data.local.entity.RepairCaseEntity
import com.example.data.local.entity.UartSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UartSessionDao {
    @Query("SELECT * FROM uart_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<UartSessionEntity>>

    @Query("SELECT * FROM uart_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): UartSessionEntity?

    @Query("SELECT * FROM uart_sessions WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteSessions(): Flow<List<UartSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UartSessionEntity): Long

    @Update
    suspend fun updateSession(session: UartSessionEntity)

    @Delete
    suspend fun deleteSession(session: UartSessionEntity)

    @Query("SELECT * FROM uart_sessions WHERE brand LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' OR repairJobNumber LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchSessions(query: String): Flow<List<UartSessionEntity>>
}

@Dao
interface GoodLogDao {
    @Query("SELECT * FROM good_log_references ORDER BY id DESC")
    fun getAllGoodLogs(): Flow<List<GoodLogReferenceEntity>>

    @Query("SELECT * FROM good_log_references WHERE brand = :brand AND model = :model")
    fun getGoodLogsByModel(brand: String, model: String): Flow<List<GoodLogReferenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodLog(log: GoodLogReferenceEntity): Long

    @Delete
    suspend fun deleteGoodLog(log: GoodLogReferenceEntity)
}

@Dao
interface FaultLogDao {
    @Query("SELECT * FROM fault_logs ORDER BY id DESC")
    fun getAllFaultLogs(): Flow<List<FaultLogEntity>>

    @Query("SELECT * FROM fault_logs WHERE faultCategory = :category")
    fun getFaultLogsByCategory(category: String): Flow<List<FaultLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaultLog(log: FaultLogEntity): Long

    @Delete
    suspend fun deleteFaultLog(log: FaultLogEntity)
}

@Dao
interface DeviceDatabaseDao {
    @Query("SELECT * FROM device_database ORDER BY brand ASC, model ASC")
    fun getAllDevices(): Flow<List<DeviceDatabaseEntity>>

    @Query("SELECT * FROM device_database WHERE brand LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' OR codename LIKE '%' || :query || '%' OR cpu LIKE '%' || :query || '%'")
    fun searchDevices(query: String): Flow<List<DeviceDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceDatabaseEntity): Long
}

@Dao
interface RepairCaseDao {
    @Query("SELECT * FROM repair_cases ORDER BY id DESC")
    fun getAllRepairCases(): Flow<List<RepairCaseEntity>>

    @Query("SELECT * FROM repair_cases WHERE fault = :faultCategory")
    fun getCasesByFault(faultCategory: String): Flow<List<RepairCaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairCase(repairCase: RepairCaseEntity): Long
}

@Dao
interface CurrentMeasurementDao {
    @Query("SELECT * FROM current_measurements WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun getMeasurementsForSession(sessionId: Long): Flow<List<CurrentMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: CurrentMeasurementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurements(measurements: List<CurrentMeasurementEntity>)
}
