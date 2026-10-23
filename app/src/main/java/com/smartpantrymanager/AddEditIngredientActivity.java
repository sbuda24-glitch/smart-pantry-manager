
package com.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText quantityInput;
    private EditText unitInput;
    private EditText expiryInput;

    private PantryDAO pantryDAO;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        nameInput = findViewById(R.id.ingredientNameInput);
        quantityInput = findViewById(R.id.ingredientQuantityInput);
        unitInput = findViewById(R.id.ingredientUnitInput);
        expiryInput = findViewById(R.id.ingredientExpiryInput);

        TextView screenTitle = findViewById(R.id.screenTitle);
        Button saveButton = findViewById(R.id.saveIngredientButton);
        Button cancelButton = findViewById(R.id.cancelIngredientButton);

        pantryDAO = new PantryDAO(this);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId > 0) {
            screenTitle.setText("Edit Ingredient");
            saveButton.setText("Update Ingredient");

            nameInput.setText(
                    getIntent().getStringExtra("ingredient_name"));

            quantityInput.setText(
                    getIntent().getStringExtra("ingredient_quantity"));

            unitInput.setText(
                    getIntent().getStringExtra("ingredient_unit"));

            expiryInput.setText(
                    getIntent().getStringExtra("ingredient_expiry"));
        }

        saveButton.setOnClickListener(v -> saveIngredient());

        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText =
                quantityInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            showMessage("Name, quantity and unit are required.");
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            showMessage("Enter a valid quantity.");
            return;
        }

        if (!Double.isFinite(quantity) || quantity < 0) {
            showMessage("Quantity must be a non-negative number.");
            return;
        }

        if (!isValidExpiryDate(expiry)) {
            showMessage("Use a valid date in YYYY-MM-DD format.");
            return;
        }

        if (ingredientId > 0) {
            int updated = pantryDAO.updateIngredient(
                    ingredientId, name, quantity, unit, expiry);

            if (updated > 0) {
                showMessage("Ingredient updated successfully.");
                finish();
            } else {
                showMessage("Could not update the ingredient.");
            }
        } else {
            long inserted = pantryDAO.addIngredient(
                    name, quantity, unit, expiry);

            if (inserted != -1) {
                showMessage("Ingredient added successfully.");
                finish();
            } else {
                showMessage("Could not add the ingredient.");
            }
        }
    }

    private boolean isValidExpiryDate(String expiry) {
        if (expiry == null || expiry.trim().isEmpty()) {
            return true;
        }

        String value = expiry.trim();

        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        Date date = format.parse(value, position);

        return date != null && position.getIndex() == value.length();
    }

    private void showMessage(String message) {
        Toast.makeText(
                this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (pantryDAO != null) {
            pantryDAO.close();
        }

        super.onDestroy();
    }
}
