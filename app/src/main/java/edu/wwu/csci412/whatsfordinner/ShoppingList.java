package edu.wwu.csci412.whatsfordinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShoppingList {
    /* Fields */
    private ArrayList<Ingredient> shoppingList;

    /* Constructors */
    public ShoppingList() {
        this.shoppingList = new ArrayList<Ingredient>();
    }

    /* Accessors */
    public ArrayList<Ingredient> getList() { return this.shoppingList; }
    public int getSize() { return shoppingList.size(); }
    public boolean itemOnList(Ingredient ingredient) { return this.shoppingList.contains(ingredient); }

    /* Mutators */
    public void addItem(Ingredient ingredient) {
        if (!this.shoppingList.contains(ingredient)) {
            this.shoppingList.add(ingredient);
        }
    }
    public void removeItem(Ingredient ingredient) {
        this.shoppingList.remove(ingredient);
    }
    public void addItems(ArrayList<Ingredient> ingredients) {
        for(Ingredient ingredient: ingredients) {
            this.shoppingList.add(ingredient);
        }
    }
    public void clearList() {
        this.shoppingList = new ArrayList<Ingredient>();
    }


}
