package com.example.android.inventoryapp.UI;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract;

import java.util.Set;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Constant for loader(s)
     * and declarations for the views and listeners being used
     */
    // Identifier for the product data loader
    private static final int EXISTING_PRODUCT_LOADER = 0;

    // Content URI of an existing product,
    // which is null when it is a new product to be added
    private Uri mCurrentProductUri;

    // TextViews
    private TextView mTextViewActivityLabel;

    // EditTextViews
    private EditText mEditTextProductName, mEditTextProductQuantity, mEditTextProductPrice,
                        mEditTextSupplier, mEditTextSupplierEmail, mEditTextSupplierPhone;

    // ImageViews
    private ImageView plusSign, minusSign, orderMail, orderPhone;

    // Boolean to keep track of changes in the current product
    private boolean mProductHasChanged = false;

    // Floating action buttons
    private FloatingActionButton fabSave, fabDelete;

    // set an onTouch listener to listen for touches of the views,
    // implying information ahs been changed
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    /** **/

    // OnCreate method of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Check which uri is active
        Intent intentToCheck = getIntent();
        mCurrentProductUri = intentToCheck.getData();

        // Initialise all the relevant Views
        initialiseViews();

        // Initialise onTouchListeners to the relevant views
        initialiseOnTouchListeners();

        // Setup FAB to save the Product
        initialiseFabs();

        // Check which intent was used to start this activity
        // and set the top label accordingly
        setLabelAndButtonsForActivity();
    }

    // Method to initialise the floating action buttons
    private void initialiseFabs(){
        fabSave = findViewById(R.id.fabAdd);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bail out of the method if the information is not validated
                if (!validateInput()){
                    return;
                } else {
                    saveProduct();
                    finish();
                }
            }
        });

        fabDelete = findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    // Method to set the label for the activity
    private void setLabelAndButtonsForActivity(){
        // Change the fab icon to a save button
        fabSave.setImageResource(R.drawable.ic_save_button);

        // If the intent data is empty, set the label to "Add a product"
        if (mCurrentProductUri == null){
            mTextViewActivityLabel.setText(getString(R.string.editorActivityLabelNewProduct));

            // Disable the button to delete
            fabDelete.setVisibility(View.GONE);

            // if the intent data is not empty, but holds a certain product
            // set the label to "Edit a product" and initialise the loader to read the product data
            // from the database
        } else {
            mTextViewActivityLabel.setText(getString(R.string.editorActivityLabelEditProduct));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
    }

    // Method to initialise the views
    private void initialiseViews(){
        mTextViewActivityLabel = findViewById(R.id.activityLabel);

        mEditTextProductName = findViewById(R.id.editTextProductName);
        mEditTextProductQuantity = findViewById(R.id.editTextProductQuantity);
        mEditTextProductPrice = findViewById(R.id.editTextProductPrice);
        mEditTextSupplier = findViewById(R.id.editTextSupplier);
        mEditTextSupplierEmail = findViewById(R.id.editTextSupplierEmail);
        mEditTextSupplierPhone = findViewById(R.id.editTextSupplierPhone);

        plusSign = findViewById(R.id.ic_plus);
        minusSign = findViewById(R.id.ic_minus);
        orderMail = findViewById(R.id.ic_mail);
        orderPhone = findViewById(R.id.ic_phone);

        if (mCurrentProductUri == null){
            orderMail.setVisibility(View.GONE);
            orderPhone.setVisibility(View.GONE);
        }

        // Set an OnClickListener to increment the counter
        plusSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int crement = 1;
                changeCounter(crement);
            }
        });

        // Set an OnClickListener to decrement the counter
        minusSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int crement = -1;
                changeCounter(crement);
            }
        });

        // Set an OnClickListener to send an order through email
        orderMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String howToContact = "mail";
                contactSupplier(howToContact);
            }
        });

        // Set an OnClickListener to send an order through telephone
        orderPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String howToContact = "phone";
                contactSupplier(howToContact);
            }
        });
    }

    // Method to contact the supplier
    private void contactSupplier(String how){
        if (how.equals("mail")){
            // Get variables to use in intent
            String mailAddress = mEditTextSupplierEmail.getText().toString();
            String productName = mEditTextProductName.getText().toString();

            // Set up and start the intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                    mailAddress));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject) +
                    productName);

            startActivity(Intent.createChooser(emailIntent, "Mail supplier"));

        } else {
            // Get variables to use in intent
            String phone = mEditTextSupplierPhone.getText().toString().trim();

            // Setup and start the intent
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }

    // Method to change the counter
    private void changeCounter(int amountToChange){
        // Variables for the calculation
        int currentAmount;
        if (mEditTextProductQuantity.getText().toString().equals("") ){
            currentAmount = 0;
        } else {
            currentAmount = Integer.parseInt(mEditTextProductQuantity.getText().toString());
        }

        int resultingAmount;

        if (currentAmount < 1 && amountToChange == -1){
            Toast.makeText(this,getString(R.string.noNegValues),Toast.LENGTH_SHORT).show();
        } else {
            resultingAmount = currentAmount + amountToChange;
            // Set the resulting amount as text for the EditText field
            mEditTextProductQuantity.setText(String.valueOf(resultingAmount));
        }
    }

    // Method to set the onTouchListeners to all the views which can be changed
    // A touch implies that a view has changed
    private void initialiseOnTouchListeners(){
        mEditTextProductName.setOnTouchListener(mOnTouchListener);
        mEditTextProductQuantity.setOnTouchListener(mOnTouchListener);
        mEditTextProductPrice.setOnTouchListener(mOnTouchListener);
        mEditTextSupplier.setOnTouchListener(mOnTouchListener);
        mEditTextSupplierEmail.setOnTouchListener(mOnTouchListener);
        mEditTextSupplierPhone.setOnTouchListener(mOnTouchListener);
    }

    // Method to validate the input
    private boolean validateInput(){
        String productName = mEditTextProductName.getText().toString().trim();
        String productQuantity = mEditTextProductQuantity.getText().toString().trim();
        String productPrice = mEditTextProductPrice.getText().toString().trim();
        String productSupplier = mEditTextSupplier.getText().toString().trim();
        String productSupplierEmail = mEditTextSupplierEmail.getText().toString().trim();
        String productSupplierPhone = mEditTextSupplierPhone.getText().toString().trim();

        // If the information is incomplete or email does not contain "@"
        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productQuantity) ||
                TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productSupplier) ||
                TextUtils.isEmpty(productSupplierEmail) || TextUtils.isEmpty(productSupplierPhone) ||
                !productSupplierEmail.contains("@")){

            Toast.makeText(this,getString(R.string.checkIncomplete),Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // Method to save the product in the database
    public void saveProduct(){
        // Fetch all the strings from the editTexts
        String productName = mEditTextProductName.getText().toString().trim();
        String productQuantity = mEditTextProductQuantity.getText().toString().trim();
        String productPrice = mEditTextProductPrice.getText().toString().trim();
        String productSupplier = mEditTextSupplier.getText().toString().trim();
        String productSupplierEmail = mEditTextSupplierEmail.getText().toString().trim();
        String productSupplierPhone = mEditTextSupplierPhone.getText().toString().trim();

        // When it is a new product and all the values are empty,
        // just quit the method early as nothing is being added
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productName) && TextUtils.isEmpty(productQuantity) &&
                TextUtils.isEmpty(productPrice) && TextUtils.isEmpty(productSupplier) &&
                TextUtils.isEmpty(productSupplierEmail) && TextUtils.isEmpty(productSupplierPhone)){
            // No fields were modified so do nothing
            return;
        }

        // Create content values object and fill the object with the values,
        // read from the editTexts
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, productSupplierEmail);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, productSupplierPhone);

        // Determine if this is a new product or an existing one
        if (mCurrentProductUri == null){
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            // Show a Toast message saying whether the insert was successful or not
            // an empty Uri object = failed, a filled Uri object = successful
            if (newUri == null){
                Toast.makeText(this,getString(R.string.insertionFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,getString(R.string.insertionSuccess), Toast.LENGTH_SHORT).show();
            }
        } else {
            // return the rows affected by the update method
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a Toast message saying whether the insert was successful or not
            // no rows affected (0) = failed, more than 0 rows affected = successful
            if (rowsAffected == 0){
                Toast.makeText(this,getString(R.string.updateFailed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,getString(R.string.updateSuccess), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to delete the product in the database
    public void deleteProduct(){
        getContentResolver().delete(mCurrentProductUri, null, null);
        Toast.makeText(this,getString(R.string.productDeleted),
                Toast.LENGTH_SHORT).show();
        finish();
    }

    // Method to show the dialog for unsaved data
    private void showUnsavedDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dataLeftUnchanged));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keepEdit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    // Method to show the dialog for deletion
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sureDelete));
        builder.setPositiveButton(getString(R.string.yesDelete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    // Detect when the "home" button is pressed in the application bar
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // prompt the user the confirmation dialog
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // Detect when the "back" button is pressed on the devide
    public void onBackPressed() {
        // if the product has not changed
        // go back to the previous activity and quit this method
        if (!mProductHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User has clicked the "discard" button so the activity can finish
                finish();
            }
        };

        showUnsavedDialog(discardButtonClickListener);
    }

    @Override
    // Mandatory override callback to create the loader
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // The DetailsActivity shows all pet attributes, so we need a projection that
        // contains all the columns from the products table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE };

        // This cursor will execute the ContentProvider's query method
        // on a background thread
        return new CursorLoader(this,mCurrentProductUri, projection,null,null, null);
    }

    @Override
    // Mandatory override callback when the loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Stop if there is no data to be handled in the cursor
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        // Move to the first row in the cursor which is the only one
        if (cursor.moveToFirst()){
            // Assign column indices
            int productNameColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int productQuantityColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productPriceColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int productSupplierColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int productSupplierEmailColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int productSupplierPhoneColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Extract the values at the given column indices
            // and assign them to variables to be used
            String productName = cursor.getString(productNameColumnIndex);
            int quantity = cursor.getInt(productQuantityColumnIndex);
            int price = cursor.getInt(productPriceColumnIndex);
            String supplier = cursor.getString(productSupplierColumnIndex);
            String supplierEmail = cursor.getString(productSupplierEmailColumnIndex);
            int supplierPhone = cursor.getInt(productSupplierPhoneColumnIndex);

            // Update the views with the respective data
            mEditTextProductName.setText(productName);
            mEditTextProductQuantity.setText(Integer.toString(quantity));
            mEditTextProductPrice.setText(Integer.toString(price));
            mEditTextSupplier.setText(supplier);
            mEditTextSupplierEmail.setText(supplierEmail);
            mEditTextSupplierPhone.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    // Mandatory override callback when the loader resets
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the text from the EditText fields
        mEditTextProductName.setText("");
        mEditTextProductQuantity.setText("");
        mEditTextProductPrice.setText("");
        mEditTextSupplier.setText("");
        mEditTextSupplierEmail.setText("");
        mEditTextSupplierPhone.setText("");
    }
}
