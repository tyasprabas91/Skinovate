# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== Kotlin =====
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-dontwarn kotlin.**
-keepclassmembers class **$serializer {
    static ** INSTANCE;
}
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ===== Room Database =====
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Room entities and DAOs
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ===== Gson =====
# Keep Gson classes for JSON serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes that are serialized with Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ===== WorkManager =====
-keep class androidx.work.** { *; }
-keepclassmembers class androidx.work.** { *; }
-dontwarn androidx.work.**

# ===== Compose =====
-keep class androidx.compose.** { *; }
-keep class kotlin.coroutines.** { *; }

# ===== Navigation Compose =====
-keep class androidx.navigation.** { *; }

# ===== Google Play Services (Sign-In) =====
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ===== ML Kit Face Detection =====
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ===== CameraX =====
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ===== Data Classes (keep for reflection) =====
# Keep your data models if they're used with Gson or Room
-keep class com.example.skinovate.data.** { *; }
-keep class com.example.skinovate.data.database.** { *; }

# ===== Keep ViewModels =====
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModelProvider$Factory { *; }

# ===== Keep Application class =====
-keep class com.example.skinovate.MainActivity { *; }

# ===== Remove logging in release =====
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===== Keep Parcelable =====
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ===== Keep Native Methods =====
-keepclasseswithmembernames class * {
    native <methods>;
}

# ===== Remove debugging code =====
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
