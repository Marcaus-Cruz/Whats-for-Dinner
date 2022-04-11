package edu.wwu.csci412.whatsfordinner;

import android.net.Uri;

import java.util.ArrayList;

public class Recipe {
    /* Fields */
    private String name;
    private ArrayList<Ingredient> ingredients;

    //For potential future use cases
    /* Notes:
     *  - imageLinkURL and recipeLinkURL are for external database */
    private String imageLinkURL;
    private String recipeLinkURL;

    /* Constructors */
    /**Recipe(String name)
     * Constructor
     */
    public Recipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<Ingredient>();
    }
    /**Recipe(String name, Ingredient[] ingredients)
     * Constructor
     */
    public Recipe(String name, ArrayList<Ingredient> ingredients, String imageURL) {
        this.name = name;
        this.ingredients = ingredients;
        this.imageLinkURL = imageURL;
    }

    /* Accessors */
    public String getName() { return this.name; }
    public ArrayList<Ingredient> getIngredients() { return this.ingredients; }
    public String getImageURL() { return this.imageLinkURL; }
    public String getRecipeLinkURL() { return this.recipeLinkURL;}
    public boolean containsIngredient(Ingredient ingredient) {return this.ingredients.contains(ingredient); }


    /* Mutators */
    public void setName(String name) { this.name = name; }
    public void addIngredient(Ingredient item) {
        if (!this.ingredients.contains(item)) {
            this.ingredients.add(item);
        }
    }
    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }
    public void setRecipeLinkURL(String recipeLinkURL) {this.recipeLinkURL = recipeLinkURL; }
    public String toString() {
        if (ingredients.size() == 0)
            return "";

        StringBuilder result = new StringBuilder();
        for (Ingredient i : ingredients) {
            result.append(i.getName()).append(", ");
        }

        // trim the last comma and space off
        int len = result.toString().length();
        return result.toString().substring(0, len-2);
    }
    public void setImageLinkURL(String url){ this.imageLinkURL = url; }
}
