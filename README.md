# Smart Pantry Manager

## About the App

Smart Pantry Manager is an Android application developed in Java to help users manage pantry ingredients and discover recipes they can prepare with the ingredients they have available.

The app stores pantry information locally and checks whether the user has all the required ingredients and sufficient quantities for a recipe.

## Features

- Add pantry ingredients with a name, quantity, unit, and optional expiry date.
- View ingredients stored in the pantry.
- Update and delete pantry ingredients.
- View suggested recipes based on available ingredients.
- Match recipes only when all required ingredients and quantities are available.
- View recipe ingredients and preparation instructions.
- Access the settings and profile screen.
- Store information locally using SQLite.

## Technologies Used

- **Programming language:** Java
- **Development environment:** Android Studio
- **Database:** SQLite
- **User interface:** Android XML layouts and Java-based activity layouts
- **Version control:** Git and GitHub

## Database

The application uses SQLite because it provides local relational data storage without requiring a separate database server or an internet connection.

The database is named `smart_pantry.db` and includes tables for:

- **Ingredients:** Stores pantry ingredient names, quantities, units, and expiry dates.
- **Recipes:** Stores recipe names and preparation instructions.
- **Recipe Ingredients:** Stores the ingredients and required quantities associated with each recipe.

The application includes seeded recipe data to provide recipes for matching against the user's pantry.

## Recipe Matching

The recipe matching feature compares the ingredients and quantities in the pantry with the requirements of each recipe.

A recipe is suggested only when all its required ingredients are available in sufficient quantities. Recipes with missing ingredients or insufficient quantities should not appear in the suggested recipes list.

## Project Structure

The Java source code is organised into the `com.smartpantrymanager` package. The application includes activities and database helper classes for pantry management, recipe suggestions, recipe details, and settings.

## How to Run the Application

1. Install Android Studio.
2. Clone or download this repository.
3. Open the project in Android Studio.
4. Allow Gradle to sync and download the required dependencies.
5. Select an Android emulator or connect an Android device with USB debugging enabled.
6. Run the application using Android Studio.

To build the project from the Windows terminal, open the project folder and run:

```powershell
.\gradlew.bat assembleDebug
```

A successful build confirms that the project compiles. The app should also be tested on an emulator or physical Android device to verify its runtime behaviour.

## Purpose

This project demonstrates Android application development, Java programming, SQLite database integration, data management, activity navigation, and ingredient-based recipe matching.