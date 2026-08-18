# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Kotlinx Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Room (if used)
#-keep class * extends androidx.room.RoomDatabase { *; }

# Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.coroutines.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }

# Keep model classes
-keep class com.qibla.compass.data.** { *; }
-keep class com.qibla.compass.domain.** { *; }
-keep class com.qibla.compass.ui.** { *; }

# Keep Hilt generated classes
-keep class * extends dagger.hilt.android.HiltViewModel { *; }
-keep class * extends dagger.hilt.android.HiltActivity { *; }
-keep class * extends dagger.hilt.android.HiltApplication { *; }