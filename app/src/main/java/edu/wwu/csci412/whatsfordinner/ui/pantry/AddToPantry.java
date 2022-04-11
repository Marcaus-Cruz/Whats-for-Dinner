package edu.wwu.csci412.whatsfordinner.ui.pantry;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.Recipe;
import edu.wwu.csci412.whatsfordinner.RecipeSearch;
import edu.wwu.csci412.whatsfordinner.databasemanager;
import edu.wwu.csci412.whatsfordinner.ui.recipes.RecipesFragment;

public class AddToPantry extends AppCompatActivity {

    public static String str;
    private EditText newIng;
    private Button confirm;
    private Button addBut;
    private databasemanager db;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_pantry);
        db = new databasemanager(this);
        newIng = findViewById(R.id.name_of_ingredient);
        addBut = findViewById(R.id.add_ingredient_but);
        confirm = findViewById(R.id.add_pantry_done_but);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //When done button is clicked
                switch(v.getId()){
                    case R.id.add_pantry_done_but:
                        done();
                        break;
                }
            }
        });
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //When add button is clicked
                switch(v.getId()){
                    case R.id.add_ingredient_but:
                        addIngredient();
                        break;
                }
            }
        });
    }

    /*** Adds ingredient to pantry ***/
    public void addIngredient(){
        //convert edit text to string and add ingredient to pantry
        str = newIng.getText().toString();

        //if user didn't enter anything, do not add ingredient
        if(str.trim().isEmpty()){
            this.finish();
            return;
        }
        Ingredient ingredient = new Ingredient(str);
        PantryFragment.pantry.addIngredient(ingredient);

        //Call to DB manager and add ingredient here
        db.addIngredient(ingredient);

        Toast.makeText(this, str + " was added to your pantry.", Toast.LENGTH_SHORT).show();
        newIng.setText("");
    }

    /*** Called when Users are finished adding to their pantry ***/
    public void done(){
        this.finish();
    }

}
