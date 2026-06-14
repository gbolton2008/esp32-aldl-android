# ProGuard rules for ESP32 ALDL Dashboard
# Compose + Room + Kotlin Serialization + Navigation3

# --- Crash stack traces: preserve file names and line numbers ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- General reflection / generic type preservation ---
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# --- Room ---
# RoomDatabase subclass is instantiated by reflection
-keep class * extends androidx.room.RoomDatabase { *; }
# Keep all @Entity, @Dao, @Database annotated members
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }

# --- Kotlin Serialization ---
# Only nav key objects annotated with @Serializable need explicit protection;
# the Kotlin compiler plugin generates keep rules for everything else.
-keep @kotlinx.serialization.Serializable class com.gronod.esp32aldldashboard.** { *; }

# --- Enums ---
# Preserve enum names used by Room, Bluetooth state, and conditional logic
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Application, Service, ViewModel, and Factory ---
# These are instantiated by the Android framework or ViewModelProvider via reflection
-keep class com.gronod.esp32aldldashboard.AldlApplication { *; }
-keep class com.gronod.esp32aldldashboard.bluetooth.BluetoothForegroundService { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModelProvider$Factory { *; }