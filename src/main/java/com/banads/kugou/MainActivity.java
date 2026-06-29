package com.banads.kugou;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private static final String PKG = "com.kugou.android";

    private static final String ACT_SPLASH =
	"com.kugou.android.app.splash.foresplash.ForeNoBgSplashActivity";

    private static final String ACT_COMMISSION =
	"com.kugou.android.splash.commission.preview.CommissionPreviewActivity";

    private static final String CMD_DISABLE =
	"pm disable " + PKG + "/" + ACT_SPLASH + "\n" +
	"pm disable " + PKG + "/" + ACT_COMMISSION;

    private static final String CMD_ENABLE =
	"pm enable " + PKG + "/" + ACT_SPLASH + "\n" +
	"pm enable " + PKG + "/" + ACT_COMMISSION;

    private static final String CMD_CHECK =
	"dumpsys package " + PKG +
	" | grep -E \"disabledComponents:|ForeNoBgSplashActivity|CommissionPreviewActivity\"";

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

					new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
							@Override
							public void run() {
								updateUI();
							}
						}, 300);
				}
			});

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        boolean disabled = isBothDisabled();

        tvStatus.setText(
			"ForeNoBgSplashActivity: "
			+ (disabled ? "已禁用喵" : "未禁用喵")
			+ "\nCommissionPreviewActivity: "
			+ (disabled ? "已禁用喵" : "未禁用喵")
			+ "\n\n禁用Act会停止应用进程喵，LSP里启用了模块的话直接打开酷狗就行了喵"
        );

        btnToggle.setText(
			disabled
			? "一键恢复这俩Act喵"
			: "一键禁用这俩Act喵");
    }

    private boolean isBothDisabled() {

        String result = execRootForResult(CMD_CHECK);

        return result.startsWith("disabledComponents:");
    }

    private void disableBoth() {

        boolean ok = execRoot(CMD_DISABLE);

        Toast.makeText(
			this,
			ok ? "已禁用喵" : "执行失败喵",
			Toast.LENGTH_LONG
        ).show();
    }

    private void enableBoth() {

        boolean ok = execRoot(CMD_ENABLE);

        Toast.makeText(
			this,
			ok ? "已恢复喵" : "执行失败喵",
			Toast.LENGTH_SHORT
        ).show();
    }

	private boolean execRoot(String cmds) {

        Process process = null;
        DataOutputStream os = null;

        try {

            process = Runtime.getRuntime().exec("su");
			
            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(cmds);
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();

            int exitCode = process.waitFor();

            return exitCode == 0;

        } catch (Throwable t) {

            Toast.makeText(
				this,
				"杂鱼，没授予su喵",
				Toast.LENGTH_LONG
            ).show();

            return false;

        } finally {

            try {
                if (os != null) {
                    os.close();
                }
            } catch (Throwable ignored) {
            }

            if (process != null) {
                process.destroy();
            }
        }
    }

    private String execRootForResult(String cmds) {

        Process process = null;
        DataOutputStream os = null;
        BufferedReader reader = null;

        try {

            process = Runtime.getRuntime().exec("su");

            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(cmds);
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();

            reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }

            process.waitFor();

            return result.toString().trim();

        } catch (Throwable t) {

            return "";

        } finally {

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable ignored) {
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (Throwable ignored) {
            }

            if (process != null) {
                process.destroy();
            }
        }
    }
}
