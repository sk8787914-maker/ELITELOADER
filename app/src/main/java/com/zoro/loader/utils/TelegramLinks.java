package com.zoro.loader.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public final class TelegramLinks {
    private static final String OWNER_URL = "https://t.me/ZOROADMINSERVER";
    private static final String CHANNEL_URL = "https://t.me/+2Z4f6fW-tMMwN2Rh";

    private TelegramLinks() { }

    public static void openOwner(Activity activity) {
        open(activity, OWNER_URL, "Owner DM");
    }

    public static void openChannel(Activity activity) {
        open(activity, CHANNEL_URL, "Channel");
    }

    private static void open(Activity activity, String url, String label) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(activity, label + " link open nahi hua", Toast.LENGTH_SHORT).show();
        }
    }
}
