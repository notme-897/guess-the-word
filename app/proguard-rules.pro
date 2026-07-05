# Add project specific Proguard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\dharm\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and targets by changing the proguardFiles
# settings in build.gradle.kts.

# Keep Jetpack Compose rules
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
