package com.banads.kugou;  

import android.app.Activity;  
import android.content.ComponentName;  
import android.content.pm.PackageManager;  
import android.os.Bundle;  
import android.view.Gravity;  
import android.widget.Button;  
import android.widget.LinearLayout;  
import android.widget.TextView;  
import android.widget.Toast;  
import android.view.View;  

import java.io.DataOutputStream;  

public class MainActivity extends Activity {  

    private static final String PKG = "com.kugou.android";  
    private static final String ACT_SPLASH =  
	"com.kugou.android.app.splash.foresplash.ForeNoBgSplashActivity";  
    private static final String ACT_COMMISSION =  
	"com.kugou.android.splash.commission.preview.CommissionPreviewActivity";  

    private TextView tvStatus;  
    private Button btnToggle;  

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
		if (android.os.Build.VERSION.SDK_INT >= 35) {
			getWindow().setDecorFitsSystemWindows(false);
		}
		setContentView(R.layout.activity_main);
		tvStatus = findViewById(R.id.tvStatus);
		btnToggle = findViewById(R.id.btnToggle);
		btnToggle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (isBothDisabled()) {
						enableBoth();
					} else {
						disableBoth();
					}

					updateUI();
				}
			});
    }  

    @Override  
    protected void onResume() {  
        super.onResume();  
        updateUI();  
    }  

    private void updateUI() {  
        boolean s = isDisabled(ACT_SPLASH);  
        boolean c = isDisabled(ACT_COMMISSION);  

        tvStatus.setText(  
			"ForeNoBgSplashActivity: " + (s ? "已禁用喵" : "未禁用喵")  
			+ "\nCommissionPreviewActivity: " + (c ? "已禁用喵" : "未禁用喵")  
			+ "\n\n禁用Act会停止应用进程喵，LSP里启用了模块的话直接打开酷狗就行了喵"  
        );  

        btnToggle.setText(isBothDisabled() ? "一键恢复这俩Act喵" : "一键禁用这俩Act喵");  
    }  

    private boolean isBothDisabled() {  
        return isDisabled(ACT_SPLASH) && isDisabled(ACT_COMMISSION);  
    }  

    private boolean isDisabled(String className) {  
        try {  
            return getPackageManager().getComponentEnabledSetting(  
				new ComponentName(PKG, className))  
				== PackageManager.COMPONENT_ENABLED_STATE_DISABLED;  
        } catch (Throwable t) {  
            return false;  
        }  
    }  

    private void disableBoth() {  
        su("pm disable " + PKG + "/" + ACT_SPLASH);  
        su("pm disable " + PKG + "/" + ACT_COMMISSION);  
        Toast.makeText(this, "已禁用喵", Toast.LENGTH_LONG).show();  
    }  

    private void enableBoth() {  
        su("pm enable " + PKG + "/" + ACT_SPLASH);  
        su("pm enable " + PKG + "/" + ACT_COMMISSION);  
        Toast.makeText(this, "已恢复喵", Toast.LENGTH_SHORT).show();  
    }  

    private void su(String cmd) {  
        try {  
            Process p = Runtime.getRuntime().exec("su");  
            DataOutputStream os = new DataOutputStream(p.getOutputStream());  
            os.writeBytes(cmd + "\n");  
            os.writeBytes("exit\n");  
            os.flush();  
            p.waitFor();  
        } catch (Throwable t) {  
            Toast.makeText(this, "杂鱼，没授予su喵", Toast.LENGTH_LONG).show();  
        }  
    }  
}
