
package com.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RecipeDAO {

    private final PantryDatabaseHelper dbHelper;

    public RecipeDAO(Context context) {
        dbHelper = new PantryDatabaseHelper(context);
    }

    // Save a recipe
    public long addRecipe(String name, String steps) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PantryDatabaseHelper.COLUMN_RECIPE_NAME, name);
        values.put(PantryDatabaseHelper.COLUMN_STEPS, steps);

        return db.insert(
                PantryDatabaseHelper.TABLE_RECIPES,
                null,
                values
        );
    }

    // Get all recipes
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                PantryDatabaseHelper.COLUMN_RECIPE_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_RECIPE_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_RECIPE_NAME
                        )
                );

                String steps = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_STEPS
                        )
                );

                recipes.add(new Recipe(id, name, steps));
            }
        } finally {
            cursor.close();
        }

        return recipes;
    }

    // Close the database helper when finished
    public void close() {
        dbHelper.close();
    }
}
