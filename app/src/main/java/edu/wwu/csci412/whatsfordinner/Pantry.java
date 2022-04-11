package edu.wwu.csci412.whatsfordinner;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Pantry {
    /* fields */
    private ArrayList<Ingredient> pantry;

    /* Constructors */
    /**Pantry()
     * Constructor
     */
    public Pantry() {
        this.pantry = new ArrayList<Ingredient>();
    }

    /* Accessors */
    public ArrayList<Ingredient> getPantry() { return this.pantry; }
    public int getSize() { return pantry.size(); }
    public boolean itemInPantry(Ingredient ingredient) {
        return this.pantry.contains(ingredient);
    }

    /* Mutators */
    public void addIngredient(Ingredient item) {
        if (!this.pantry.contains(item)) {
            this.pantry.add(item);
        }
    }
    public void removeIngredient(Ingredient ingredient) {
        this.pantry.remove(ingredient);
    }
    public void addIngredients(ArrayList<Ingredient> ingredients) {
        for(Ingredient ingredient : ingredients) {
            this.addIngredient(ingredient);
        }
    }

    @NotNull
    public String toString() {
        if (pantry.size() == 0)
            return "";

        StringBuilder result = new StringBuilder();
        for (Ingredient i : pantry) {
            result.append(i.getName()).append(", ");
        }

        // trim the last comma and space off
        int len = result.toString().length();
        return result.toString().substring(0, len-2);
    }

    public void clearPantry() {
        this.pantry = new ArrayList<Ingredient>();
    }
}
