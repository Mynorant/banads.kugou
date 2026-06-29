package com.banads.kugou;

import android.content.DialogInterface;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.kugou.android".equals(lpparam.packageName)) return;

        XposedBridge.log("酷狗去广告 已加载喵");

        try {
            XposedHelpers.findAndHookMethod(
				"od.a",
				lpparam.classLoader,
				"show",
				new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) {
						final android.app.Dialog dialog = (android.app.Dialog) param.thisObject;

						dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface di) {
                                    dialog.dismiss();
                                    XposedBridge.log("酷狗去广告 od.a 已关闭喵");
                                }
                            });
					}
				}
            );
        } catch (Throwable t) {
            XposedBridge.log("酷狗去广告 Hook 失败喵: " + t.getMessage());
        }
    }
}
