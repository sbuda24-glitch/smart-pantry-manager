
package com.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedButton = findViewById(R.id.btnGetStarted);

        getStartedButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    PantryActivity.class);
            startActivity(intent);
        });
    }
}
