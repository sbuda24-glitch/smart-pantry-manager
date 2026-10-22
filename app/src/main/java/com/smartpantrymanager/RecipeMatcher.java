
package com.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private final PantryDAO pantryDAO;
    private final RecipeDAO recipeDAO;
    private final RecipeIngredientDAO ingredientDAO;

    public RecipeMatcher(android.content.Context context) {
        pantryDAO = new PantryDAO(context);
        recipeDAO = new RecipeDAO(context);
        ingredientDAO = new RecipeIngredientDAO(context);
    }

    public ArrayList<Recipe> getMatchingRecipes() {

        ArrayList<Recipe> matchingRecipes = new ArrayList<>();
        ArrayList<Recipe> allRecipes = recipeDAO.getAllRecipes();

        // Load pantry ingredients once.
        ArrayList<String> pantryIngredients = pantryDAO.getAllIngredients();

        // Combine quantities for duplicate ingredients with matching units.
        Map<String, Double> availableQuantities = new HashMap<>();

        for (String pantryItem : pantryIngredients) {

            String[] parts = pantryItem.split("\\|");

            if (parts.length < 3) {
                continue;
            }

            String pantryName = normaliseName(parts[1].trim());
            String quantityAndUnit = parts[2].trim();
            String[] quantityParts = quantityAndUnit.split("\\s+");

            if (quantityParts.length < 2) {
                continue;
            }

            try {
                double quantity = Double.parseDouble(quantityParts[0]);
                String unit = normaliseUnit(quantityParts[1]);

                if (quantity < 0 || !Double.isFinite(quantity)) {
                    continue;
                }

                String key = pantryName + "|" + unit;

                availableQuantities.put(
                        key,
                        availableQuantities.getOrDefault(key, 0.0) + quantity
                );

            } catch (NumberFormatException e) {
                // Skip pantry entries with invalid quantities.
            }
        }

        // Check every recipe against the combined pantry quantities.
        for (Recipe recipe : allRecipes) {

            ArrayList<RecipeIngredient> requiredIngredients =
                    ingredientDAO.getIngredientsForRecipe(recipe.getId());

            boolean canMakeRecipe = !requiredIngredients.isEmpty();

            for (RecipeIngredient required : requiredIngredients) {

                String ingredientName =
                        normaliseName(required.getIngredientName());

                String unit = normaliseUnit(required.getRequiredUnit());

                String key = ingredientName + "|" + unit;

                double available =
                        availableQuantities.getOrDefault(key, 0.0);

                if (available < required.getRequiredQuantity()) {
                    canMakeRecipe = false;
                    break;
                }
            }

            if (canMakeRecipe) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    private String normaliseName(String name) {

        String cleaned = name.toLowerCase(Locale.ROOT).trim();

        if (cleaned.endsWith("ies") && cleaned.length() > 3) {
            return cleaned.substring(0, cleaned.length() - 3) + "y";
        }

        if (cleaned.endsWith("s")
                && !cleaned.endsWith("ss")
                && cleaned.length() > 1) {
            return cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    private String normaliseUnit(String unit) {

        String cleaned = unit.toLowerCase(Locale.ROOT).trim();

        switch (cleaned) {
            case "pieces":
            case "pcs":
                return "piece";

            case "grams":
                return "g";

            case "millilitres":
            case "milliliters":
                return "ml";

            default:
                return cleaned;
        }
    }

    public void close() {
        pantryDAO.close();
        recipeDAO.close();
        ingredientDAO.close();
    }
}
