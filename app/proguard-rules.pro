# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep iText classes
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
