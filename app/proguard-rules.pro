# ProGuard / R8 Rules for Lentera App

# Keep line numbers, annotations, signatures, and enclosing methods for reflection & crash stacktraces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault

# Kotlinx Serialization (Kotlin 2.x + R8 full mode)
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.PPBM.lentera.**$$serializer { *; }
-keepclassmembers class com.PPBM.lentera.** {
    *** Companion;
}
-keepclasseswithmembers class com.PPBM.lentera.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    <init>(...);
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Supabase Client, Auth Plugins & Multiplatform Settings (Session persistence)
-keep class io.github.jan.supabase.** { *; }
-keep interface io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**
-keep class com.russhwolf.settings.** { *; }
-dontwarn com.russhwolf.settings.**

# Ktor HTTP Client, Android Engine & ServiceLoader
-keep class io.ktor.** { *; }
-keep interface io.ktor.** { *; }
-keep class * implements io.ktor.client.HttpClientEngineContainer { *; }
-dontwarn io.ktor.**
-dontwarn org.slf4j.**

# Kotlinx Coroutines & DateTime
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.datetime.** { *; }
-dontwarn kotlinx.datetime.**

# AndroidX ViewModel, WorkManager & App Startup Initializers (called in MainActivity & LenteraNavGraph)
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.startup.Initializer {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
}
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.impl.** { *; }
-keep class androidx.work.WorkManagerInitializer { *; }
-keep class com.PPBM.lentera.core.notification.LenteraNotifWorker { *; }

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Models & DTOs
-keep class com.PPBM.lentera.backend.**.model.** { *; }
-keep class com.PPBM.lentera.features.**.model.** { *; }
