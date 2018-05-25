package com.example.android.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract;

import java.text.DecimalFormat;

/**
 * Created by DTPAdmin on 18/05/2018.
 */

public class ProductCursorAdapter extends CursorAdapter {

    /** declarations of the views
     * to be handled by the adapter
     * **/

    // TextViews
    private TextView nameTextView, quantityTextView, priceTextView;

    // ImageView
    private ImageView saleImageView;

    /** **/

    // Constructor for the CursorAdapter where "c" is the cursor from which to get the data
    public ProductCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    // Creates a blank list item view, without setting any data
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // Return the view by inflating it
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item, viewGroup, false);

        return view;
    }

    // Binds the Product data through use of the cursor (which row)
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Initialises the views
        initializeViews(view);

        // Find the columns of the Product that we need
        int nameColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int idColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry._ID);

        // Read the values from the current Product
        String productName = cursor.getString(nameColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        Double productPrice = cursor.getDouble(priceColumnIndex);
        final Long productId = cursor.getLong(idColumnIndex);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        String formattedPrice = decimalFormat.format(productPrice);

        // Populate the TextViews with the read values
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(formattedPrice);

        // Check for 0 quantity
        checkForZeroQuantity();

        // Set an onClickListener for the saleImageView
        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the quantity as an integer
                int currentQuantity = Integer.parseInt(productQuantity);

                // Decrement the quantity by one
                currentQuantity-=1;
                Uri quantityUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, productId);

                // Assign the new values
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);

                // Update the counter (decrement by one)
                context.getContentResolver().update(quantityUri, values, null, null);

                // Provide a toast message saying the product has been sold or sold out
                if (currentQuantity == 0){
                    String message = context.getString(R.string.productSoldOut);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    String message = context.getString(R.string.productSold);
                    Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Helper method to initialise the views
    private void initializeViews(View view){
        nameTextView = view.findViewById(R.id.product_name);
        quantityTextView = view.findViewById(R.id.product_Quantity);
        priceTextView = view.findViewById(R.id.product_price);
        saleImageView = view.findViewById(R.id.saleImage);
    }

    // Helper method to look for zero quantity
    private void checkForZeroQuantity(){
        int quantity = Integer.parseInt(quantityTextView.getText().toString());

        // If there are no more products available for sale
        // disable the sales button
        if (quantity == 0){
            saleImageView.setVisibility(View.INVISIBLE);
        } else {
            saleImageView.setVisibility(View.VISIBLE);
        }
    }
}
