package com.example.baraa.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.baraa.inventoryapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {


    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();


    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;


        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:

                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("book requires a name");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || !BookEntry.isValidQuantity(quantity)) {
            throw new IllegalArgumentException("book requires valid quantity");
        }

        Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("book requires valid price");
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("book requires a supplier name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER)) {
            Integer supplierPhone = values.getAsInteger(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("book requires valid supplier phone");
            }
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;

        }

        // Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:

                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("book requires a name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || !BookEntry.isValidQuantity(quantity)) {
                throw new IllegalArgumentException("book requires valid quantity");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {

            Double price = values.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("book requires valid price");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("book requires a supplier name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER)) {
            Integer supplierNumber = values.getAsInteger(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("book requires valid supplier phone");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
