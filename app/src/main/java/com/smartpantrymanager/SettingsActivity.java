
package com.smartpantrymanager;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.TOP);
        layout.setBackgroundColor(Color.parseColor("#F5F7EF"));

        TextView title = new TextView(this);
        title.setText("Settings / Profile");
        title.setTextSize(26);
        title.setTextColor(Color.parseColor("#285943"));
        title.setPadding(0, 0, 0, 24);
        layout.addView(title);

        TextView description = new TextView(this);
        description.setText(
                "Smart Pantry Manager\n\n" +
                        "Database: SQLite\n" +
                        "Manage your pantry ingredients and " +
                        "find recipes using ingredients you have."
        );
        description.setTextSize(16);
        description.setTextColor(Color.DKGRAY);
        layout.addView(description);

        Button backButton = new Button(this);
        backButton.setText("Back to Pantry");
        backButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#285943")
                )
        );
        backButton.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        buttonParams.topMargin = 24;
        layout.addView(backButton, buttonParams);

        backButton.setOnClickListener(view -> finish());

        setContentView(layout);
    }
}
