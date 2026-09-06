echo $env:JAVA_HOME# MM Pantry

**Made with love from your pantry.**

MM Pantry is a Java Android application that helps users reduce food waste by suggesting recipes only when every required ingredient is available in the pantry in the required quantity. Its design combines a chic Spanish-and-Italian summer mood with a clean, feed-inspired layout.

## Technology

The project uses Java, Android Studio, XML-compatible Android views created in Java, AndroidX AppCompat, Material Components, RecyclerView, SQLiteOpenHelper, Activities, Intents, and SharedPreferences for theme preference persistence. It does not require an internet connection or a recipe API.

## Setup

Open the `MMPantry` folder in Android Studio, allow Gradle to synchronise, and run the app on an Android emulator or physical device. The app seeds its recipe collection the first time the database is created.

## Assessment evidence checklist

Demonstrate adding, viewing, editing, and deleting a pantry item; persistence after closing and reopening; strict matching by adding/removing an ingredient; light/dark mode; recipe detail navigation; validation feedback; and the database/matching code during the video.

## Suggested incremental commit history

Use genuine commits during development, for example: scaffold project, add theme resources, add database helper, seed recipes, implement pantry CRUD, add RecyclerView adapter, add strict matching, add recipe detail, add settings theme switch, test empty states, polish UI, and update documentation. Do not manufacture history after the fact.
