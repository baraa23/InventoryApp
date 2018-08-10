package com.example.baraa.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.baraa.inventoryapp.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;


    /**
     * EditText field to enter the book's supplier
     */
    private EditText mSupplierEditText;

    public Spinner mQuantitySpinner;

    /**
     * EditText field to enter the book's supplier phone number
     */
    private EditText mSupplierNumberEditText;

    public static String nameString;
    public static String priceString;
    public static String supplierNameString;
    public static String supplierPhoneString;

    public static int mQuantity = BookEntry.QUANTITY0;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_book));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_book));

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantitySpinner = findViewById(R.id.spinner_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mSupplierNumberEditText = findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantitySpinner.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierNumberEditText.setOnTouchListener(mTouchListener);

        setupSpinner();

    }

    private void setupSpinner() {

        ArrayAdapter quantitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quantity, android.R.layout.simple_spinner_item);

        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mQuantitySpinner.setAdapter(quantitySpinnerAdapter);

        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                        mQuantity = position;
                } else {
                    mQuantity = BookEntry.QUANTITY0;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = BookEntry.QUANTITY0;
            }
        });
    }


    /**
     * Get user input from editor and save new book into database.
     */
    private void saveBook() {


        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER, supplierPhoneString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);

        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Intent i = new Intent(EditorActivity.this, CatalogActivity.class);

                i.setData(mCurrentBookUri);

                startActivity(i);

                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                nameString = mNameEditText.getText().toString().trim();
                priceString = mPriceEditText.getText().toString().trim();
                supplierNameString = mSupplierEditText.getText().toString().trim();
                supplierPhoneString = mSupplierNumberEditText.getText().toString().trim();

                if (
                        !TextUtils.isEmpty(nameString) &&
                        !TextUtils.isEmpty(priceString) &&
                        !TextUtils.isEmpty(supplierNameString) &&
                        !TextUtils.isEmpty(supplierPhoneString)) {

                    saveBook();
                    mQuantity = 0;
                    finish();
                    return true;

                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER};

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mSupplierEditText.setText(supplierName);
            mSupplierNumberEditText.setText(Integer.toString(supplierPhone));


            switch (quantity) {

                case BookEntry.QUANTITY1:
                    mQuantitySpinner.setSelection(1);
                    break;
                case BookEntry.QUANTITY2:
                    mQuantitySpinner.setSelection(2);
                    break;
                case BookEntry.QUANTITY3:
                    mQuantitySpinner.setSelection(3);
                    break;
                case BookEntry.QUANTITY4:
                    mQuantitySpinner.setSelection(4);
                    break;
                case BookEntry.QUANTITY5:
                    mQuantitySpinner.setSelection(5);
                    break;
                case BookEntry.QUANTITY6:
                    mQuantitySpinner.setSelection(6);
                    break;
                case BookEntry.QUANTITY7:
                    mQuantitySpinner.setSelection(7);
                    break;
                case BookEntry.QUANTITY8:
                    mQuantitySpinner.setSelection(8);
                    break;
                case BookEntry.QUANTITY9:
                    mQuantitySpinner.setSelection(9);
                    break;
                case BookEntry.QUANTITY10:
                    mQuantitySpinner.setSelection(10);
                    break;
                default:
                    mQuantitySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantitySpinner.setSelection(0);
        mSupplierEditText.setText("");
        mSupplierNumberEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent(EditorActivity.this, CatalogActivity.class);
                startActivity(i);

            }
        }

        finish();
    }
}