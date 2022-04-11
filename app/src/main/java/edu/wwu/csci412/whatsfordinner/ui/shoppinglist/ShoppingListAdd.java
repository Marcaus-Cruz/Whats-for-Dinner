package edu.wwu.csci412.whatsfordinner.ui.shoppinglist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.ShoppingList;
import edu.wwu.csci412.whatsfordinner.databasemanager;

import static edu.wwu.csci412.whatsfordinner.ui.shoppinglist.ShoppingListFragment.shopList;

public class ShoppingListAdd extends AppCompatActivity {

    private LinearLayout lo;
    private LinearLayout hlo;
    private TextView title;
    private EditText et;
    private Button addBut;
    private Button doneBut;
    private String str;
    private databasemanager db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_shopping_list);

        lo = findViewById(R.id.shop_list_add_layout);
        hlo = findViewById(R.id.horizonatal_layout_shop_list);
        title = findViewById(R.id.shop_list_add_title);
        et = findViewById(R.id.shop_list_add_item);
        addBut = findViewById(R.id.shop_list_add_but);
        doneBut = findViewById(R.id.shop_list_add_done);
        db = new databasemanager(this);

        updateView();

    }

    public void updateView(){

        //Set params for text views to be added
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        params.bottomMargin = 15;
        params.leftMargin = 15;
        params.topMargin = 15;

        //remove views from layout to update
        lo.removeAllViews();
        lo.addView(title);
        //Don't need to clear the horizontal layout because it always only has the editText and button
        lo.addView(hlo);

        //Get shallow copy of list to use size() method lol too lazy to add to ShoppingList class
        ArrayList<Ingredient> list = new ArrayList<Ingredient>(shopList.getList());
        //Add all items from shopping list to view
        for(int i = 0; i < list.size(); i++) {
            TextView txt = new TextView(this);
            txt.setText(list.get(i).getName());
            txt.setTextSize(20);
            txt.setLayoutParams(params);
            lo.addView(txt);
        }
        //add "done adding" button to bottom
        lo.addView(doneBut);

        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //str = entered item
                str = et.getText().toString();

                //if user didn't enter anything, do not add item
                if(str.trim().isEmpty()){
                    return;
                } else {
                    //add ingredient to shopping list
                    Ingredient ingredient = new Ingredient(str);
                    ShoppingListFragment.shopList.addItem(ingredient);
                    //Call to DB manager and add ingredient here
                    db.addToShopping(ingredient);
                    Toast.makeText(ShoppingListAdd.this, str + " was added to your list.", Toast.LENGTH_SHORT).show();
                    //clear edit text for next item to add
                    et.setText("");
                    //dynamically update as user adds
                    updateView();
                }
            }
        });
        //When user is done adding
        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListAdd.this.finish();
            }
        });


    }

}
