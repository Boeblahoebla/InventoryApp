package com.example.android.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract;

/**
 * Created by DTPAdmin on 18/05/2018.
 */

public class ProductCursorAdapter extends CursorAdapter {

    /** constants of the views
     * to be handled by the adapter
     * **/

    // TextViews
    TextView nameTextView, quantityTextView, priceTextView;

    // ImageView
    ImageView saleImageView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Initialises the views
        initializeViews(view);

        // Find the columns of the Product that we need
        int nameColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);

        // Read the values from the current Product
        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

        // Populate the TextViews with the read values
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);

        // Check for zero quantity to disable the sale imageView
        // Which can only be visible when there are products available for sale
        checkForZeroQuantity();
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
        String quantity = quantityTextView.getText().toString();

        // If there are no more products available for sale
        // disable the sales button
        if (quantity == "0"){
            saleImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        saleImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });

        return super.getView(position, convertView, parent);
    }
}
