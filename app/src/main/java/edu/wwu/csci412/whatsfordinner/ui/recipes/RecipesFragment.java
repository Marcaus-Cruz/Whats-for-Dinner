package edu.wwu.csci412.whatsfordinner.ui.recipes;

import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.APICallback;
import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.Pantry;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.Recipe;
import edu.wwu.csci412.whatsfordinner.RecipeSearch;
import edu.wwu.csci412.whatsfordinner.databasemanager;
import edu.wwu.csci412.whatsfordinner.ui.pantry.PantryFragment;

public class RecipesFragment extends Fragment {

    private databasemanager db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recipes, container, false);

        if (PantryFragment.pantry.getSize() > 0) {
            RecipeSearch rs = new RecipeSearch(PantryFragment.pantry);

            rs.callback = response -> {
                // recipe search is completed and recipes are loaded
                ArrayList<Recipe> recipes = rs.getRecipes();
                showRecipes(PantryFragment.pantry, recipes);
            };

            rs.searchNoKeyWord();
        }

        this.db = new databasemanager(getActivity());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (PantryFragment.pantry.getSize() == 0) {
            showErrorMessage("Add some items to your pantry to get started!");
        }
    }

    private void showErrorMessage(String msg) {
        TextView message = new TextView(getActivity());
        message.setGravity(Gravity.CENTER);
        message.setText(Html.fromHtml("<h1>"+msg+"</h1>"));

        // ignore warning, findViewById will not return null in a Fragment context
        ScrollView sv = getView().findViewById(R.id.recipe_scroll);
        sv.removeAllViews();
        sv.addView(message);
    }

    public void showRecipes(Pantry params, ArrayList<Recipe> recipes) {
        if (recipes.size() > 0) {
            GridLayout grid = new GridLayout(getActivity());

            // title row and one row for each recipe
            grid.setRowCount(recipes.size()+1);

            // name/ingredients and photo
            grid.setColumnCount(3);

            TextView[] description = new TextView[recipes.size()];
            ImageView[] photos = new ImageView[recipes.size()];

            Point size = new Point();
            // getActivity().getWindowManager().getDefaultDisplay().getSize(size); // doesn't work
            int width = size.x;

            int i=0;
            for (Recipe recipe : recipes) {
                description[i] = new TextView(getActivity());
                description[i].setGravity(Gravity.CENTER);

                StringBuilder ing = new StringBuilder();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ing.append(ingredient.getName());
                }
                description[i].setText(Html.fromHtml("<b>"+recipe.getName() + "</b><br> ingredients: "+ing.toString()));

                photos[i] = new ImageView(getActivity());
                if (recipe.getImageURL().isEmpty()) {
                    photos[i].setImageResource(R.drawable.temp_logo); // No image is given with the recipe, default to logo
                }
                else {
                    Picasso.get().load(recipe.getImageURL()).into(photos[i]);
                }

                // TODO: base sizes on screen width
                grid.addView(description[i], 450, ViewGroup.LayoutParams.WRAP_CONTENT);
                grid.addView(photos[i], 450, 400);
                Button favButton = new Button(getActivity());
                favButton.setOnClickListener(new View.OnClickListener() {
                    private final Recipe thisRecipe = recipe;

                    @Override
                    public void onClick(View v) {
                        db.addRecipe(thisRecipe);

                        Toast.makeText(getActivity(), "Added " + thisRecipe.getName() + " to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
                favButton.setText("Favorite");
                grid.addView(favButton, 100, ViewGroup.LayoutParams.WRAP_CONTENT);
                i++;
            }

            // ignore warning, findViewById will not return null in a Fragment context
            ScrollView sv = getView().findViewById(R.id.recipe_scroll);
            sv.removeAllViews();
            sv.addView(grid);
        } else {
            // set results row
            showErrorMessage("No recipes found with your ingredients :(");
        }
    }

}