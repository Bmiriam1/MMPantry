package com.mmpantry.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles all local persistence for MM Pantry using SQLite.
 *
 * Three tables are managed here:
 *  - pantry_items: the ingredients the user currently has at home
 *  - recipes: the fixed catalogue of recipes the app knows about (seeded on first run)
 *  - recipe_ingredients: the ingredients each recipe requires, linked to recipes by recipe_id
 *
 * The most important piece of logic in this class is getMatchingRecipes(), which implements
 * the assignment's "strict-matching rule": a recipe is only suggested if the user's pantry
 * already contains every single ingredient it needs, in at least the required quantity.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

 private static final String DB_NAME = "mmpantry.db";
 private static final int DB_VERSION = 1;

 public DatabaseHelper(Context context) {
  super(context, DB_NAME, null, DB_VERSION);
 }

 /**
  * Called once, the first time the database is created on a device.
  * Sets up all three tables and seeds the recipe catalogue.
  */
 @Override
 public void onCreate(SQLiteDatabase db) {
  db.execSQL("CREATE TABLE pantry_items(" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "name TEXT," +
          "normalized TEXT," +
          "quantity REAL," +
          "unit TEXT," +
          "expiry TEXT)");

  db.execSQL("CREATE TABLE recipes(" +
          "id INTEGER PRIMARY KEY," +
          "name TEXT," +
          "description TEXT," +
          "prep INTEGER," +
          "method TEXT)");

  db.execSQL("CREATE TABLE recipe_ingredients(" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
          "recipe_id INTEGER," +
          "name TEXT," +
          "normalized TEXT," +
          "quantity REAL," +
          "unit TEXT)");

  seed(db);
 }

 @Override
 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  // No schema migrations needed yet — only one version exists so far.
 }

 /**
  * Normalizes an ingredient name so that simple variations (plural/singular, casing,
  * whitespace) don't break matching. This is a lightweight heuristic, not full NLP:
  * it lowercases, trims, and strips common plural endings ("tomatoes" -> "tomato").
  */
 public static String norm(String s) {
  s = s.toLowerCase(Locale.US).trim();
  if (s.endsWith("ies")) {
   s = s.substring(0, s.length() - 3) + "y";
  } else if (s.endsWith("es")) {
   s = s.substring(0, s.length() - 2);
  } else if (s.endsWith("s")) {
   s = s.substring(0, s.length() - 1);
  }
  return s;
 }

 // ---------- Pantry CRUD ----------

 /** Inserts a new pantry ingredient and returns its new row id. */
 public long insertIngredient(Ingredient x) {
  ContentValues v = new ContentValues();
  v.put("name", x.name);
  v.put("normalized", norm(x.name));
  v.put("quantity", x.quantity);
  v.put("unit", x.unit);
  v.put("expiry", x.expiry);
  return getWritableDatabase().insert("pantry_items", null, v);
 }

 /** Returns every ingredient currently in the pantry, sorted alphabetically. */
 public List<Ingredient> getIngredients() {
  List<Ingredient> result = new ArrayList<>();
  Cursor c = getReadableDatabase().query(
          "pantry_items", null, null, null, null, null, "name COLLATE NOCASE");

  while (c.moveToNext()) {
   result.add(new Ingredient(
           c.getInt(c.getColumnIndexOrThrow("id")),
           c.getString(c.getColumnIndexOrThrow("name")),
           c.getDouble(c.getColumnIndexOrThrow("quantity")),
           c.getString(c.getColumnIndexOrThrow("unit")),
           c.getString(c.getColumnIndexOrThrow("expiry"))));
  }
  c.close();
  return result;
 }

 /** Looks up a single pantry ingredient by id, or null if it doesn't exist. */
 public Ingredient getIngredient(int id) {
  for (Ingredient x : getIngredients()) {
   if (x.id == id) return x;
  }
  return null;
 }

 /** Updates an existing pantry ingredient's details. */
 public void updateIngredient(Ingredient x) {
  ContentValues v = new ContentValues();
  v.put("name", x.name);
  v.put("normalized", norm(x.name));
  v.put("quantity", x.quantity);
  v.put("unit", x.unit);
  v.put("expiry", x.expiry);
  getWritableDatabase().update("pantry_items", v, "id=?", new String[]{"" + x.id});
 }

 /** Removes a pantry ingredient permanently. */
 public void deleteIngredient(int id) {
  getWritableDatabase().delete("pantry_items", "id=?", new String[]{"" + id});
 }

 // ---------- Recipe matching ----------

 /**
  * Core business logic: returns only the recipes the user can make RIGHT NOW,
  * using strictly what's already in their pantry.
  *
  * The rule (per the assignment brief) is strict: a recipe qualifies only if EVERY
  * ingredient it needs is present in the pantry in at least the required quantity.
  * A recipe missing even one ingredient, or short on quantity for one ingredient,
  * must NOT appear in the results — no partial matches are allowed.
  */
 public List<Recipe> getMatchingRecipes() {
  List<Recipe> matches = new ArrayList<>();

  // Build a lookup of what the user currently has, keyed by normalized ingredient name,
  // so "tomato" and "tomatoes" resolve to the same pantry entry.
  Map<String, Ingredient> pantry = new HashMap<>();
  for (Ingredient x : getIngredients()) {
   pantry.put(norm(x.name), x);
  }

  Cursor recipeCursor = getReadableDatabase().query(
          "recipes", null, null, null, null, null, "name");

  while (recipeCursor.moveToNext()) {
   Recipe recipe = new Recipe(
           recipeCursor.getInt(0),
           recipeCursor.getString(1),
           recipeCursor.getString(2),
           recipeCursor.getInt(3),
           recipeCursor.getString(4));

   Cursor ingredientCursor = getReadableDatabase().query(
           "recipe_ingredients", null, "recipe_id=?",
           new String[]{"" + recipe.id}, null, null, null);

   boolean canMake = true;

   while (ingredientCursor.moveToNext()) {
    String neededNormalized = ingredientCursor.getString(
            ingredientCursor.getColumnIndexOrThrow("normalized"));
    double neededQuantity = ingredientCursor.getDouble(
            ingredientCursor.getColumnIndexOrThrow("quantity"));

    Ingredient have = pantry.get(neededNormalized);

    // Strict-matching rule: if the ingredient is missing entirely, OR the pantry
    // doesn't have enough of it, this recipe is disqualified immediately.
    if (have == null || have.quantity < neededQuantity) {
     canMake = false;
     break;
    }

    recipe.ingredients.add(new Recipe.Req(
            ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("name")),
            neededQuantity,
            ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("unit"))));
   }
   ingredientCursor.close();

   if (canMake) {
    matches.add(recipe);
   }
  }
  recipeCursor.close();
  return matches;
 }

 /**
  * Looks up a single recipe by id, for the recipe detail screen.
  * First checks the currently-matching recipes (so ingredient requirement details are
  * populated); falls back to a direct lookup if the recipe no longer matches the pantry
  * (e.g. the user removed an ingredient after tapping into the recipe).
  */
 public Recipe getRecipe(int id) {
  for (Recipe x : getMatchingRecipes()) {
   if (x.id == id) return x;
  }

  Cursor c = getReadableDatabase().query(
          "recipes", null, "id=?", new String[]{"" + id}, null, null, null);

  if (!c.moveToFirst()) {
   c.close();
   return null;
  }

  Recipe recipe = new Recipe(
          c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getString(4));
  c.close();

  Cursor ingredientCursor = getReadableDatabase().query(
          "recipe_ingredients", null, "recipe_id=?",
          new String[]{"" + id}, null, null, null);

  while (ingredientCursor.moveToNext()) {
   recipe.ingredients.add(new Recipe.Req(
           ingredientCursor.getString(2),
           ingredientCursor.getDouble(4),
           ingredientCursor.getString(5)));
  }
  ingredientCursor.close();
  return recipe;
 }

 // ---------- Seed data ----------

 /**
  * Populates the recipe catalogue on first run. Each row is:
  * { name, description, prep time (minutes), method, ingredient list }
  * where the ingredient list is semicolon-separated entries of "name|quantity|unit".
  */
 private void seed(SQLiteDatabase db) {
  String[][] data = {
          {"Caprese Salad", "Tomato, mozzarella and basil in a sunlit classic.", "10",
                  "Slice and layer the tomato and mozzarella. Add basil, olive oil and seasoning.",
                  "tomato|2|pieces;mozzarella|1|piece;basil|10|leaves;olive oil|1|tablespoon"},
          {"Pasta Aglio e Olio", "Simple pasta with garlic, olive oil and chilli.", "20",
                  "Cook pasta. Warm garlic and chilli in olive oil, then toss with pasta.",
                  "pasta|200|grams;garlic|2|cloves;olive oil|2|tablespoons;chilli|1|piece"},
          {"Gazpacho", "A chilled Spanish tomato soup for warm afternoons.", "25",
                  "Blend vegetables with olive oil and vinegar. Chill before serving.",
                  "tomato|4|pieces;cucumber|1|piece;pepper|1|piece;olive oil|2|tablespoons"},
          {"Pan con Tomate", "Toasted bread rubbed with ripe tomato and garlic.", "10",
                  "Toast bread, rub with garlic, add crushed tomato and olive oil.",
                  "bread|2|slices;tomato|2|pieces;garlic|1|clove;olive oil|1|tablespoon"},
          {"Tortilla Espanola", "A Spanish potato and egg omelette.", "35",
                  "Cook potato and onion gently. Combine with eggs and cook until set.",
                  "potato|3|pieces;egg|4|pieces;onion|1|piece;olive oil|2|tablespoons"},
          {"Panzanella", "Tuscan bread salad with tomato and basil.", "15",
                  "Combine toasted bread, tomato, cucumber and basil. Dress and rest.",
                  "bread|2|slices;tomato|2|pieces;cucumber|1|piece;basil|8|leaves"},
          {"Bruschetta", "Crisp bread with tomato, basil and olive oil.", "15",
                  "Toast bread and top with chopped tomato, basil and olive oil.",
                  "bread|2|slices;tomato|2|pieces;basil|6|leaves;olive oil|1|tablespoon"},
          {"Greek Salad", "Fresh cucumber, tomato, olive and feta salad.", "15",
                  "Chop ingredients and toss gently with olive oil.",
                  "tomato|2|pieces;cucumber|1|piece;feta|100|grams;olive|10|pieces"},
          {"Hummus Plate", "Creamy chickpea hummus with lemon and garlic.", "15",
                  "Blend chickpeas, lemon, garlic and olive oil until smooth.",
                  "chickpea|1|can;lemon|1|piece;garlic|1|clove;olive oil|1|tablespoon"},
          {"Ratatouille", "A colourful Provençal vegetable stew.", "45",
                  "Dice vegetables and simmer slowly with olive oil and herbs.",
                  "tomato|2|pieces;eggplant|1|piece;zucchini|1|piece;onion|1|piece"},
          {"Lemon Couscous", "Bright couscous with lemon and herbs.", "15",
                  "Pour boiling water over couscous, rest, then fold through lemon and herbs.",
                  "couscous|150|grams;lemon|1|piece;parsley|10|grams;olive oil|1|tablespoon"},
          {"Mediterranean Rice Bowl", "Rice with roasted vegetables and herbs.", "35",
                  "Cook rice and serve with roasted vegetables and olive oil.",
                  "rice|200|grams;pepper|1|piece;zucchini|1|piece;olive oil|1|tablespoon"},
          {"Spanish Chickpea Stew", "Comforting chickpeas with tomato and paprika.", "40",
                  "Simmer chickpeas, tomato, onion and paprika until rich.",
                  "chickpea|1|can;tomato|2|pieces;onion|1|piece;paprika|1|teaspoon"},
          {"Garlic Butter Pasta", "Silky pasta with garlic and butter.", "20",
                  "Cook pasta, melt butter with garlic, and toss together.",
                  "pasta|200|grams;garlic|2|cloves;butter|2|tablespoons"},
          {"Tomato Basil Pasta", "A quick Italian pasta with fresh tomato.", "25",
                  "Cook pasta and fold through tomato, basil and olive oil.",
                  "pasta|200|grams;tomato|3|pieces;basil|10|leaves;olive oil|1|tablespoon"}
  };

  for (int k = 0; k < data.length; k++) {
   int recipeId = k + 1;

   ContentValues recipeValues = new ContentValues();
   recipeValues.put("id", recipeId);
   recipeValues.put("name", data[k][0]);
   recipeValues.put("description", data[k][1]);
   recipeValues.put("prep", Integer.parseInt(data[k][2]));
   recipeValues.put("method", data[k][3]);
   db.insert("recipes", null, recipeValues);

   // Each recipe's ingredient list is semicolon-separated "name|quantity|unit" entries.
   for (String part : data[k][4].split(";")) {
    String[] fields = part.split("\\|");

    ContentValues ingredientValues = new ContentValues();
    ingredientValues.put("recipe_id", recipeId);
    ingredientValues.put("name", fields[0]);
    ingredientValues.put("normalized", norm(fields[0]));
    ingredientValues.put("quantity", Double.parseDouble(fields[1]));
    ingredientValues.put("unit", fields[2]);
    db.insert("recipe_ingredients", null, ingredientValues);
   }
  }
 }
}