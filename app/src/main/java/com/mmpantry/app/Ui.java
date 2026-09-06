package com.mmpantry.app;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;

public final class Ui {
    public static final int GOLD = Color.rgb(184,148,85);
    public static boolean dark(Context c) { return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES; }
    public static int background(Context c) { return dark(c) ? Color.rgb(23,23,23) : Color.rgb(245,240,232); }
    public static int card(Context c) { return dark(c) ? Color.rgb(36,36,36) : Color.rgb(255,253,248); }
    public static int primary(Context c) { return dark(c) ? Color.rgb(245,240,232) : Color.rgb(32,32,32); }
    public static int secondary(Context c) { return dark(c) ? Color.rgb(180,173,164) : Color.rgb(116,110,102); }
    public static TextView text(Context c, String value, float size) { TextView t=new TextView(c); t.setText(value); t.setTextSize(size); t.setTextColor(primary(c)); t.setPadding(0,8,0,8); return t; }
    private Ui() {}
}
