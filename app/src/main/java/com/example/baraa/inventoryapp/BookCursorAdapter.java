package com.example.baraa.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.baraa.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        Button saleButton = view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);

        final String productName = cursor.getString(nameColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final String productPrice = cursor.getString(priceColumnIndex);

        final Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID)));

        nameTextView.setText(productName);
        quantityTextView.setText(" " + productQuantity);
        priceTextView.setText(" " + productPrice);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (quantity > 0) {
                    Integer afterSale = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, afterSale);
                    context.getContentResolver().update(uri, values, null, null);

                    quantityTextView.setText(afterSale.toString());
                }
            }
        });

    }
}
