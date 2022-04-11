package edu.wwu.csci412.whatsfordinner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.Recipe;
import edu.wwu.csci412.whatsfordinner.databasemanager;

public class HomeFragment extends Fragment {

    private databasemanager db;
    private HomeViewModel homeViewModel;
    private AutoCompleteTextView search;
    private LinearLayout hlo;
    private LinearLayout lo;
    private Button searchBut;
    private Button cameraBut;
    private TextView title;
    private GridLayout grid;
    private Button clearBut;


    //Todo: show last searched recipes and completion of them
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true); //get delete button
        search = root.findViewById(R.id.home_search_bar);
        hlo = root.findViewById(R.id.home_horiz_layout);
        lo = root.findViewById(R.id.home_main_layout);
        searchBut = root.findViewById(R.id.home_search_button);
        cameraBut = root.findViewById(R.id.cameraButton);
        title = root.findViewById(R.id.favorites_title);
        grid = root.findViewById(R.id.recipeGridHome);
        clearBut = root.findViewById(R.id.home_clear_but);

        db = new databasemanager(getActivity());

        updateView();

        return root;
    }


    /*** To display delete button on app bar ***/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_shop);
        menu.removeItem(R.id.action_add);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /*** Called when Add or Remove are selected, prompts add or remove activity ***/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.action_remove:
                removeFavorite();
                Toast.makeText(getContext(), "Home: remove selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void removeFavorite(){
        Intent removeIntent = new Intent(getContext(), RemoveFavorite.class);
        this.startActivity(removeIntent);
    }

    public void updateView(){
        //get all recipes
        ArrayList<Recipe> favRecipes = db.getAllRecipes();
        //search list
        ArrayList<String> searchFavNames = new ArrayList<String>();

        // clear grid layout, set column count to 2
        grid.removeAllViews();
        grid.setColumnCount(2);

        for (Recipe recipe : favRecipes) {
            // Recipe description
            TextView desc = new TextView(getActivity());
            desc.setGravity(Gravity.CENTER);
            StringBuilder ing = new StringBuilder();
            String prefix = "";
            for (Ingredient ingredient : recipe.getIngredients()) {
                ing.append(prefix);
                ing.append(ingredient.getName());
                prefix = ", ";
            }
            desc.setText(Html.fromHtml("<b>"+recipe.getName() + "</b><br> ingredients: "+ing.toString()));

            // Recipe image
            ImageView photo = new ImageView(getActivity());
            if (recipe.getImageURL().isEmpty()) {
                photo.setImageResource(R.drawable.temp_logo);
            }
            else {
                Picasso.get().load(recipe.getImageURL()).into(photo);
            }

            grid.addView(desc, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
            grid.addView(photo, 500, 400);

            //get list of recipes user can search for
            searchFavNames.add(recipe.getName());
        }

        //Set adapter for search edit text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchFavNames);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search.getText().toString().isEmpty())
                    updateSearchView(favRecipes);
            }
        });
        lo.removeView(clearBut);
    }

    public void updateSearchView(ArrayList<Recipe> favRecipes){
        String searchString = search.getText().toString();
        search.setText("");

        lo.removeView(clearBut);

        //search list
        ArrayList<String> searchFavNames = new ArrayList<String>();

        // clear grid layout, set column count to 2
        grid.removeAllViews();
        grid.setColumnCount(2);

        for(Recipe recipe : favRecipes){
            if(recipe.getName().contains(searchString)){
            TextView desc = new TextView(getActivity());
            desc.setGravity(Gravity.CENTER);
            StringBuilder ing = new StringBuilder();
            for (Ingredient ingredient : recipe.getIngredients()) {
                ing.append(ingredient.getName());
            }
            desc.setText(Html.fromHtml("<b>"+recipe.getName() + "</b><br> ingredients: "+ing.toString()));

            // Recipe image
            ImageView photo = new ImageView(getActivity());
            if (recipe.getImageURL().isEmpty()) {
                photo.setImageResource(R.drawable.temp_logo);
            }
            else {
                Picasso.get().load(recipe.getImageURL()).into(photo);
            }

            grid.addView(desc, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
            grid.addView(photo, 500, 400);

            searchFavNames.add(recipe.getName());
            }
        }
        lo.addView(clearBut);
        clearBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
            }
        });
        //Set adapter for search edit text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchFavNames);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search.getText().toString().isEmpty())
                    updateSearchView(favRecipes);
            }
        });

    }

    /*** Updates view when returning from delete activity ***/
    @Override
    public void onResume(){
        updateView();
        super.onResume();
    }
}