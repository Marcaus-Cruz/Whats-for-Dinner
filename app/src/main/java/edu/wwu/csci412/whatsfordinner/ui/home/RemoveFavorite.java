package edu.wwu.csci412.whatsfordinner.ui.home;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.security.auth.callback.CallbackHandler;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.Pantry;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.Recipe;
import edu.wwu.csci412.whatsfordinner.RecipeSearch;
import edu.wwu.csci412.whatsfordinner.databasemanager;
import edu.wwu.csci412.whatsfordinner.ui.recipes.RecipesFragment;

public class RemoveFavorite extends AppCompatActivity{

    private LinearLayout lo;
    private TextView title;
    private Button backBut;
    private databasemanager db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_favorite);

        title = findViewById(R.id.remove_fav_title);
        lo = findViewById(R.id.remove_fav_lo);
        backBut = findViewById(R.id.remove_fav_back_but);

        db = new databasemanager(this);
        ArrayList<Recipe> favs = db.getAllRecipes();

        updateView(favs);
    }

    public void updateView(ArrayList<Recipe> recipes) {
        if (recipes.size() > 0) {
            GridLayout grid = new GridLayout(this);

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
                description[i] = new TextView(this);
                description[i].setGravity(Gravity.CENTER);

                StringBuilder ing = new StringBuilder();
                String prefix = "";
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ing.append(prefix);
                    ing.append(ingredient.getName());
                    prefix = ", ";
                }
                description[i].setText(Html.fromHtml("<b>"+recipe.getName() + "</b><br> ingredients: "+ing.toString()));

                photos[i] = new ImageView(this);
                if (recipe.getImageURL().isEmpty()) {
                    photos[i].setImageResource(R.drawable.temp_logo); // No image is given with the recipe, default to logo
                }
                else {
                    Picasso.get().load(recipe.getImageURL()).into(photos[i]);
                }

                // TODO: base sizes on screen width
                grid.addView(description[i], 450, ViewGroup.LayoutParams.WRAP_CONTENT);
                grid.addView(photos[i], 450, 400);
                Button removeButton = new Button(this);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    private final Recipe thisRecipe = recipe;

                    @Override
                    public void onClick(View v) {
                        db.deleteRecipe(thisRecipe);

                        Toast.makeText(RemoveFavorite.this, "Removed " + thisRecipe.getName() + " from favorites", Toast.LENGTH_SHORT).show();
                        RemoveFavorite.this.finish();
                    }
                });
                removeButton.setText("Remove");
                grid.addView(removeButton, 100, ViewGroup.LayoutParams.WRAP_CONTENT);
                i++;
            }

            // ignore warning, findViewById will not return null in a Fragment context
            ScrollView sv = findViewById(R.id.remove_fav_sv);
            lo.removeAllViews();
            lo.addView(title);
            lo.addView(grid);
            lo.addView(backBut);
            backBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RemoveFavorite.this.finish();
                }
            });
        }
    }
}
