package com.example.baraa.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Books app.
 */
public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider.
     */
    public static final String CONTENT_AUTHORITY = "com.example.baraa.inventoryapp";

    /**
     * CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.baraa.inventoryapp/books/ is a valid path for
     * looking at book data. content://com.example.baraa.inventoryapp/books/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";


    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /**
         * The content URI to access the books data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the book.
         */
        public final static String COLUMN_BOOK_NAME = "name";

        /**
         * Price of the book.
         */
        public final static String COLUMN_BOOK_PRICE = "price";

        /**
         * quantity of the book.
         */
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        public static final int QUANTITY0 = 0;
        public static final int QUANTITY1 = 1;
        public static final int QUANTITY2 = 2;
        public static final int QUANTITY3 = 3;
        public static final int QUANTITY4 = 4;
        public static final int QUANTITY5 = 5;
        public static final int QUANTITY6 = 6;
        public static final int QUANTITY7 = 7;
        public static final int QUANTITY8 = 8;
        public static final int QUANTITY9 = 9;
        public static final int QUANTITY10 = 10;


        /**
         * Returns whether or not the given quantity is a number between 0 and 10
         */
        public static boolean isValidQuantity(int quantity) {
            if (quantity == QUANTITY0 || quantity == QUANTITY1 || quantity == QUANTITY2 || quantity == QUANTITY3
                    || quantity == QUANTITY4 || quantity == QUANTITY5 || quantity == QUANTITY6
                    || quantity == QUANTITY7 || quantity == QUANTITY8 || quantity == QUANTITY9
                    || quantity == QUANTITY10) {
                return true;
            }
            return false;
        }

        /**
         * Name of the supplier.
         */
        public final static String COLUMN_BOOK_SUPPLIER = "supplierName";

        /**
         * Phone number of the book supplier.
         */
        public final static String COLUMN_BOOK_SUPPLIER_NUMBER = "supplierNumber";


    }

}