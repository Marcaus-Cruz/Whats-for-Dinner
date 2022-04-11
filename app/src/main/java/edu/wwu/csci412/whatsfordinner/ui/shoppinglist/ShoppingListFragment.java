package edu.wwu.csci412.whatsfordinner.ui.shoppinglist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.MapsActivity;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.ShoppingList;
import edu.wwu.csci412.whatsfordinner.databasemanager;
import edu.wwu.csci412.whatsfordinner.ui.pantry.PantryFragment;
import edu.wwu.csci412.whatsfordinner.ui.pantry.RemoveFromPantry;

public class ShoppingListFragment extends Fragment {

    private ShoppingListViewModel shoppingListViewModel;
    private LinearLayout lo;
    private LinearLayout hlo;
    private LinearLayout buthlo;
    private AutoCompleteTextView search;
    private TextView title;
    private Button delete;
    private Button moveBut;
    private Button searchBut;
    public static ShoppingList shopList = new ShoppingList();
    private String str;
    private databasemanager db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shoppingListViewModel =
                new ViewModelProvider(this).get(edu.wwu.csci412.whatsfordinner.ui.shoppinglist.ShoppingListViewModel.class);
        View root = inflater.inflate(R.layout.shoppinglist, container, false);
        setHasOptionsMenu(true);

        lo = root.findViewById(R.id.shop_list_layout);
        search = root.findViewById(R.id.shop_list_search);
        title = root.findViewById(R.id.shop_list_title);
        delete = root.findViewById(R.id.shop_list_remove);
        hlo = root.findViewById(R.id.shop_list_horizontal);
        buthlo = root.findViewById(R.id.shop_list_remove_but_hlo);
        searchBut = root.findViewById(R.id.shop_list_search_but);
        moveBut = root.findViewById(R.id.shop_list_to_pantry);
        
        db = new databasemanager(getActivity());
        shopList.clearList();
        shopList.addItems(db.getAllShopList());

        updateView();

        return root;
    }

    /*** To display add button at top right corner of screen ***/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_remove);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /*** Acts as a listener for the toolbar, namely the add button ***/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.action_add:
                addToShoppingList();
                Toast.makeText(getContext(), "Shopping List: add selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_shop:
                openMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*** Called upon hitting the add button to bring up add activity ***/
    public void addToShoppingList(){
        Intent addIntent = new Intent(getContext(), ShoppingListAdd.class);
        this.startActivity(addIntent);
    }

    /*** Dynamically updates shopping list with check boxes that the user can use to delete at any time ***/
    public void updateView(){
        //Setup layout parameters for each text view placed in the layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        params.bottomMargin = 15;
        params.leftMargin = 15;
        params.topMargin = 15;

        lo.removeAllViews();
        //Add the title and search bar
        lo.addView(title);
        lo.addView(hlo);

        //Get a shallow copy of the shopping list to make removing with indexing a lot easier
        ArrayList<Ingredient> list = new ArrayList<Ingredient>(shopList.getList());
        ArrayList<String> items = new ArrayList<String>();

        //Create check box views for each ingredient in the shopping list and add them to layout
        for(int i = 0; i < list.size(); i++) {
            str = list.get(i).getName();
            items.add(str);
            CheckBox check = new CheckBox(getContext());
            check.setText(str);
            check.setTextSize(20);
            check.setLayoutParams(params);
            lo.addView(check);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchView(list);
            }
        });

        //Add delete button and move to pantry button
        lo.addView(buthlo);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Upon user checking their items and pressing delete
                switch(v.getId()){
                    case R.id.shop_list_remove:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < list.size()+2; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                shopList.removeItem(list.get(x));
                                //remove from database here
                                db.deleteFromList(list.get(x));
                            }
                            x++;
                        }
                        Toast.makeText(getContext(),  "Items being removed...", Toast.LENGTH_SHORT).show();
                        //Show updated shopping list
                        updateView();
                        break;
                }
            }
        });
        moveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.shop_list_to_pantry:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < list.size()+2; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                shopList.removeItem(list.get(x));
                                //move ingredient to pantry
                                //remove from database here
                                db.addIngredient(list.get(x));
                                db.deleteFromList(list.get(x));
                            }
                            x++;
                        }
                        Toast.makeText(getContext(),  "Items being transferred...", Toast.LENGTH_SHORT).show();
                        //Show updated shopping list
                        updateView();
                        break;
                }
            }
        });
    }

    public void updateSearchView(ArrayList<Ingredient> list){
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

        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getName().contains(str)){
                //add checkboxes
                CheckBox check = new CheckBox(getContext());
                check.setText(list.get(i).getName());
                check.setTextSize(20);
                check.setLayoutParams(params);
                lo.addView(check);
                //get search results
                searchResults.add(list.get(i));
                searchResultStrings.add(list.get(i).getName());
            }
        }
        //add delete button
        lo.addView(buthlo);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Upon user checking their items and pressing delete
                switch(v.getId()){
                    case R.id.shop_list_remove:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < 2 + searchResults.size() ; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                shopList.removeItem(searchResults.get(x));
                                //remove from database here
                                db.deleteFromList(list.get(x));
                            }
                            x++;
                        }
                        Toast.makeText(getContext(),  "Items being deleted...", Toast.LENGTH_SHORT).show();
                        updateView();
                        break;
                }
            }
        });
        moveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.shop_list_to_pantry:
                        //remove items that were checked
                        int x = 0;
                        for(int i = 2; i < list.size()+2; i++){
                            if(((CheckBox) lo.getChildAt(i)).isChecked()){
                                shopList.removeItem(list.get(x));
                                //move ingredient to pantry
                                //remove from database here
                                db.addIngredient(list.get(x));
                                db.deleteFromList(list.get(x));
                            }
                            x++;
                        }
                        Toast.makeText(getContext(),  "Items being transferred...", Toast.LENGTH_SHORT).show();
                        //Show updated shopping list
                        updateView();
                        break;
                }
            }
        });

        //Set adapter for updated search edit text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchResultStrings);
        search.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchView(searchResults);
            }
        });
    }

    /*** Updates view when returning from add activity ***/
    @Override
    public void onResume(){
        updateView();
        super.onResume();
    }

    public void openMap() {
        startActivity(new Intent(getContext(), MapsActivity.class));
    }

}
