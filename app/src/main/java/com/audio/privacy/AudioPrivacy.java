package com.audio.privacy;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;


public class AudioPrivacy implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static boolean hide_text;
    private static XSharedPreferences prefs;
    private static boolean enabled;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable
    {
        XposedBridge.log("Loading AudioPrivacy...");

    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.android.systemui")) {
            Class psbClass = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader);
            findAndHookMethod(psbClass, "updateMediaMetaData", boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    return null;
                }
            });
        }
    }
}
