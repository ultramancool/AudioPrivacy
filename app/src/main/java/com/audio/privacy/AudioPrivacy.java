package com.audio.privacy;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


public class AudioPrivacy implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static boolean hide_text;
    private static XSharedPreferences prefs;
    private static boolean enabled;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable
    {
        XposedBridge.log("Loading AudioPrivacy...");
        prefs = new XSharedPreferences(SettingsActivity.class.getPackage().getName());
        prefs.makeWorldReadable();
        updatePreferences(prefs);

    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        findAndHookMethod(
                android.media.RemoteControlClient.MetadataEditor.class,
                "putBitmap", int.class, Bitmap.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        prefs = new XSharedPreferences(SettingsActivity.class.getPackage().getName());
                        updatePreferences(prefs);
                        if (enabled)
                            param.args[1] = null;
                    }
                }
        );

        findAndHookMethod(
                android.media.RemoteControlClient.MetadataEditor.class,
                "putString", int.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        prefs = new XSharedPreferences(SettingsActivity.class.getPackage().getName());
                        updatePreferences(prefs);
                        if (hide_text) {
                            int key = (Integer) param.args[0];
                            switch (key) {
                                case MediaMetadataRetriever.METADATA_KEY_ALBUM:
                                case MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST:
                                case MediaMetadataRetriever.METADATA_KEY_AUTHOR:
                                    param.args[1] = null;
                                    break;
                                case MediaMetadataRetriever.METADATA_KEY_TITLE:
                                    param.args[1] = "Audio";
                                    break;
                            }
                        }
                    }
                });
    }

    public static void updatePreferences(SharedPreferences prefs)
    {
        hide_text = prefs.getBoolean("hide_text_checkbox", true);
        enabled = prefs.getBoolean("enable_checkbox", true);
    }
}
