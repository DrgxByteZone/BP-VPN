# Proguard rules for BP VPN
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
