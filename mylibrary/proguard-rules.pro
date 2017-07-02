# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in N:\AndroidSDK/tools/proguard/proguard-android.txt
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

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

-keepclassmembers,allowoptimization enum * {
    public static **[] values(); public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class com.github.dozzatq.phoenix.auth.** { *; }
-keepclasseswithmembers class com.github.dozzatq.phoenix.cloudmessaging.FirebaseData { *; }

-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnCompleteListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnFailureListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnSuccessListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnUnionListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnTaskSuccessListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.OnTaskFailureListener { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.tasks.Extension { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.notification.PhoenixNotification { *;}
-keepclasseswithmembernames public interface com.github.dozzatq.phoenix.notification.OnActionComplete { *;}
-keepclasseswithmembernames public class com.github.dozzatq.phoenix.core.NotificationHandler { *;}

-keepattributes *Annotation*

-keep public class * {
    public protected *;
}

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}