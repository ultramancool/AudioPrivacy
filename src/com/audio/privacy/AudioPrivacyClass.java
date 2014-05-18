package com.audio.privacy;

import android.graphics.Bitmap;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.media.MediaMetadataRetriever;

public class AudioPrivacyClass implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedBridge.log("Loaded app: " + lpparam.packageName);
		findAndHookMethod(
				android.media.RemoteControlClient.MetadataEditor.class,
				"putBitmap", int.class, Bitmap.class,
				new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return param.thisObject;
					}
				});

		findAndHookMethod(
				android.media.RemoteControlClient.MetadataEditor.class,
				"putString", int.class, String.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
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
				});
	}

}