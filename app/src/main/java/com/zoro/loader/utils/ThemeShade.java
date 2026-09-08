package com.zoro.loader.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import com.zoro.loader.R;

import java.security.SecureRandom;

/** Applies a fresh, readable dark-purple palette whenever a loader screen opens. */
public final class ThemeShade {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Palette[] PALETTES = new Palette[] {
            new Palette("#090511", "#21103A", "#B36BFF", "#E6D2FF"),
            new Palette("#070412", "#26104B", "#9A70FF", "#E2D8FF"),
            new Palette("#0B0616", "#321047", "#D06CFF", "#F0D9FF"),
            new Palette("#050611", "#15205A", "#7D8CFF", "#D9DDFF"),
            new Palette("#100511", "#3D123D", "#F06BD4", "#FFD7F5")
    };

    private ThemeShade() { }

    public static void apply(Activity activity, int rootId) {
        View root = activity.findViewById(rootId);
        if (root == null) return;
        Palette palette = PALETTES[RANDOM.nextInt(PALETTES.length)];
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { Color.parseColor(palette.background), Color.parseColor(palette.surface) });
        root.setBackground(background);
        activity.getWindow().setStatusBarColor(Color.parseColor(palette.background));
        activity.getWindow().setNavigationBarColor(Color.parseColor(palette.background));
        recolor(root, palette);
    }

    public static void spin(View badge) {
        if (badge == null) return;
        ObjectAnimator rotation = ObjectAnimator.ofFloat(badge, View.ROTATION, 0f, 360f);
        rotation.setDuration(9000L);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();
    }

    private static void recolor(View view, Palette palette) {
        if (view instanceof Button) {
            boolean whiteAction = view.getId() == R.id.login || view.getId() == R.id.starthack;
            GradientDrawable button = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    whiteAction
                            ? new int[] { Color.parseColor("#FFFFFF"), Color.parseColor("#DDE5E4") }
                            : new int[] { Color.parseColor(palette.accent), Color.parseColor(palette.accentDark) });
            button.setCornerRadius(14f);
            view.setBackground(button);
        } else if (view instanceof EditText) {
            view.setBackground(panel(palette, 12f));
        } else if (!(view instanceof android.widget.ImageView)
                && !(view instanceof com.airbnb.lottie.LottieAnimationView)
                && view.getBackground() != null) {
            view.setBackground(panel(palette, 18f));
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) recolor(group.getChildAt(i), palette);
        }
    }

    private static GradientDrawable panel(Palette palette, float radius) {
        GradientDrawable panel = new GradientDrawable();
        panel.setColor(Color.parseColor(palette.surface));
        panel.setCornerRadius(radius);
        panel.setStroke(1, Color.parseColor(palette.border));
        return panel;
    }

    private static final class Palette {
        final String background;
        final String surface;
        final String accent;
        final String accentDark;
        final String border;

        Palette(String background, String surface, String accent, String accentDark) {
            this.background = background;
            this.surface = surface;
            this.accent = accent;
            this.accentDark = accentDark;
            this.border = accent;
        }
    }
}
