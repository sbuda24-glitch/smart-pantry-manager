
package com.smartpantrymanager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class IngredientAdapter extends ArrayAdapter<String> {

    public IngredientAdapter(Context context, List<String> ingredients) {
        super(context, android.R.layout.simple_list_item_1, ingredients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView ingredientView;

        if (convertView instanceof TextView) {
            ingredientView = (TextView) convertView;
        } else {
            ingredientView = new TextView(getContext());
        }

        String item = getItem(position);

        if (item == null) {
            item = "";
        }

        String[] parts = item.split("\\s*\\|\\s*", 4);

        if (parts.length >= 4) {
            String name = parts[1];
            String quantity = parts[2];
            String expiry = parts[3];

            ingredientView.setText(name + "\n" + quantity + "  •  " + expiry);
        } else {
            ingredientView.setText(item);
        }

        ingredientView.setTextSize(16);
        ingredientView.setTextColor(0xFF285943);
        ingredientView.setTypeface(null, Typeface.NORMAL);
        ingredientView.setPadding(24, 20, 24, 20);
        ingredientView.setMinHeight(80);

        return ingredientView;
    }
}
