
package com.smartpantrymanager;

public class RecipeIngredient {

    private int id;
    private int recipeId;
    private String ingredientName;
    private double requiredQuantity;
    private String requiredUnit;

    public RecipeIngredient(
            int id,
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String requiredUnit) {

        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.requiredUnit = requiredUnit;
    }

    public int getId() {
        return id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getRequiredUnit() {
        return requiredUnit;
    }
}
