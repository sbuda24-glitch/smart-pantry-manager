
package com.smartpantrymanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private EditText ingredientName;
    private EditText ingredientQuantity;
    private EditText ingredientUnit;
    private EditText ingredientExpiry;
    private ListView ingredientList;

    private PantryDAO pantryDAO;
    private IngredientAdapter ingredientAdapter;
    private ArrayList<String> ingredients;

    @Override
    protected void onResume() {
        super.onResume();

        if (pantryDAO != null && ingredientAdapter != null) {
            loadIngredients();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        ingredientName = findViewById(R.id.ingredientName);
        ingredientQuantity = findViewById(R.id.ingredientQuantity);
        ingredientUnit = findViewById(R.id.ingredientUnit);
        ingredientExpiry = findViewById(R.id.ingredientExpiry);
        ingredientList = findViewById(R.id.ingredientList);

        Button addButton = findViewById(R.id.addIngredientButton);
        Button suggestedRecipesButton = findViewById(R.id.suggestedRecipesButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        pantryDAO = new PantryDAO(this);
        ingredients = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(this, ingredients);
        ingredientList.setAdapter(ingredientAdapter);

        loadIngredients();

        addButton.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            PantryActivity.this,
                            AddEditIngredientActivity.class);

            startActivity(intent);
        });

        ingredientList.setOnItemClickListener((parent, view, position, id) ->
                showIngredientOptions(position));

        suggestedRecipesButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        PantryActivity.this, SuggestedRecipesActivity.class)));

        settingsButton.setOnClickListener(v ->
                startActivity(new android.content.Intent(
                        PantryActivity.this, SettingsActivity.class)));
    }

    private void addIngredient() {
        String name = ingredientName.getText().toString().trim();
        String quantityText = ingredientQuantity.getText().toString().trim();
        String unit = ingredientUnit.getText().toString().trim();
        String expiryDate = ingredientExpiry.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            showMessage("Please enter the ingredient name, quantity and unit.");
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid quantity.");
            return;
        }

        if (!Double.isFinite(quantity) || quantity < 0) {
            showMessage("Quantity must be a non-negative number.");
            return;
        }

        if (!isValidExpiryDate(expiryDate)) {
            showMessage("Enter a valid expiry date in YYYY-MM-DD format.");
            return;
        }

        long inserted = pantryDAO.addIngredient(
                name, quantity, unit, expiryDate);

        if (inserted != -1) {
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
        ingredientAdapter.notifyDataSetChanged();
    }

    private void showIngredientOptions(int position) {
        if (position < 0 || position >= ingredients.size()) {
            return;
        }

        String item = ingredients.get(position);
        String[] parts = item.split("\\s*\\|\\s*", 4);

        if (parts.length < 3) {
            showMessage("Could not read this ingredient.");
            return;
        }

        String idText = parts[0].trim();
        String name = parts[1].trim();
        String quantityAndUnit = parts[2].trim();

        String expiryText = "";
        if (parts.length >= 4) {
            expiryText = parts[3].replace("Expires:", "").trim();

            if (expiryText.equalsIgnoreCase("Not set")) {
                expiryText = "";
            }
        }

        final String expiryDate = expiryText;

        String[] quantityParts = quantityAndUnit.split("\\s+", 2);

        if (quantityParts.length < 2) {
            showMessage("Could not read the ingredient quantity and unit.");
            return;
        }

        int ingredientId;
        double quantity;

        try {
            ingredientId = Integer.parseInt(idText);
            quantity = Double.parseDouble(quantityParts[0]);
        } catch (NumberFormatException e) {
            showMessage("Could not read the ingredient details.");
            return;
        }

        String unit = quantityParts[1].trim();

        new AlertDialog.Builder(this)
                .setTitle(name)
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        android.content.Intent intent =
                                new android.content.Intent(
                                        PantryActivity.this,
                                        AddEditIngredientActivity.class);

                        intent.putExtra("ingredient_id", ingredientId);
                        intent.putExtra("ingredient_name", name);
                        intent.putExtra("ingredient_quantity",
                                String.valueOf(quantity));
                        intent.putExtra("ingredient_unit", unit);
                        intent.putExtra("ingredient_expiry", expiryDate);

                        startActivity(intent);
                    } else {
                        confirmDelete(ingredientId, name);
                    }
                })
                .show();
    }

    private void showEditDialog(
            int ingredientId,
            String currentName,
            double currentQuantity,
            String currentUnit,
            String currentExpiry) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText nameField = createField(
                "Ingredient name", currentName, InputType.TYPE_CLASS_TEXT);

        EditText quantityField = createField(
                "Quantity", String.valueOf(currentQuantity),
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);

        EditText unitField = createField(
                "Unit (e.g. g, ml, piece)", currentUnit, InputType.TYPE_CLASS_TEXT);

        EditText expiryField = createField(
                "Expiry date (YYYY-MM-DD, optional)",
                currentExpiry, InputType.TYPE_CLASS_TEXT);

        layout.addView(nameField);
        layout.addView(quantityField);
        layout.addView(unitField);
        layout.addView(expiryField);

        new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameField.getText().toString().trim();
                    String newQuantityText =
                            quantityField.getText().toString().trim();
                    String newUnit = unitField.getText().toString().trim();
                    String newExpiry = expiryField.getText().toString().trim();

                    if (newName.isEmpty()
                            || newQuantityText.isEmpty()
                            || newUnit.isEmpty()) {
                        showMessage("Name, quantity and unit are required.");
                        return;
                    }

                    double newQuantity;

                    try {
                        newQuantity = Double.parseDouble(newQuantityText);
                    } catch (NumberFormatException e) {
                        showMessage("Please enter a valid quantity.");
                        return;
                    }

                    if (!Double.isFinite(newQuantity) || newQuantity < 0) {
                        showMessage("Quantity must be a non-negative number.");
                        return;
                    }

                    if (!isValidExpiryDate(newExpiry)) {
                        showMessage(
                                "Enter a valid expiry date in YYYY-MM-DD format.");
                        return;
                    }

                    int updated = pantryDAO.updateIngredient(
                            ingredientId, newName, newQuantity,
                            newUnit, newExpiry);

                    if (updated > 0) {

                        showMessage("Ingredient updated successfully.");
                        loadIngredients();
                    } else {
                        showMessage("Could not update the ingredient.");
                    }
                })
                .show();
    }

    private void confirmDelete(int ingredientId, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + name + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int deleted = pantryDAO.deleteIngredient(ingredientId);

                    if (deleted > 0) {

                        showMessage("Ingredient deleted.");
                        loadIngredients();
                    } else {
                        showMessage("Could not delete the ingredient.");
                    }
                })
                .show();
    }

    private EditText createField(
            String hint, String value, int inputType) {

        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value);
        field.setInputType(inputType);
        return field;
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return true;
        }

        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        Date date = format.parse(expiryDate.trim(), position);

        return date != null
                && position.getIndex() == expiryDate.trim().length();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (pantryDAO != null) {
            pantryDAO.close();
        }
        super.onDestroy();
    }
}
