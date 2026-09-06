# ========== GENERAL ==========
-printconfiguration build/outputs/mapping/release/configuration.txt
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.**
-dontwarn javax.lang.model.**

# ========== KEEP APPLICATION & CORE PACKAGES ==========
-keep class com.zoro.loader.** { *; }
-keep class com.elite.** { *; }
-keep class net_62v.external.** { *; }
-keep class android.MetaCore.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.** { *; }

# ========== NATIVE METHODS (JNI) ==========
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keep class _lxy_oxor_any_ { *; }
-keepclassmembers class _lxy_oxor_any_ {
    static <methods>;
}

# ========== LIBRARY SPECIFIC ==========

# BlackReflection & FreeReflection
-keep class com.github.tiann.FreeReflection.** { *; }
-keep class com.github.CodingGay.BlackReflection.** { *; }
-keep class me.weishu.reflection.** { *; }

# OkHttp & Okio
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.conscrypt.**

# Zip4j
-keep class net.lingala.zip4j.** { *; }

# JDeferred
-keep class org.jdeferred.** { *; }

# PathSelector
-keep class io.github.molihuan.pathselector.** { *; }
-keep class com.blankj.molihuan.** { *; }
-keep class com.blankj.utilcode.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.load.resource.bitmap.*

# Lottie
-keep class com.airbnb.lottie.** { *; }

# StateView
-keep class com.github.nukc.StateView.** { *; }

# SLF4J
-keep class org.slf4j.** { *; }
-keep class org.slf4j.impl.** { *; }

# GSON (required by pathselector)
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.stream.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**

# JSON for native (nlohmann)
-keep class nlohmann.** { *; }

# OpenSSL / cURL
-dontwarn backends.openssl.**
-dontwarn backends.curl.**

# ========== ANDROID COMPONENTS ==========
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.Application
-keep class * extends android.app.Fragment
-keep class * extends androidx.fragment.app.Fragment

-keepattributes *Annotation*, Signature, SourceFile, LineNumberTable
-keepattributes EnclosingMethod

# Parcelable & Serializable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep R resources
-keep class **.R$* {
    <fields>;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ========== SUPPRESS NON-CRITICAL WARNINGS ==========
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.xml.bind.**
-dontwarn org.checkerframework.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn org.xmlpull.v1.**
-dontwarn com.stericson.RootTools.**

# ========== OPTIMIZATION TWEAKS ==========
-keepattributes LocalVariableTable, LocalVariableTypeTable
-keep,allowshrinking class com.android.vending.licensing.ILicensingService

# ========== FALLBACK: Keep all classes in libs folder ==========
-keep class !android.support.**, !androidx.**, !com.google.**, !org.slf4j.**, !org.jdeferred.**, !com.squareup.**, !okio.**, !net.lingala.**, !io.github.molihuan.**, !com.github.**, !com.airbnb.**, !nlohmann.**, !backends.** {
    public *;
}