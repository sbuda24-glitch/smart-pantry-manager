
package com.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class PantryDAO {

    private final PantryDatabaseHelper dbHelper;

    public PantryDAO(Context context) {
        dbHelper = new PantryDatabaseHelper(context);
    }

    // Add a new ingredient
    public long addIngredient(
            String name, double quantity, String unit, String expiryDate) {

        if (name == null || name.trim().isEmpty()
                || quantity < 0
                || unit == null || unit.trim().isEmpty()) {
            return -1;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PantryDatabaseHelper.COLUMN_NAME, name.trim());
        values.put(PantryDatabaseHelper.COLUMN_QUANTITY, quantity);
        values.put(PantryDatabaseHelper.COLUMN_UNIT, unit.trim());
        values.put(PantryDatabaseHelper.COLUMN_EXPIRY_DATE, expiryDate);

        return db.insert(
                PantryDatabaseHelper.TABLE_INGREDIENTS, null, values);
    }

    // Get all ingredients
    public ArrayList<String> getAllIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                null, null, null, null, null,
                PantryDatabaseHelper.COLUMN_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_UNIT));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COLUMN_EXPIRY_DATE));

                ingredients.add(
                        id + " | " + name + " | "
                                + quantity + " " + unit
                                + " | Expires: "
                                + (expiryDate == null
                                || expiryDate.trim().isEmpty()
                                ? "Not set" : expiryDate)
                );
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    // Update an ingredient
    public int updateIngredient(
            int id, String name, double quantity,
            String unit, String expiryDate) {

        if (id <= 0 || name == null || name.trim().isEmpty()
                || quantity < 0
                || unit == null || unit.trim().isEmpty()) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PantryDatabaseHelper.COLUMN_NAME, name.trim());
        values.put(PantryDatabaseHelper.COLUMN_QUANTITY, quantity);
        values.put(PantryDatabaseHelper.COLUMN_UNIT, unit.trim());
        values.put(PantryDatabaseHelper.COLUMN_EXPIRY_DATE, expiryDate);

        return db.update(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                values,
                PantryDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Delete an ingredient
    public int deleteIngredient(int id) {
        if (id <= 0) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                PantryDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public void close() {
        dbHelper.close();
    }
}
