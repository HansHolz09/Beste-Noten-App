-dontwarn **

-keepclassmembers class * implements android.os.Parcelable {
        public static final ** CREATOR;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
