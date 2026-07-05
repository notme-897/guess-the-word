# Add project specific Proguard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\dharm\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and targets by changing the proguardFiles
# settings in build.gradle.kts.

# Keep Jetpack Compose rules
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep kotlinx.serialization classes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keepclassmembers class * {
    *** Companion;
}

-keepclasseswithmembers class * {
    *** Companion;
}

-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

-keep class * implements kotlinx.serialization.KSerializer {
    *;
}

# Keep our navigation keys specifically
-keep class com.example.guesstheword.Splash { *; }
-keep class com.example.guesstheword.Login { *; }
-keep class com.example.guesstheword.Home { *; }
-keep class com.example.guesstheword.Gameplay { *; }
-keep class com.example.guesstheword.Profile { *; }
-keep class com.example.guesstheword.Settings { *; }
-keep class com.example.guesstheword.Onboarding { *; }
-keep class com.example.guesstheword.LevelComplete { *; }

