-keep class com.hansholz.bestenotenapp.api.models.** { *; }

# Keep Ktor classes
-keep class io.ktor.** { *; }

# Keep necessary attributes
-keepattributes InnerClasses

# JNA classes
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.** { public *; }
-keep class * implements com.sun.jna.** { *; }

# JBR
-keep class com.jetbrains.JBR* { *; }
-keepnames class com.jetbrains.** { *; }

# For MaterialYou on Mac
-keep class dev.zwander.jfa.** { *; }

-dontwarn **
-dontnote **