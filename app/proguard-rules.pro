# ProGuard rules for ESP32 ALDL Dashboard
# Compose + Room + Kotlin Serialization + Navigation3

# --- General ---
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep class * { @androidx.room.* <fields>; }
-dontwarn androidx.room.paging.**

# --- Kotlin Serialization ---
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
    @kotlinx.serialization.Serializable <fields>;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static **$$serializerInstance;
   kotlinx.serialization.KSerializer serializer(...);
}

# --- Compose / Navigation ---
-keep class androidx.navigation3.** { *; }
-keep class * implements androidx.navigation3.NavigationUi { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# --- DataStore / Preferences ---
-keep class androidx.datastore.** { *; }
-keep class * implements androidx.datastore.core.Serializer { *; }

# --- Application classes (auto-generated, keep to be safe) ---
-keep class com.gronod.esp32aldldashboard.** { *; }
-keepclassmembers class com.gronod.esp32aldldashboard.** { *; }
