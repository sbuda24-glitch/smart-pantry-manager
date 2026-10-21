
package com.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Color;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends Activity {

    private RecipeMatcher recipeMatcher;
    private ListView recipeList;
    private TextView messageText;
    private ArrayList<Recipe> matchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.rgb(245, 247, 239));

        TextView heading = new TextView(this);
        heading.setText("Suggested Recipes");
        heading.setTextSize(26);
        heading.setTextColor(Color.rgb(40, 89, 67));
        heading.setPadding(0, 0, 0, 20);
        layout.addView(heading);

        messageText = new TextView(this);
        messageText.setTextSize(16);
        messageText.setTextColor(Color.DKGRAY);
        messageText.setPadding(0, 0, 0, 16);
        layout.addView(messageText);

        recipeList = new ListView(this);
        layout.addView(recipeList, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        Button backButton = new Button(this);
        backButton.setText("Back to Pantry");
        layout.addView(backButton);

        setContentView(layout);

        backButton.setOnClickListener(v -> finish());

        recipeList.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = matchingRecipes.get(position);

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );

            intent.putExtra("recipe_id", selectedRecipe.getId());
            startActivity(intent);
        });

        loadMatchingRecipes();
    }

    private void loadMatchingRecipes() {
        recipeMatcher = new RecipeMatcher(this);
        matchingRecipes = recipeMatcher.getMatchingRecipes();

        ArrayList<String> recipeNames = new ArrayList<>();

        for (Recipe recipe : matchingRecipes) {
            recipeNames.add(recipe.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );

        recipeList.setAdapter(adapter);

        if (matchingRecipes.isEmpty()) {
            messageText.setText(
                    "No recipes match your pantry yet. Add more ingredients "
                            + "or check your quantities."
            );
        } else {
            messageText.setText(
                    matchingRecipes.size()
                            + " recipe(s) you can make with your ingredients."
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recipeMatcher != null) {
            recipeMatcher.close();
        }
    }
}
