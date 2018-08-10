package com.example.baraa.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baraa.inventoryapp.data.BookContract.BookEntry;

public class BookDetail extends AppCompatActivity {

    Integer quantity;
    TextView nameIn;
    TextView priceIn;
    TextView quantityIn;
    TextView supplierNameIn;
    TextView supplierNumberIn;
    Uri mCurrentUri;
    Uri mNewUri;
    ImageButton deleteButton;
    ImageButton contactButton;
    ImageButton editButton;
    ImageButton minusButton;
    ImageButton plusButton;

    public String supplierNumber;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        Intent i = getIntent();
        mNewUri = i.getData();
        nameIn = findViewById(R.id.book_name);
        quantityIn = findViewById(R.id.book_quantity);
        priceIn = findViewById(R.id.book_price);
        supplierNameIn = findViewById(R.id.supplier_name);
        supplierNumberIn = findViewById(R.id.supplier_phone);
        editButton = findViewById(R.id.edit_button);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);
        deleteButton = findViewById(R.id.delete_button);
        contactButton = findViewById(R.id.contact_button);


        Cursor cNew = managedQuery(mNewUri, null, null, null, "name");

        if (cNew.moveToFirst()) {
            do {
                String productName = cNew.getString(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_NAME));
                String productPrice = cNew.getString(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
                String productQuantity = cNew.getString(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));
                String supplierName = cNew.getString(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER));
                supplierNumber = cNew.getString(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER));
                quantity = cNew.getInt(cNew.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));

                nameIn.setText(" " + productName);
                priceIn.setText(" " + productPrice);
                quantityIn.setText(" " + productQuantity);
                supplierNameIn.setText(" " + supplierName);
                supplierNumberIn.setText(" " + supplierNumber);

            } while (cNew.moveToNext());
        }

        editButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(BookDetail.this, EditorActivity.class);

                intent.setData(mCurrentUri);
                startActivity(intent);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (quantity > 0) {

                    quantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);

                    quantityIn.setText(quantity.toString());
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity < 10) {

                    quantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);
                    quantityIn.setText(quantity.toString());
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteConfirmationDialog();
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierNumber));
                startActivity(intent);
            }
        });

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBook();
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

    private void deleteBook() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}
