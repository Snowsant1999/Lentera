# ProGuard / R8 Rules for Lentera App

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*, InnerClasses, Signature

# Kotlinx Serialization
-dontnote kotlinx.serialization.SerializationKt
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
}
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    <init>(...);
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Supabase Client & Plugins
-keep class io.github.jan.supabase.** { *; }
-keep interface io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# Ktor HTTP Client & Engine
-keep class io.ktor.** { *; }
-keep interface io.ktor.** { *; }
-dontwarn io.ktor.**

# Models & DTOs
-keep class com.PPBM.lentera.backend.**.model.** { *; }
-keep class com.PPBM.lentera.features.**.model.** { *; }
