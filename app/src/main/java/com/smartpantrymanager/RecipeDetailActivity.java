
package com.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeDetailActivity extends Activity {

    private RecipeDAO recipeDAO;
    private RecipeIngredientDAO ingredientDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.rgb(245, 247, 239));

        TextView title = new TextView(this);
        title.setTextSize(26);
        title.setTextColor(Color.rgb(40, 89, 67));
        layout.addView(title);

        TextView ingredientsTitle = new TextView(this);
        ingredientsTitle.setText("\nRequired Ingredients");
        ingredientsTitle.setTextSize(20);
        ingredientsTitle.setTextColor(Color.rgb(40, 89, 67));
        layout.addView(ingredientsTitle);

        TextView ingredientsText = new TextView(this);
        ingredientsText.setTextSize(16);
        layout.addView(ingredientsText);

        TextView stepsTitle = new TextView(this);
        stepsTitle.setText("\nPreparation Steps");
        stepsTitle.setTextSize(20);
        stepsTitle.setTextColor(Color.rgb(40, 89, 67));
        layout.addView(stepsTitle);

        TextView stepsText = new TextView(this);
        stepsText.setTextSize(16);
        layout.addView(stepsText);

        Button backButton = new Button(this);
        backButton.setText("Back to Recipes");
        layout.addView(backButton);

        setContentView(layout);

        recipeDAO = new RecipeDAO(this);
        ingredientDAO = new RecipeIngredientDAO(this);

        Recipe selectedRecipe = null;

        for (Recipe recipe : recipeDAO.getAllRecipes()) {
            if (recipe.getId() == recipeId) {
                selectedRecipe = recipe;
                break;
            }
        }

        if (selectedRecipe == null) {
            title.setText("Recipe not found");
            ingredientsText.setText("");
            stepsText.setText("");
        } else {
            title.setText(selectedRecipe.getName());
            stepsText.setText(selectedRecipe.getSteps());

            ArrayList<RecipeIngredient> ingredients =
                    ingredientDAO.getIngredientsForRecipe(recipeId);

            StringBuilder details = new StringBuilder();

            for (RecipeIngredient ingredient : ingredients) {
                details.append("• ")
                        .append(ingredient.getIngredientName())
                        .append(" - ")
                        .append(ingredient.getRequiredQuantity())
                        .append(" ")
                        .append(ingredient.getRequiredUnit())
                        .append("\n");
            }

            ingredientsText.setText(details.toString());
        }

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recipeDAO != null) {
            recipeDAO.close();
        }

        if (ingredientDAO != null) {
            ingredientDAO.close();
        }
    }
}
