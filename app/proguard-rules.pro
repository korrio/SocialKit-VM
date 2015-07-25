# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Mac/Downloads/adt-bundle-mac-x86_64-20140624/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class org.parceler.Parceler$$Parcels

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.*

-dontwarn rx.**

-dontwarn com.squareup.okhttp.**
-dontwarn java.nio.file.*

-keepattributes *Annotation*
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}