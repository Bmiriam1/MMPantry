# MM Pantry

MM Pantry is an Android app I built for my Mobile App Development assignment. The idea is simple: you tell it what's in your pantry, and it tells you what you can actually cook with it — nothing more, nothing less. If a recipe needs 5 ingredients and you only have 4, it won't show up. That's the whole point of the app, really.

It's built in Java using Android Studio, and everything runs locally on the device with SQLite — no internet connection needed, no login, nothing to set up.

## What it does

You can add ingredients to your pantry (name, quantity, unit, and an optional expiry date if you want to track that), edit them, or delete them once they're used up. There's a Suggested Recipes screen that checks your pantry against a list of 15 recipes and only shows you the ones you can make right now. Tap into any recipe to see the full ingredient list and method. There's also a basic settings screen with a dark mode toggle, and a bottom nav bar to move between the three main screens.

## Why SQLite

I went with SQLite over Firebase or PostgreSQL mainly because this app doesn't need to talk to a server for anything — it's just you and your own pantry, so there's no real reason to add a network dependency. SQLiteOpenHelper is also what we covered in the persistent data section of the module, and it meant I could keep everything self-contained and not have to worry about setting up and paying for hosting somewhere.

## Getting it running

Clone the repo:

git clone https://github.com/Bmiriam1/MMPantry.git


Open the `MMPantry` folder in Android Studio and let Gradle sync. Once that's done you can just hit Run, or if you want to build from the command line:

.\gradlew.bat assembleDebug
adb install app\build\outputs\apk\debug\app-debug.apk


First time you launch the app it'll seed the database with the recipe list automatically, so you don't need to add anything manually to see it working.

## A quick tour of the code

- `DatabaseHelper.java` — this is where most of the actual logic lives, including the strict-matching function that decides what counts as "suggested"
- `PantryActivity`, `RecipesActivity`, `RecipeDetailActivity`, `AddEditIngredientActivity`, `SettingsActivity` — the five screens
- `PantryAdapter` / `RecipeAdapter` — the RecyclerView adapters for the two list screens
- `NavBar.java` — small helper class for the bottom nav bar, shared across the three main screens so I wasn't repeating the same code three times