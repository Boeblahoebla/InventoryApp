package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by DTPAdmin on 17/05/2018.
 */

public class ProductProvider extends ContentProvider {

    /** Database Helper object **/
    private ProductDbHelper mDbHelper;

    /** Constants for the URI matcher **/
    // URI matcher object to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Uri match code for the entire products table
    private static final int PRODUCTS = 100;

    // URI match code for a single product in the products table
    private static final int PRODUCT_ID = 101;

    // Static initializer which gets run,
    // the first time everything is called from this class
    static {
        // Add a Uri to the UriMatcher for the entire products table
        sUriMatcher.addURI(
                ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS, PRODUCTS);
        // Add a Uri to the UriMatcher for a single product in the products table
        sUriMatcher.addURI(
                ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Initialise an empty cursor object
        Cursor cursor;

        // Add a uri to match as an integer
        int match = sUriMatcher.match(uri);

        // Find a Uri match
        switch (match) {
            // in case the URI matches the PRODUCTS uri
            case PRODUCTS:
                // No selection and selectionArgs needed, so
                // initialise the cursor with selection and selectionArgs as null
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            // in case the URI matches the PRODUCT_ID uri
            case PRODUCT_ID:
                // We now need a selection and selection-arguments
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                // initialise the cursor with valid selection and selectionArgs
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            // in neither case, throw an exception
            default:
                throw new IllegalArgumentException("Can not query unknown URI " + uri);
        }

        // Set a notification on the Uri to know what content URI the cursor was created for
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // return the cursor of the queried data
        return cursor;
    }

    // Generic insert
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Add a uri to match as an integer
        int match = sUriMatcher.match(uri);
        // Find a Uri match
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert a Product with the given values and return the
    // content URI for that particular row in the database
    private Uri insertProduct(Uri uri, ContentValues values) {

        // TODO: DATA validation if the data entered is correct

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        // The id gets the value of the newly inserted row or -1 if the insert has failed
        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME,null, values);

        // If the insert has failed, return "null" as there is no URI for an inserted row
        if (id == -1){
            Log.e("ProductProvider", "Failed to insert row for " + uri);
            return null;
        }

        // Notify the listeners that the data has changed for the Product
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the uri for the row-ID which has been added
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionargs) {
        // Initiate a counter for the rows, affected by the update
        int rowsAffected;

        // Add a uri to match as an integer
        int match = sUriMatcher.match(uri);

        // Find a match
        switch (match){
            case PRODUCTS:
                // No selection and selectionArgs needed, so
                // fill the rows affected through the updateProduct method with
                // selection and selectionArgs as null
                rowsAffected = updateProduct(uri, contentValues, selection, selectionargs);
                return rowsAffected;
            case PRODUCT_ID:
                // We now need a selection and selection-arguments
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionargs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // fill the rows affected through the updateProduct method with
                // valid selection and selectionArgs
                rowsAffected = updateProduct(uri, contentValues, selection, selectionargs);
                return rowsAffected;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Update method to apply changes to the rows, specified in selection and selectionArgs
    // and return the number of rows affected
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        // TODO: DATA validation if the data entered is correct

        // Get a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // initiate a counter for the rows, affected by the update
        int rowsAffected = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If there are rows affected notify the listeners of such
        if (rowsAffected != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the rows affected
        return rowsAffected;
    }

    // Delete method which returns the number of rows deleted
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Initiate a counter for the rows, deleted
        int rowsDeleted;

        // Add a uri to match as an integer
        final int match = sUriMatcher.match(uri);

        // Find a match
        switch (match){
            case PRODUCTS:
                // No selection and selectionArgs needed, so
                // delete all the rows because selection and selectionArgs are null
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // We now have a selection and selectionArgs, so
                // delete the selected rows with the valid selection and selectionArgs
                selection = PRODUCT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If there are rows affected notify the listeners of such
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    // Method to identify the type of Uri
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Add a uri to match as an integer
        final int match = sUriMatcher.match(uri);

        // Find a match
        switch (match){
            case PRODUCTS:
                // We expect a list as type since we use the whole table
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                // We expect a list item as type because we work with specific items in the table
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}