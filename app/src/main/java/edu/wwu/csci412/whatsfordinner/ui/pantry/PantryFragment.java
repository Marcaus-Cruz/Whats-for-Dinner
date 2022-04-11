package edu.wwu.csci412.whatsfordinner.ui.pantry;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;

import edu.wwu.csci412.whatsfordinner.Ingredient;
import edu.wwu.csci412.whatsfordinner.Pantry;
import edu.wwu.csci412.whatsfordinner.R;
import edu.wwu.csci412.whatsfordinner.databasemanager;

public class PantryFragment extends Fragment {
    private databasemanager db;
    private PantryViewModel pantryViewModel;
    public static Pantry pantry = new Pantry(); //Used this pantry for testing
    static ScrollView scroll; //scroll view for this fragment
    static AutoCompleteTextView searchBar; //search bar for this fragment
    static LinearLayout lo; //layout within scroll view
    String str;
    Button clearBut;
    Button searchBut;
    LinearLayout hlo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pantryViewModel =
                new ViewModelProvider(this).get(PantryViewModel.class);
        View root = inflater.inflate(R.layout.pantry, container, false);

        setHasOptionsMenu(true); //get add/delete buttons
        scroll = root.findViewById(R.id.pantry_scroll);
        searchBar = root.findViewById(R.id.pantry_search);
        lo = root.findViewById(R.id.scroll_layout);
        hlo = root.findViewById(R.id.pantry_horizontal_lo);
        searchBut = root.findViewById(R.id.pantry_search_button);
        clearBut = root.findViewById(R.id.pantry_clear_but);

        //for testing//
        /*pantry.addIngredient(new Ingredient("Chicken"));
        pantry.addIngredient(new Ingredient("Rice"));
        pantry.addIngredient(new Ingredient("potatoes"));
        pantry.addIngredient(new Ingredient("steak"));*/

        db = new databasemanager(getActivity());
        pantry.clearPantry();
        pantry.addIngredients(db.getAllPantry());

        updateView();
        return root;
    }
    /*** To display add/delete buttons on app bar ***/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.removeItem(R.id.action_shop);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /*** Called when Add or Remove are selected, prompts add or remove activity ***/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.action_add:
                Toast.makeText(getContext(), "Pantry: add selected", Toast.LENGTH_SHORT).show();
                addToPantry();
                return true;
            case R.id.action_remove:
                removeFromPantry();
                Toast.makeText(getContext(), "Pantry: remove selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*** Called to start add activity ***/
    public void addToPantry(){
        Intent addIntent = new Intent(getContext(), AddToPantry.class);
        this.startActivity(addIntent);
    }
    /*** Called to start remove activity ***/
    public void removeFromPantry(){
        Intent removeIntent = new Intent(getContext(),RemoveFromPantry.class);
        this.startActivity(removeIntent);
    }

    /*** Updates view with current pantry ***/
    public void updateView(){
        String str;
        //Clear everything in linear layout (inside scroll view)
        lo.removeAllViews();

        //Re-add search bar
        lo.addView(hlo);

        //Add ingredients from pantry to view
        ArrayList<String> ingredients = new ArrayList<>();
        ArrayList<Ingredient> list = pantry.getPantry();
        for(int i = 0; i < list.size(); i++) {
            TextView tv = new TextView(getContext());
            str = list.get(i).getName();
            tv.setText(str);
            tv.setTextSize((float) 50);
            tv.setPadding(20,5,5,5);
            lo.addView(tv);
            ingredients.add(str);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ingredients);
        searchBar.setAdapter(adapter);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSearchView(list);
            }
        });
    }

    /*** Updates view to items that have search text in them ***/
    public void updateSearchView(ArrayList<Ingredient> pantry){
        str = searchBar.getText().toString();
        searchBar.setText("");

        lo.removeAllViews();
        lo.addView(hlo);

        for(int i = 0; i < pantry.size(); i++){
            if(pantry.get(i).getName().contains(str)){
                //add text views
                TextView tv = new TextView(getContext());
                tv.setText(pantry.get(i).getName());
                tv.setTextSize((float) 50);
                tv.setPadding(20,5,5,5);
                lo.addView(tv);
            }
        }
        lo.addView(clearBut);
        //shows full pantry again
        clearBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
            }
        });
    }

    /*** Updates view when returning from add or delete activities ***/
    @Override
    public void onResume(){
        updateView();
        super.onResume();
    }

}
