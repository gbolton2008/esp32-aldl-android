package com.example.esp32aldldashboard.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {

    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("UPDATE sessions SET endTime = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long)

    @Insert
    suspend fun insertDataPoints(dataPoints: List<TelemetryDataPointEntity>)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM telemetry_data_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getSessionData(sessionId: Long): Flow<List<TelemetryDataPointEntity>>

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
