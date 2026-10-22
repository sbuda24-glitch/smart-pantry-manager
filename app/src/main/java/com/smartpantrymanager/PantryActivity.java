package com.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class PantryActivity extends AppCompatActivity {

    private EditText ingredientName;
    private EditText ingredientQuantity;
    private EditText ingredientUnit;
    private EditText ingredientExpiry;

    private ListView ingredientList;
    private PantryDAO pantryDAO;
    private IngredientAdapter adapter;
    private ArrayList<String> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        Button suggestedRecipesButton =
                findViewById(R.id.suggestedRecipesButton);

        Button settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        suggestedRecipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        ingredientName = findViewById(R.id.ingredientName);
        ingredientQuantity = findViewById(R.id.ingredientQuantity);
        ingredientUnit = findViewById(R.id.ingredientUnit);
        ingredientExpiry = findViewById(R.id.ingredientExpiry);
        ingredientList = findViewById(R.id.ingredientList);

        Button addButton = findViewById(R.id.addIngredientButton);

        pantryDAO = new PantryDAO(this);
        ingredients = new ArrayList<>();

        adapter = new IngredientAdapter(this, ingredients);
        ingredientList.setAdapter(adapter);

        addButton.setOnClickListener(view -> addIngredient());

        ingredientList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedIngredient = ingredients.get(position);
            showIngredientOptions(selectedIngredient);
        });

        loadIngredients();
    }

    private void addIngredient() {
        String name = ingredientName.getText().toString().trim();
        String quantityText =
                ingredientQuantity.getText().toString().trim();
        String unit = ingredientUnit.getText().toString().trim();
        String expiryDate = ingredientExpiry.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            showMessage("Enter the ingredient name, quantity and unit.");
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
            showMessage("Quantity must be a valid, non-negative number.");
            return;
        }

        long result = pantryDAO.addIngredient(
                name, quantity, unit, expiryDate
        );

        if (result != -1) {
            showMessage("Ingredient added successfully.");

            ingredientName.setText("");
            ingredientQuantity.setText("");
            ingredientUnit.setText("");
            ingredientExpiry.setText("");

            loadIngredients();
        } else {
            showMessage("Could not add the ingredient.");
        }
    }

    private void loadIngredients() {
        ingredients.clear();
        ingredients.addAll(pantryDAO.getAllIngredients());
        adapter.notifyDataSetChanged();
    }

    private void showIngredientOptions(String item) {
        String[] parts = item.split("\\s*\\|\\s*", 4);

        if (parts.length < 4) {
            showMessage("Could not read this ingredient.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(parts[1])
                .setItems(new String[]{"Edit ingredient", "Delete ingredient"},
                        (dialog, which) -> {
                            if (which == 0) {
                                showEditDialog(parts);
                            } else {
                                confirmDelete(parts[0], parts[1]);
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(String[] parts) {
        String[] quantityAndUnit = parts[2].trim().split("\\s+", 2);

        if (quantityAndUnit.length < 2) {
            showMessage("Could not read the quantity and unit.");
            return;
        }

        int ingredientId;

        try {
            ingredientId = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            showMessage("Could not read the ingredient ID.");
            return;
        }

        String expiry = parts[3].replaceFirst("^Expires:\\s*", "");

        if (expiry.equals("Not set")) {
            expiry = "";
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding / 2, padding, 0);

        EditText nameField = createField("Ingredient name",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        nameField.setText(parts[1]);

        EditText quantityField = createField("Quantity",
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        quantityField.setText(quantityAndUnit[0]);

        EditText unitField = createField("Unit",
                InputType.TYPE_CLASS_TEXT);
        unitField.setText(quantityAndUnit[1]);

        EditText expiryField = createField("Expiry date (YYYY-MM-DD, optional)",
                InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        expiryField.setText(expiry);

        layout.addView(nameField);
        layout.addView(quantityField);
        layout.addView(unitField);
        layout.addView(expiryField);

        new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameField.getText().toString().trim();
                    String quantityText =
                            quantityField.getText().toString().trim();
                    String unit = unitField.getText().toString().trim();
                    String newExpiry =
                            expiryField.getText().toString().trim();

                    if (name.isEmpty() || quantityText.isEmpty()
                            || unit.isEmpty()) {
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
                        showMessage("Quantity must be non-negative.");
                        return;
                    }

                    int result = pantryDAO.updateIngredient(
                            ingredientId, name, quantity, unit, newExpiry
                    );

                    if (result > 0) {
                        showMessage("Ingredient updated.");
                        loadIngredients();
                    } else {
                        showMessage("Ingredient was not updated.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private EditText createField(String hint, int inputType) {
        EditText field = new EditText(this);
        field.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        field.setHint(hint);
        field.setInputType(inputType);
        return field;
    }

    private void confirmDelete(String idText, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        int id = Integer.parseInt(idText.trim());
                        int result = pantryDAO.deleteIngredient(id);

                        if (result > 0) {
                            showMessage("Ingredient deleted.");
                            loadIngredients();
                        } else {
                            showMessage("Ingredient was not deleted.");
                        }
                    } catch (NumberFormatException e) {
                        showMessage("Could not identify the ingredient.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryDAO != null) {
            pantryDAO.close();
        }
    }
}
