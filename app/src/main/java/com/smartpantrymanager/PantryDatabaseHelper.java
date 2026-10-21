
package com.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    // Existing pantry table
    public static final String TABLE_INGREDIENTS = "ingredients";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    // Recipe table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_STEPS = "steps";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String COLUMN_RECIPE_INGREDIENT_ID =
            "recipe_ingredient_id";
    public static final String COLUMN_INGREDIENT_NAME =
            "ingredient_name";
    public static final String COLUMN_REQUIRED_QUANTITY =
            "required_quantity";
    public static final String COLUMN_REQUIRED_UNIT =
            "required_unit";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPantryTable(db);
        createRecipeTables(db);
        seedRecipes(db);
        seedRecipeIngredients(db);
    }

    private void createPantryTable(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL CHECK ("
                + COLUMN_QUANTITY + " >= 0), "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_EXPIRY_DATE + " TEXT"
                + ")";

        db.execSQL(createTable);
    }

    private void createRecipeTables(SQLiteDatabase db) {
        String createRecipes = "CREATE TABLE IF NOT EXISTS "
                + TABLE_RECIPES + " ("
                + COLUMN_RECIPE_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                + COLUMN_STEPS + " TEXT NOT NULL"
                + ")";

        db.execSQL(createRecipes);

        String createRecipeIngredients =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_RECIPE_INGREDIENTS + " ("
                        + COLUMN_RECIPE_INGREDIENT_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_RECIPE_ID + " INTEGER NOT NULL, "
                        + COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, "
                        + COLUMN_REQUIRED_QUANTITY
                        + " REAL NOT NULL CHECK ("
                        + COLUMN_REQUIRED_QUANTITY + " > 0), "
                        + COLUMN_REQUIRED_UNIT + " TEXT NOT NULL, "
                        + "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES "
                        + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID
                        + ") ON DELETE CASCADE"
                        + ")";

        db.execSQL(createRecipeIngredients);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            createRecipeTables(db);
        }

        if (oldVersion < 3) {
            seedRecipes(db);
            seedRecipeIngredients(db);
        }
    }
    private void seedRecipes(SQLiteDatabase db) {

        android.content.ContentValues values =
                new android.content.ContentValues();

        String[][] recipes = {
                {"Scrambled Eggs", "Beat eggs, cook gently in a pan, and stir until set."},
                {"Tomato Omelette", "Beat eggs, add chopped tomatoes, and cook in a pan."},
                {"Garlic Tomato Pasta", "Cook pasta. Fry garlic and tomatoes, then mix together."},
                {"Pancakes", "Mix flour, milk, egg, and a little sugar. Cook portions in a pan."},
                {"Banana Oat Pancakes", "Mash banana, mix with oats and egg, then cook in a pan."},
                {"Vegetable Fried Rice", "Cook rice, fry mixed vegetables, then stir in the rice."},
                {"Tomato Soup", "Cook tomatoes with onion and water, then blend until smooth."},
                {"Potato Hash", "Dice potatoes and onion, then fry until golden and tender."},
                {"Cheese Toast", "Place cheese on bread and toast until melted."},
                {"Egg Sandwich", "Boil eggs, mash them, and serve between slices of bread."},
                {"Garlic Butter Potatoes", "Boil potatoes, then toss with butter and garlic."},
                {"Simple Salad", "Chop lettuce, tomato, and cucumber, then combine."},
                {"Banana Smoothie", "Blend banana with milk until smooth."},
                {"Oatmeal", "Cook oats with milk or water until soft."},
                {"French Toast", "Dip bread in beaten egg and milk, then fry until golden."},
                {"Chickpea Curry", "Cook chickpeas with onion, tomato, and curry powder."},
                {"Lentil Soup", "Simmer lentils with onion, carrots, and water until tender."},
                {"Tomato Rice", "Cook rice with tomatoes, onion, and seasoning."},
                {"Baked Eggs", "Place eggs and chopped tomatoes in an oven-safe dish and bake until set."},
                {"Potato Omelette", "Cook sliced potatoes, add beaten eggs, and cook until set."}
        };

        for (String[] recipe : recipes) {

            android.database.Cursor cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{COLUMN_RECIPE_ID},
                    COLUMN_RECIPE_NAME + " = ?",
                    new String[]{recipe[0]},
                    null,
                    null,
                    null
            );

            boolean exists = cursor.moveToFirst();
            cursor.close();

            if (!exists) {
                values.clear();
                values.put(COLUMN_RECIPE_NAME, recipe[0]);
                values.put(COLUMN_STEPS, recipe[1]);

                db.insert(TABLE_RECIPES, null, values);
            }
        }
    }

    private void seedRecipeIngredients(SQLiteDatabase db) {

        String[][] data = {
                {"Scrambled Eggs", "egg", "2", "piece"},
                {"Scrambled Eggs", "milk", "30", "ml"},
                {"Scrambled Eggs", "butter", "10", "g"},

                {"Tomato Omelette", "egg", "2", "piece"},
                {"Tomato Omelette", "tomato", "1", "piece"},
                {"Tomato Omelette", "onion", "0.5", "piece"},

                {"Garlic Tomato Pasta", "pasta", "100", "g"},
                {"Garlic Tomato Pasta", "tomato", "2", "piece"},
                {"Garlic Tomato Pasta", "garlic", "1", "piece"},

                {"Pancakes", "flour", "100", "g"},
                {"Pancakes", "milk", "120", "ml"},
                {"Pancakes", "egg", "1", "piece"},

                {"Banana Oat Pancakes", "banana", "1", "piece"},
                {"Banana Oat Pancakes", "oats", "50", "g"},
                {"Banana Oat Pancakes", "egg", "1", "piece"},

                {"Vegetable Fried Rice", "rice", "100", "g"},
                {"Vegetable Fried Rice", "carrot", "1", "piece"},
                {"Vegetable Fried Rice", "onion", "1", "piece"},

                {"Tomato Soup", "tomato", "3", "piece"},
                {"Tomato Soup", "onion", "1", "piece"},
                {"Tomato Soup", "water", "250", "ml"},

                {"Potato Hash", "potato", "2", "piece"},
                {"Potato Hash", "onion", "1", "piece"},
                {"Potato Hash", "oil", "15", "ml"},

                {"Cheese Toast", "bread", "2", "slice"},
                {"Cheese Toast", "cheese", "40", "g"},

                {"Egg Sandwich", "egg", "2", "piece"},
                {"Egg Sandwich", "bread", "2", "slice"},
                {"Egg Sandwich", "mayonnaise", "15", "g"},

                {"Garlic Butter Potatoes", "potato", "2", "piece"},
                {"Garlic Butter Potatoes", "butter", "15", "g"},
                {"Garlic Butter Potatoes", "garlic", "1", "piece"},

                {"Simple Salad", "lettuce", "50", "g"},
                {"Simple Salad", "tomato", "1", "piece"},
                {"Simple Salad", "cucumber", "0.5", "piece"},

                {"Banana Smoothie", "banana", "1", "piece"},
                {"Banana Smoothie", "milk", "250", "ml"},

                {"Oatmeal", "oats", "50", "g"},
                {"Oatmeal", "milk", "200", "ml"},

                {"French Toast", "bread", "2", "slice"},
                {"French Toast", "egg", "1", "piece"},
                {"French Toast", "milk", "60", "ml"},

                {"Chickpea Curry", "chickpeas", "150", "g"},
                {"Chickpea Curry", "tomato", "2", "piece"},
                {"Chickpea Curry", "onion", "1", "piece"},

                {"Lentil Soup", "lentils", "100", "g"},
                {"Lentil Soup", "carrot", "1", "piece"},
                {"Lentil Soup", "onion", "1", "piece"},

                {"Tomato Rice", "rice", "100", "g"},
                {"Tomato Rice", "tomato", "2", "piece"},
                {"Tomato Rice", "onion", "1", "piece"},

                {"Baked Eggs", "egg", "2", "piece"},
                {"Baked Eggs", "tomato", "1", "piece"},

                {"Potato Omelette", "potato", "1", "piece"},
                {"Potato Omelette", "egg", "2", "piece"}
        };

        for (String[] item : data) {

            android.database.Cursor recipeCursor = db.query(
                    TABLE_RECIPES,
                    new String[]{COLUMN_RECIPE_ID},
                    COLUMN_RECIPE_NAME + " = ?",
                    new String[]{item[0]},
                    null, null, null
            );

            if (recipeCursor.moveToFirst()) {

                int recipeId = recipeCursor.getInt(0);
                recipeCursor.close();

                android.database.Cursor existingCursor = db.query(
                        TABLE_RECIPE_INGREDIENTS,
                        new String[]{COLUMN_RECIPE_INGREDIENT_ID},
                        COLUMN_RECIPE_ID + " = ? AND "
                                + COLUMN_INGREDIENT_NAME + " = ?",
                        new String[]{
                                String.valueOf(recipeId),
                                item[1]
                        },
                        null, null, null
                );

                boolean exists = existingCursor.moveToFirst();
                existingCursor.close();

                if (!exists) {
                    android.content.ContentValues values =
                            new android.content.ContentValues();

                    values.put(COLUMN_RECIPE_ID, recipeId);
                    values.put(COLUMN_INGREDIENT_NAME, item[1]);
                    values.put(
                            COLUMN_REQUIRED_QUANTITY,
                            Double.parseDouble(item[2])
                    );
                    values.put(COLUMN_REQUIRED_UNIT, item[3]);

                    db.insert(
                            TABLE_RECIPE_INGREDIENTS,
                            null,
                            values
                    );
                }

            } else {
                recipeCursor.close();
            }
        }
    }

}
