package com.example.android.inventoryapp.UI;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.adapters.ProductCursorAdapter;
import com.example.android.inventoryapp.data.ProductContract;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Constants and variables used in this activity
     */
    // Identifier for the Product loader
    private static final int PRODUCT_LOADER = 0;

    // Adapter for the listview
    ProductCursorAdapter mCursorAdapter;

    // ListView for the list of products
    ListView productListView;

    // Empty View
    View emptyView;

    /** **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Initialise the views
        initialiseViews();

        // Initialise the Cursor adapter
        initialiseCursorAdapter();

        // Set up the item click listener on the ListView
        initialiseOnClickClickListener();

        // Start the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        // Setup FAB to open the DetailsActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        CatalogActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to initialise the views
    private void initialiseViews(){
        productListView = findViewById(R.id.productListView);
        emptyView = findViewById(R.id.empty_view_group);
        // set the emptyView viewgroup as empty view for the ProductListview
        productListView.setEmptyView(emptyView);
    }

    // Method to initialise the CursorAdapter
    private void initialiseCursorAdapter(){
        // No data yet until the loader has finished, so we pass null here
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);
    }

    // Method to initialise the click listener on the listview
    private void initialiseOnClickClickListener(){
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(
                        CatalogActivity.this, DetailsActivity.class);

                // Content Uri for the particular item that was was clicked
                Uri currentProductUri = ContentUris.withAppendedId(
                        ProductContract.ProductEntry.CONTENT_URI, id);

                // Add this Content Uri to the intent
                intent.setData(currentProductUri);

                // Launch the DetailsActivity with the data passed along to the intent
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Projection to specify which columns we are interested in
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the CursorAdapter with this new cursor, containing updated Product Data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
