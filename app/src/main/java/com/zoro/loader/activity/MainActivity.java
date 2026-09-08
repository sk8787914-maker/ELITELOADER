package com.zoro.loader.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zoro.loader.R;
import com.zoro.loader.libhelper.FileCopyTask;
import com.zoro.loader.utils.ThemeShade;
import com.zoro.loader.utils.TelegramLinks;
import com.elite.EliteInstaller;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import net_62v.external.MetaActivationManager;
import android.MetaCore.AdvancedPopupHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    public static native String TimeExpired();

    static {
        try {
            System.loadLibrary("MCoreEsp");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private static final String BGMI_PACKAGE = "com.pubg.imobile";
    private static final int USER_ID = 0;

    private EliteInstaller eliteInstaller;
    private FileCopyTask fileCopyTask;
    private LottieAnimationView backgroundAnimation;
    private Button starthack, stophack;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeShade.apply(this, R.id.mainRoot);
        ThemeShade.spin(findViewById(R.id.teamBadge));
        findViewById(R.id.ownerLink).setOnClickListener(v -> TelegramLinks.openOwner(this));
        findViewById(R.id.channelLink).setOnClickListener(v -> TelegramLinks.openChannel(this));

        // Initialize views
        backgroundAnimation = findViewById(R.id.backgroundAnimation);
        starthack = findViewById(R.id.starthack);
        stophack = findViewById(R.id.stophack);

        // Setup Lottie Animation
        if (backgroundAnimation != null) {
            backgroundAnimation.setSpeed(0.8f);
            backgroundAnimation.setRepeatCount(LottieDrawable.INFINITE);
            backgroundAnimation.playAnimation();
        }

        // Initialize helpers
        eliteInstaller = EliteInstaller.get();
        fileCopyTask = new FileCopyTask(this);

        // Check activation status
        checkActivationAndUpdateUI();

        // Start countdown timer
        countDownStart();

        // Set click listeners
        starthack.setOnClickListener(view -> handleStart());
        stophack.setOnClickListener(view -> handleStop());
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Resume animation
        if (backgroundAnimation != null && !backgroundAnimation.isAnimating()) {
            backgroundAnimation.resumeAnimation();
        }

        // Re-check activation on resume
        checkActivationAndUpdateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Pause animation
        if (backgroundAnimation != null && backgroundAnimation.isAnimating()) {
            backgroundAnimation.pauseAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Cleanup animation
        if (backgroundAnimation != null) {
            backgroundAnimation.cancelAnimation();
            backgroundAnimation.clearAnimation();
            backgroundAnimation = null;
        }
    }

    /**
     * Check activation status and update UI accordingly
     */
    private void checkActivationAndUpdateUI() {
        boolean isActivated = MetaActivationManager.getActivatedStatus();
        
        if (!isActivated) {
            showBlockingPopup();
            if (starthack != null) starthack.setEnabled(false);
            if (stophack != null) stophack.setEnabled(false);
        } else {
            if (starthack != null) starthack.setEnabled(true);
            if (stophack != null) stophack.setEnabled(true);
        }
    }

    /**
     * Show popup when license is expired
     */
    private void showBlockingPopup() {
        try {
            // Option 1: Try without parameters (if method exists)
            // AdvancedPopupHelper.showAuto();
            
            // Option 2: Using AlertDialog (safer and works always)
            new AlertDialog.Builder(this)
                .setTitle("License Status")
                .setMessage("❌ LICENSE EXPIRED / ACCESS REVOKED\n\nPlease contact support for activation.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Optionally finish activity or do nothing
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
                
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback toast
            Toast.makeText(this, "License expired! Contact support.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle Start button click
     */
    private void handleStart() {
        if (!MetaActivationManager.getActivatedStatus()) {
            showBlockingPopup();
            return;
        }
        
        if (isPackageInstalled(BGMI_PACKAGE, USER_ID)) {
            copyObbFilesAndLaunch();
        } else {
            installGame();
        }
    }

    /**
     * Handle Stop button click
     */
    private void handleStop() {
        if (!MetaActivationManager.getActivatedStatus()) {
            showBlockingPopup();
            return;
        }
        
        try {
            eliteInstaller.uninstallPackageAsUser(BGMI_PACKAGE, USER_ID);
            Toast.makeText(this, "Game Uninstalled From Container", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Uninstall Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check if package is installed
     */
    private boolean isPackageInstalled(String packageName, int userId) {
        try {
            return eliteInstaller.isInstalled(packageName, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Install game in container
     */
    private void installGame() {
        Toast.makeText(this, "Installing In Container...", Toast.LENGTH_SHORT).show();
        
        try {
            eliteInstaller.installPackageAsUser(BGMI_PACKAGE, USER_ID);
            
            new Handler().postDelayed(() -> {
                copyObbFilesAndLaunch();
            }, 2000);
            
        } catch (Exception e) {
            Toast.makeText(this, "Installation Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copy OBB files and launch game
     */
    private void copyObbFilesAndLaunch() {
        fileCopyTask.copyObbFolderAsync(BGMI_PACKAGE, success -> {
            launchGame();
        });
    }

    /**
     * Launch the game
     */
    private void launchGame() {
        try {
            eliteInstaller.launchApk(BGMI_PACKAGE, USER_ID);
        } catch (Exception e) {
            Toast.makeText(this, "Launch Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Countdown timer for license expiry
     */
    private void countDownStart() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String expiryStr = TimeExpired();
                    
                    if (expiryStr != null && !expiryStr.isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date expiryDate = dateFormat.parse(expiryStr);
                        
                        if (expiryDate != null) {
                            long distance = expiryDate.getTime() - System.currentTimeMillis();
                            
                            if (distance > 0) {
                                long days = distance / (24 * 60 * 60 * 1000);
                                long hours = (distance / (60 * 60 * 1000)) % 24;
                                long minutes = (distance / (60 * 1000)) % 60;
                                long seconds = (distance / 1000) % 60;
                                
                                runOnUiThread(() -> {
                                    TextView tvD = findViewById(R.id.tv_d);
                                    TextView tvH = findViewById(R.id.tv_h);
                                    TextView tvM = findViewById(R.id.tv_m);
                                    TextView tvS = findViewById(R.id.tv_s);
                                    
                                    if (tvD != null) tvD.setText(String.format(Locale.getDefault(), "%02d", days));
                                    if (tvH != null) tvH.setText(String.format(Locale.getDefault(), "%02d", hours));
                                    if (tvM != null) tvM.setText(String.format(Locale.getDefault(), "%02d", minutes));
                                    if (tvS != null) tvS.setText(String.format(Locale.getDefault(), "%02d", seconds));
                                });
                            } else {
                                // License expired - update UI
                                runOnUiThread(() -> {
                                    TextView tvD = findViewById(R.id.tv_d);
                                    TextView tvH = findViewById(R.id.tv_h);
                                    TextView tvM = findViewById(R.id.tv_m);
                                    TextView tvS = findViewById(R.id.tv_s);
                                    
                                    if (tvD != null) tvD.setText("00");
                                    if (tvH != null) tvH.setText("00");
                                    if (tvM != null) tvM.setText("00");
                                    if (tvS != null) tvS.setText("00");
                                    
                                    // Disable buttons if license expired
                                    if (starthack != null) starthack.setEnabled(false);
                                    if (stophack != null) stophack.setEnabled(false);
                                });
                            }
                        }
                    }
                    
                    handler.postDelayed(this, 1000);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        handler.postDelayed(runnable, 0);
    }
}
