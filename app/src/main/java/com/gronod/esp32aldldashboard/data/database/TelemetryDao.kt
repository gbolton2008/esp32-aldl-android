package com.gronod.esp32aldldashboard.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface TelemetryDao {

    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("UPDATE sessions SET endTime = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long): Int

    @Insert
    suspend fun insertDataPoints(dataPoints: List<TelemetryDataPointEntity>): List<Long>

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM telemetry_data_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getSessionData(sessionId: Long): Flow<List<TelemetryDataPointEntity>>

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long): Int
}
