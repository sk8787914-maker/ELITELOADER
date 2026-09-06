package com.zoro.loader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.zoro.loader.utils.FLog;
import com.elite.EliteInstaller;
import com.elite.app.configuration.ClientConfiguration;
import net_62v.external.MetaActivationManager;

public class BoxApplication extends Application {
    public static BoxApplication gApp;

    private native String BoxApp();

    public static BoxApplication get() {
        return gApp;
    }

    static {
        try {
            System.loadLibrary("MCoreEsp");
        } catch (UnsatisfiedLinkError w) {
            FLog.error(w.getMessage() != null ? w.getMessage() : "Load lib error");
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            EliteInstaller.get().doAttachBaseContext(base, new ClientConfiguration() {
                public String getHostPackageName() { return base.getPackageName(); }
                public boolean isHideRoot() { return true; }
                public boolean isHideXposed() { return true; }
                public boolean isEnableDaemonService() { return true; }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gApp = this;
        try {
            EliteInstaller.get().doCreate();
            
            String key = BoxApp(); 
            Log.d("LICENSE_DEBUG", "KEY FROM JNI: " + key);
            
            if (key != null && !key.isEmpty()) {
                MetaActivationManager.activateSdk(key);
                
                // ✅ Poll for up to 10 seconds (20 attempts * 500ms)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    int attempts = 0;
                    @Override
                    public void run() {
                        if (MetaActivationManager.getActivatedStatus()) {
                            Log.d("LICENSE_DEBUG", "Activation SUCCESS");
                        } else if (attempts >= 20) {
                            Toast.makeText(BoxApplication.this, "Elite-SDK-Activation-Failed", Toast.LENGTH_LONG).show();
                            Log.e("LICENSE_DEBUG", "Activation failed after 10 seconds. Key: " + key);
                        } else {
                            attempts++;
                            new Handler(Looper.getMainLooper()).postDelayed(this, 500);
                        }
                    }
                });
            } else {
                Log.e("LICENSE_DEBUG", "Key is null or empty from JNI!");
                MetaActivationManager.activateSdk("");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            FLog.error("SDK Init Failed: " + exception.getMessage());
        }
    }
}