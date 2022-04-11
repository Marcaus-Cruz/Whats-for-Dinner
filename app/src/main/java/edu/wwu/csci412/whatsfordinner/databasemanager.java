package edu.wwu.csci412.whatsfordinner;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class databasemanager extends SQLiteOpenHelper {

    //Log
    private static final String LOG = "DatabaseHelper";

    //Database version
    private static final int ver = 1;

    //Database name
    private static final String db_name = "Application Tables";

    //Table Names
    private static final String Pantry_list = "PantryList";
    private static final String Shopping_list = "ShoppingList";
    private static final String Recipe_list = "RecipeList";

    //Column Names
    private static final String Pantry_ingredient_list = "Ingredient";
    private static final String Shopping_ingredient_list = "Ingredient";
    private static final String recipe_name = "Name";
    private static final String ingredient_name = "Ingredient";
    private static final String URL_link = "URLLink";

    //Table creation Statements
    //Pantry Table
    private static final String CREATE_PANTRY = "CREATE TABLE " + Pantry_list + "(" + Pantry_ingredient_list +" TEXT PRIMARY KEY" + ")";

    //Shopping List Table
    private static final String CREATE_SHOPPING_LIST = "CREATE TABLE " + Shopping_list + "(" + Shopping_ingredient_list +" TEXT PRIMARY KEY" + ")";

    //Recipe Table
    private static final String CREATE_Recipe = "CREATE TABLE " + Recipe_list + "(" +
                                                recipe_name +" TEXT PRIMARY KEY, " +
                                                ingredient_name + " TEXT, " +
                                                URL_link + " TEXT" + ")";

    public databasemanager(Context context){
        super(context, db_name, null, ver);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //Create tables
        db.execSQL(CREATE_PANTRY);
        db.execSQL(CREATE_SHOPPING_LIST);
        db.execSQL(CREATE_Recipe);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        //Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + Pantry_list);
        db.execSQL("DROP TABLE IF EXISTS " + Shopping_list);
        db.execSQL("DROP TABLE IF EXISTS " + Recipe_list);

        //create new tables
        onCreate(db);
    }

    //----------------Pantry List Table Methods----------------//

    //Add Ingredient
    public boolean addIngredient(Ingredient newIn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(Pantry_ingredient_list,newIn.getName());
        long id = db.insert(Pantry_list,null,value);
        if(id == -1){
            return false;
        }
        else{return true;}
    }

    //Add multiple Ingredients
    public boolean addIngredients(Pantry pantry){
        if(pantry.getSize() == 0){
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        ArrayList <Ingredient> list = pantry.getPantry();
        for (Ingredient in : list){
            value.put(Pantry_ingredient_list, in.getName());
            long id = db.insert(Pantry_list, null, value);
            if(id == -1){
                return false;
            }
        }
        return true;
    }
    public boolean addIngredients(ArrayList<Ingredient> ingredients) { //Overload to take an ArrayList instead
        if(ingredients.size() == 0) {
            return false;
        }
        for(Ingredient ingredient : ingredients) {
            this.addIngredient(ingredient);
        }
        return true;
    }

    //Get whole pantry
    public ArrayList<Ingredient> getAllPantry() {
        ArrayList<Ingredient> pantry = new ArrayList<Ingredient>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlSelectAll = "select * from " + Pantry_list;
        Cursor cursor = db.rawQuery(sqlSelectAll, null);
        while(cursor.moveToNext()) {
            Ingredient currentIngredient = new Ingredient(cursor.getString(0));
            pantry.add(currentIngredient);
        }
        db.close();
        return pantry;
    }

    //Check if Ingredient is in list
    public boolean checkIngredient(Ingredient targetIn){
        return false;
    }

    //Delete Ingredient
    public void deleteIngredient(Ingredient targetIn){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Pantry_list, Pantry_ingredient_list + " =?",
                   new String[] {targetIn.getName()});
    }

    //----------------Shopping List Table Methods----------------//

    //Add Ingredient
    public boolean addToShopping(Ingredient newIn){
        SQLiteDatabase db = this.getWritableDatabase();
        String SQLInsert = "insert into " + Shopping_list + " values ( '" + newIn.getName() +"' )";
        db.execSQL(SQLInsert);
        db.close();
        return true;
    }

    //Add multiple Ingredients
    public boolean addToShoppingMulti(ShoppingList list){
        if(list.getSize() == 0){
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        ArrayList <Ingredient> shopList = list.getList();
        for (Ingredient in : shopList){
            value.put(Shopping_ingredient_list, in.getName());
            long id = db.insert(Shopping_list, null, value);
            if(id == -1){
                return false;
            }
        }
        db.close();
        return true;
    }
    public boolean addToShoppingMulti(ArrayList<Ingredient> ingredients) {
        if(ingredients.size() > 0) {
            for(Ingredient ingredient : ingredients) {
                addToShopping(ingredient);
            }
            return true;
        }
        return false;
    }

    //Check if Ingredient is in list
    public boolean checkShopList(Ingredient targetIn){
        return false;
    }

    //Delete Ingredient
    public void deleteFromList(Ingredient targetIn){
        SQLiteDatabase db = this.getWritableDatabase();
        String SQLDelete = "delete from " + Shopping_list + " where " + Shopping_ingredient_list + " = '" + targetIn.getName() + "'";
        db.execSQL(SQLDelete);
        db.close();
    }

    //Get whole shopping list
    public ArrayList<Ingredient> getAllShopList() {
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlSelectAll = "select * from " + Shopping_list;
        Cursor cursor = db.rawQuery(sqlSelectAll, null);
        while(cursor.moveToNext()) {
            ingredients.add(new Ingredient(cursor.getString(0)));
        }
        db.close();
        return ingredients;
    }

    //----------------Recipe List Table Methods----------------//

    //Add Recipe
    public boolean addRecipe(Recipe newRec){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(recipe_name, newRec.getName());
        values.put(ingredient_name, newRec.toString());
        values.put(URL_link, newRec.getImageURL());

        long id = db.insert(Recipe_list, null,values);
        db.close();
        if(id == -1){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlSelectAll = "select * from " + Recipe_list;
        Cursor cursor = db.rawQuery(sqlSelectAll, null);
        while(cursor.moveToNext()) {
            Recipe currRecipe = new Recipe(cursor.getString(0));
            String[] ingredients = cursor.getString(1).split(". ");
            for (String ingredient : ingredients) {
                currRecipe.addIngredient(new Ingredient(ingredient));
            }
            currRecipe.setImageLinkURL(cursor.getString(2));
            recipes.add(currRecipe);
        }
        db.close();
        return recipes;
    }

    //Check if Recipe is in list
    public boolean checkRecipe(Recipe targetRec){
        return false;
    }

    //Delete Recipe
    public void deleteRecipe(Recipe targetRec){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Recipe_list, recipe_name + " =?", new String[] {targetRec.getName()});
        db.close();
    }
}
