
package com.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RecipeIngredientDAO {

    private final PantryDatabaseHelper dbHelper;

    public RecipeIngredientDAO(Context context) {
        dbHelper = new PantryDatabaseHelper(context);
    }

    // Save a required ingredient for a recipe
    public long addRecipeIngredient(
            int recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PantryDatabaseHelper.COLUMN_RECIPE_ID, recipeId);
        values.put(
                PantryDatabaseHelper.COLUMN_INGREDIENT_NAME,
                ingredientName
        );
        values.put(
                PantryDatabaseHelper.COLUMN_REQUIRED_QUANTITY,
                quantity
        );
        values.put(
                PantryDatabaseHelper.COLUMN_REQUIRED_UNIT,
                unit
        );

        return db.insert(
                PantryDatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }

    // Get the required ingredients for one recipe
    public ArrayList<RecipeIngredient> getIngredientsForRecipe(
            int recipeId) {

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                PantryDatabaseHelper.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                PantryDatabaseHelper.COLUMN_INGREDIENT_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper
                                        .COLUMN_RECIPE_INGREDIENT_ID
                        )
                );

                int savedRecipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_RECIPE_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_INGREDIENT_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_REQUIRED_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_REQUIRED_UNIT
                        )
                );

                ingredients.add(
                        new RecipeIngredient(
                                id,
                                savedRecipeId,
                                name,
                                quantity,
                                unit
                        )
                );
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    public void close() {
        dbHelper.close();
    }
}
