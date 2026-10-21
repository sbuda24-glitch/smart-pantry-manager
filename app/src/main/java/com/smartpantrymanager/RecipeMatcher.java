
package com.smartpantrymanager;

import java.util.ArrayList;
import java.util.Locale;

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

        for (Recipe recipe : allRecipes) {

            ArrayList<RecipeIngredient> requiredIngredients =
                    ingredientDAO.getIngredientsForRecipe(recipe.getId());

            boolean canMakeRecipe = !requiredIngredients.isEmpty();

            for (RecipeIngredient required : requiredIngredients) {

                boolean found = false;

                // Check whether the pantry contains enough of this ingredient.
                for (String pantryItem : pantryDAO.getAllIngredients()) {

                    String[] parts = pantryItem.split("\\|");

                    if (parts.length < 3) {
                        continue;
                    }

                    String pantryName = parts[1].trim();
                    String quantityAndUnit = parts[2].trim();

                    String[] quantityParts = quantityAndUnit.split("\\s+");

                    if (quantityParts.length < 2) {
                        continue;
                    }

                    double pantryQuantity;

                    try {
                        pantryQuantity = Double.parseDouble(quantityParts[0]);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    // FIX: Normalise the pantry unit too.
                    String pantryUnit = normaliseUnit(quantityParts[1]);

                    if (normaliseName(pantryName).equals(
                            normaliseName(required.getIngredientName()))
                            && pantryUnit.equals(
                            normaliseUnit(required.getRequiredUnit()))
                            && pantryQuantity >= required.getRequiredQuantity()) {

                        found = true;
                        break;
                    }
                }

                if (!found) {
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
