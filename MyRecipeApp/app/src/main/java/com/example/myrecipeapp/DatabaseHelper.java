package com.example.myrecipeapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RecipeDatabase";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_RECIPES = "recipes";

    private static final String KEY_USER_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_RECIPE_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_USER_FK = "user_id";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String LOGGED_IN_USER_ID = "loggedInUserId";
    private static final String LOGGED_IN_USER_EMAIL = "loggedInUserEmail";
    private static final String LOGGED_IN_USER_PASSWORD = "loggedInUserPassword";


    SharedPreferences sharedPreferences;
    public String getSavedEmail() {
        return sharedPreferences.getString(LOGGED_IN_USER_EMAIL, null);
    }

    public String getSavedPassword() {
        return sharedPreferences.getString(LOGGED_IN_USER_PASSWORD, null);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_EMAIL + " TEXT," +
                KEY_PASSWORD + " TEXT" + ")";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "(" +
                KEY_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_TITLE + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_CATEGORY + " TEXT," +
                KEY_IMAGE_URL + " TEXT," +
                KEY_USER_FK + " INTEGER," +
                "FOREIGN KEY(" + KEY_USER_FK + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_EMAIL, email);
        contentValues.put(KEY_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean signIn(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ? AND " + KEY_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] {
                email,
                password
        });

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
            saveLoggedInUser(userId, email, password);
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    public void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(LOGGED_IN_USER_ID, -1);
    }

    public boolean addRecipe(String title, String description, String category, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_DESCRIPTION, description);
        contentValues.put(KEY_CATEGORY, category);
        contentValues.put(KEY_IMAGE_URL, imageUrl);
        contentValues.put(KEY_USER_FK, getUserId());

        long result = db.insert(TABLE_RECIPES, null, contentValues);

        return result != -1;
    }

    public List < Recipe > getAllRecipes() {
        List < Recipe > recipeList = new ArrayList < > ();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_RECIPES + " WHERE " + KEY_USER_FK + " = " + getUserId();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                String category = cursor.getString(3);
                String imageUri = cursor.getString(4);

                Recipe recipe = new Recipe(id, title, description, category, imageUri);
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipeList;
    }

    public boolean deleteRecipe(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RECIPES, KEY_RECIPE_ID + "=?", new String[] {
                String.valueOf(recipeId)
        }) > 0;
    }

    public boolean updateRecipe(int recipeId, String title, String description, String category, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("category", category);

        contentValues.put("image_url", imageUrl);

        int numRowsAffected = db.update(TABLE_RECIPES, contentValues, KEY_RECIPE_ID + "=?", new String[] {
                String.valueOf(recipeId)
        });
        return numRowsAffected > 0;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?", new String[] {
                String.valueOf(userId)
        }, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            cursor.close();
            return new User(userId, email, password);
        }
        return null;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_PASSWORD, newPassword);

        int numRowsAffected = db.update(TABLE_USERS, contentValues, KEY_USER_ID + "=?", new String[] {
                String.valueOf(userId)
        });
        return numRowsAffected > 0;
    }

    public void saveLoggedInUser(int userId, String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOGGED_IN_USER_ID, userId);
        editor.putString(LOGGED_IN_USER_EMAIL, email);
        editor.putString(LOGGED_IN_USER_PASSWORD, password);
        editor.apply();
    }


}