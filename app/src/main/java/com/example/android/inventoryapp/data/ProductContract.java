package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DTPAdmin on 17/05/2018.
 */

public final class ProductContract {

    // Empty constructor
    private ProductContract(){}

    /**
     * Constants related to
     * the content provider
     */
    // Content Authority (name for entire content provider)
    public static final String CONTENT_AUTHORITY = "com.example.inventoryapp";

    // Base of all URI's apps will use to contect the contact provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path
    // eg, content://com.example.android.inventoryapp/products is valid
    // eg, content://com.example.android.inventoryapp/boeblahoebla is invalid
    public static final String PATH_PRODUCTS = "products";

    /** Inner class wich constants for the Products database table
     * Each entry is a single product
     */
    public static final class ProductEntry implements BaseColumns {
        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Table name **/
        public final static String TABLE_NAME = "Products";

        /** Table columns **/
        // ID, type INTEGER
        public final static String _ID = BaseColumns._ID;

        // Product name, type TEXT
        public final static String COLUMN_PRODUCT_NAME = "product_name";

        // Product quantity, type INTEGER
        public final static String COLUMN_PRODUCT_QUANTITY = "product_quantity";

        // Product price, type double
        public final static String COLUMN_PRODUCT_PRICE = "product_price";

        // Product supplier, type TEXT
        public final static String COLUMN_PRODUCT_SUPPLIER = "product_supplier";

        // Product supplier phone, type INTEGER
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "product_supplier_phone";

        // Product supplier email, type TEXT
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "product_supplier_email";
    }
}
