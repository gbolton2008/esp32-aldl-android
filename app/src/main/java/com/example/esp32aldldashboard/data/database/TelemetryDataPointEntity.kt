package com.example.esp32aldldashboard.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "telemetry_data_points",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId", "timestamp"])
    ]
)
data class TelemetryDataPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long,
    val rawBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TelemetryDataPointEntity

        if (id != other.id) return false
        if (sessionId != other.sessionId) return false
        if (timestamp != other.timestamp) return false
        return rawBytes.contentEquals(other.rawBytes)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + rawBytes.contentHashCode()
        return result
    }
}
