package com.gui.inventoryapp.constant;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemConstants {

    public static final String DB_NAME = "guinventory.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "items";
    public static final String DEFAULT_SORT = ITEM.ENTRY_DATE + " DESC";
    // Constantes del content provider
    // content://com.example.jadiego.yamba.StatusProvider/status
    public static final String AUTHORITY = "com.gui.inventoryapp.contentprovider.ItemProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
    public static final int DATA_ITEMS = 1;
    public static final int DATA_ITEM = 2;

    public class ITEM{
        public static final String ID = BaseColumns._ID;
        public static final String BARCODE = "barcode";
        public static final String OWNER = "owner";
        public static final String ENTRY_DATE = "entry_date";
        public static final String CONDITION = "condition";
        public static final String CHECKOUT_EXPIRE_DATE = "checkout_expire_date";
        public static final String GIVEN_TO = "given_to";
    }
}
