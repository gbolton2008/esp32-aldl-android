# ProGuard rules for ESP32 ALDL Dashboard
# Compose + Room + Kotlin Serialization + Navigation3

# --- General ---
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep class * { @androidx.room.* <fields>; }
-dontwarn androidx.room.paging.**

# --- Kotlin Serialization ---
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    static ** \]serializerInstance;
}

# --- Compose / Navigation3 ---
-keep class androidx.navigation3.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# --- DataStore ---
-keep class androidx.datastore.** { *; }

# --- Keep all app classes (safe for now) ---
-keep class com.gronod.esp32aldldashboard.** { *; }