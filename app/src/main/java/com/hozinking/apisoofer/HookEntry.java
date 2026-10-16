package com.hozinking.apisoofer;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * LSPosed module: spoof Android API level for old games/apps.
 *
 * Many old 32-bit games check Build.VERSION.SDK_INT and either refuse to
 * run or take broken code paths on new Android versions. This module makes
 * the app see a fake (older) API level.
 *
 * What is spoofed:
 *   - Build.VERSION.SDK_INT  -> 28 (Android 9)
 *   - Build.VERSION.RELEASE  -> "9"
 *   - Build.VERSION.CODENAME -> "REL" (not a preview)
 *
 * Why SDK 28: it's the last API before scoped storage (29) and the new
 * permission model changes, so old games behave most compatibly there.
 * Most games that do `if (SDK_INT >= 29)` checks will take their legacy path.
 *
 * Scope: enable this module in LSPosed Manager and check ONLY the old
 * games you want to spoof. Do NOT enable it system-wide.
 *
 * No UI, no launcher icon: configured purely via LSPosed Manager.
 */
public class HookEntry implements IXposedHookLoadPackage {

    private static final String TAG = "[ApiSpoofer]";

    // Fake API level: 28 = Android 9 (Pie)
    private static final int FAKE_SDK_INT = 28;
    private static final String FAKE_RELEASE = "9";
    private static final String FAKE_CODENAME = "REL";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Don't hook ourselves or the system
        if (lpparam.packageName.equals("com.hozinking.apisoofer") ||
            lpparam.packageName.equals("android")) {
            return;
        }

        XposedBridge.log(TAG + " loaded in " + lpparam.packageName +
                         ", spoofing SDK_INT=" + FAKE_SDK_INT);

        // Hook Build.VERSION.SDK_INT (static int field)
        try {
            XposedHelpers.setStaticObjectField(
                Build.VERSION.class, "SDK_INT", FAKE_SDK_INT);
            XposedBridge.log(TAG + " SDK_INT spoofed");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " failed to spoof SDK_INT: " + t);
        }

        // Hook Build.VERSION.RELEASE (static String field)
        try {
            XposedHelpers.setStaticObjectField(
                Build.VERSION.class, "RELEASE", FAKE_RELEASE);
            XposedBridge.log(TAG + " RELEASE spoofed");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " failed to spoof RELEASE: " + t);
        }

        // Hook Build.VERSION.CODENAME
        try {
            XposedHelpers.setStaticObjectField(
                Build.VERSION.class, "CODENAME", FAKE_CODENAME);
            XposedBridge.log(TAG + " CODENAME spoofed");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " failed to spoof CODENAME: " + t);
        }

        // Also hook getPreviewSDKInt() for apps that check preview status
        try {
            XposedHelpers.findAndHookMethod(
                Build.VERSION.class, "getPreviewSDKInt",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(0);
                    }
                });
        } catch (Throwable t) {
            // Method may not exist on all versions, not fatal
            XposedBridge.log(TAG + " getPreviewSDKInt hook skipped: " + t.getMessage());
        }
    }
}
