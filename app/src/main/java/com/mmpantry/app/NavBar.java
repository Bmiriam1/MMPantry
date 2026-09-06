package com.mmpantry.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public final class NavBar {
    // Builds a simple bottom navigation bar with three buttons: Pantry, Recipes, Settings.
    // Highlights the current screen and navigates via Intent when another is tapped.
    public static LinearLayout build(final Activity activity, String current) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setPadding(0, 16, 0, 16);
        bar.setBackgroundColor(Ui.card(activity));

        bar.addView(button(activity, "PANTRY", PantryActivity.class, current.equals("pantry")));
        bar.addView(button(activity, "RECIPES", RecipesActivity.class, current.equals("recipes")));
        bar.addView(button(activity, "SETTINGS", SettingsActivity.class, current.equals("settings")));

        return bar;
    }

    private static Button button(final Activity activity, String label, final Class<?> target, boolean isCurrent) {
        Button b = new Button(activity);
        b.setText(label);
        b.setTextColor(isCurrent ? Color.WHITE : Ui.secondary(activity));
        b.setBackgroundColor(isCurrent ? Ui.GOLD : Ui.card(activity));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        b.setLayoutParams(p);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!activity.getClass().equals(target)) {
                    activity.startActivity(new Intent(activity, target));
                }
            }
        });
        return b;
    }

    private NavBar() {}
}