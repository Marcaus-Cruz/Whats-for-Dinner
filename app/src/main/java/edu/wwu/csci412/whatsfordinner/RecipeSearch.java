package edu.wwu.csci412.whatsfordinner;

import android.util.Log;

import java.util.ArrayList;

import org.json.*;

public class RecipeSearch {
    /* fields */
    private static final String RECIPE_API_HEAD = "http://www.recipepuppy.com/api";
    //private boolean searchBasedOnPantry = true;
    private ArrayList<Recipe> recipes;
    private Pantry pantry;

    public APICallback callback;

    /* Constructors */
    /**RecipeSearch(Pantry)
     * Constructor
     * Takes in a pantry Object as an argument
     */
    public RecipeSearch(Pantry pantry) {
        this.recipes = new ArrayList<Recipe>();
        this.pantry = pantry;
    }

    /**RecipeSearch()
     * Constructor
     */
    public RecipeSearch() {
        this.recipes = new ArrayList<Recipe>();
    }

    /* Accessors */
    public ArrayList<Recipe> getRecipes() { return this.recipes; }

    /* Mutators */
    public void setPantry(Pantry pantry) { this.pantry = pantry; }

    /* Searches */
    public void searchNoKeyWord() {
        /* Create API Query */
        ArrayList<Ingredient> ingredients = this.pantry.getPantry();
        StringBuilder apiIngredientQuery = new StringBuilder("?i=");
        for(int i = 0; i < ingredients.size(); i++) {
            apiIngredientQuery.append(ingredients.get(i).getName().toLowerCase());
            if (i<ingredients.size()-1)
                apiIngredientQuery.append(",");
        }
        String apiQuery = RECIPE_API_HEAD + apiIngredientQuery;

        /* perform the API call */
        new APICall(apiQuery).getString(response -> {
            try {
                populateRecipes(new JSONObject(response));
                // after doing the HTTP GET, call the RecipeSearch callback
                if (callback != null)
                    callback.call("");
            } catch (JSONException e) {
                Log.e("RecipeSearch", "JSON Exception");
            }
        });
    }

    public void searchKeyWord(String keyWord) {
        /* Create API Query */
        ArrayList<Ingredient> ingredients = this.pantry.getPantry();
        StringBuilder apiIngredientQuery = new StringBuilder("?i=");
        for(int i = 0; i < ingredients.size(); i++) {
            apiIngredientQuery.append(ingredients.get(i).getName().toLowerCase());
            apiIngredientQuery.append(",");
        }
        String apiKeyWordQuery = "&q=" + "\"" + keyWord + "\"";
        String apiQuery = RECIPE_API_HEAD + apiIngredientQuery + apiKeyWordQuery;

        /* perform the API call */
        new APICall(apiQuery).getString(response -> {
            try {
                populateRecipes(new JSONObject(response));
                // after doing the HTTP GET, call the RecipeSearch callback
                if (callback != null)
                    callback.call("");
            } catch (JSONException e) {
                Log.e("RecipeSearch", "JSON Exception");
            }
        });
    }

    private void populateRecipes(JSONObject jsonObject) throws JSONException {
        if (jsonObject.length() == 0) {
            Log.w("RecipeSearch", "populate recipes: no recipes loaded");
            return;
        }

        JSONArray jsonRecipeArray = jsonObject.getJSONArray("results");
        Log.d("RecipeSearch", jsonRecipeArray.length() +" recipes");
        for(int i = 0; i < jsonRecipeArray.length(); i++) {
            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
            String ingredientsString = jsonRecipeArray.getJSONObject(i).getString("ingredients");
            String[] ingredientStringArray = ingredientsString.split("\\s,");
            for (String s : ingredientStringArray) {
                ingredients.add(new Ingredient(s.toLowerCase()));
            }
            String recipeName = jsonRecipeArray.getJSONObject(i).getString("title").replaceAll("(\\r|\\n|\\t)", "");
            String imageURL = jsonRecipeArray.getJSONObject(i).getString("thumbnail");
            String recipeURL = jsonRecipeArray.getJSONObject(i).getString("href");
            Recipe r = new Recipe(recipeName, ingredients, imageURL);
            r.setRecipeLinkURL(recipeURL);
            this.recipes.add(r);
        }
    }
}
