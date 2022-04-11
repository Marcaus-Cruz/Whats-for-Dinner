package edu.wwu.csci412.whatsfordinner.ui.pantry;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import javax.security.auth.callback.CallbackHandler;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.Pantry;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.Recipe;
import edu.wwu.csci412.whatsfordinner.RecipeSearch;
import edu.wwu.csci412.whatsfordinner.databasemanager;
import edu.wwu.csci412.whatsfordinner.ui.recipes.RecipesFragment;

public class RemoveFromPantry extends AppCompatActivity {

    private LinearLayout lo;
    private LinearLayout hlo;
    private AutoCompleteTextView search;
    private TextView title;
    private Button delete;
    private Button searchBut;
    private String str;
    private databasemanager db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_from_pantry);

        title = findViewById(R.id.remove_pantry_title);
        lo = findViewById(R.id.remove_layout);
        hlo = findViewById(R.id.remove_pantry_search_layout);
        search = findViewById(R.id.remove_pantry_search);
        delete = findViewById(R.id.remove_pantry_but);
        searchBut = findViewById(R.id.pantry_remove_search_but);
        db = new databasemanager(this);

        updateView();
    }

    public void updateView(){
        //Setup layout parameters for each text view placed in the layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        params.bottomMargin = 15;
        params.leftMargin = 15;
        params.topMargin = 15;

        //remove all view from linear layout
        lo.removeAllViews();

        //Add the title and search bar
        lo.addView(title);
        lo.addView(hlo);

        //Get a shallow copy of the pantry to make removing with indexing a lot easier
        ArrayList<Ingredient> list = new ArrayList<Ingredient>(PantryFragment.pantry.getPantry());
        ArrayList<String> ingredients = new ArrayList<String>();

        //Create check box views for each ingredient in the pantry and add them to layout
        for(int i = 0; i < list.size(); i++) {
            str = list.get(i).getName();
            ingredients.add(str);
            CheckBox check = new CheckBox(this);
            check.setText(str);
            check.setTextSize(20);
            check.setLayoutParams(params);
            lo.addView(check);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredients);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchView(list);
            }
        });

        //Add delete button at end
        delete.setBackgroundColor(Color.parseColor("#FFFF0000"));
        lo.addView(delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Upon user checking their items and pressing delete
                switch(v.getId()){
                    case R.id.remove_pantry_but:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < list.size()+2; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                PantryFragment.pantry.removeIngredient(list.get(x));
                                //remove from database here
                                db.deleteIngredient(list.get(x));
                            }
                            x++;
                        }
                        Toast.makeText(RemoveFromPantry.this,  "Items being deleted...", Toast.LENGTH_SHORT).show();
                        RemoveFromPantry.this.finish();
                        break;
                }
            }
        });
    }

    public void updateSearchView(ArrayList<Ingredient> pantry){
        str = search.getText().toString();
        search.setText("");

        //Setup layout parameters for each text view placed in the layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        params.bottomMargin = 15;
        params.leftMargin = 15;
        params.topMargin = 15;

        //remove views, add title and search layout
        lo.removeAllViews();
        lo.addView(title);
        lo.addView(hlo);

        //new Arraylist to make removing easier
        ArrayList<Ingredient> searchResults = new ArrayList<Ingredient>();
        //String Arraylist for search completion
        ArrayList<String> searchResultStrings = new ArrayList<String>();

        for(int i = 0; i < pantry.size(); i++){
            if(pantry.get(i).getName().contains(str)){
                //add checkboxes
                CheckBox check = new CheckBox(this);
                check.setText(pantry.get(i).getName());
                check.setTextSize(20);
                check.setLayoutParams(params);
                lo.addView(check);
                //get search results
                searchResults.add(pantry.get(i));
                searchResultStrings.add(pantry.get(i).getName());
            }
        }
        //add delete button
        lo.addView(delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Upon user checking their items and pressing delete
                switch(v.getId()){
                    case R.id.remove_pantry_but:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < 2 + searchResults.size() ; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                PantryFragment.pantry.removeIngredient(searchResults.get(x));
                                //remove from database here
                            }
                            x++;
                        }
                        Toast.makeText(RemoveFromPantry.this,  "Items being deleted...", Toast.LENGTH_SHORT).show();
                        RemoveFromPantry.this.finish();
                        break;
                }
            }
        });

        //Set adapter for updated search edit text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchResultStrings);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchView(searchResults);
            }
        });

    }

}
