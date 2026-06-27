package com.banads.kugou;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"com.kugou.android".equals(lpparam.packageName)) return;

        XposedBridge.log("模块已加载");

        //禁用Act
        disableComponent("com.kugou.android.app.splash.foresplash.ForeNoBgSplashActivity");
        disableComponent("com.kugou.android.splash.commission.preview.CommissionPreviewActivity");

        //Hook od.a
        try {
            Class<?> odA = XposedHelpers.findClass("od.a", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(odA, "show", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) {
						param.setResult(null);
						XposedBridge.log("已拦截 od.a Dialog");
					}
				});
        } catch (Throwable t) {
            XposedBridge.log("Hook od.a 失败 " + t.getMessage());
        }
    }

    //禁用组件
    private void disableComponent(String className) {
        try {
            // 反射获取PackageManager
            PackageManager pm = (PackageManager) Class.forName("android.app.ActivityThread")
				.getMethod("currentApplication")
				.invoke(null).getClass()
				.getMethod("getPackageManager")
				.invoke(Class.forName("android.app.ActivityThread")
						.getMethod("currentApplication").invoke(null));

            ComponentName cn = new ComponentName("com.kugou.android", className);
            pm.setComponentEnabledSetting(
				cn,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP
            );
            XposedBridge.log("已禁用: " + className);
        } catch (Throwable t) {
            XposedBridge.log("禁用失败 " + className + ": " + t.getMessage());
        }
    }
}
