package com.gui.inventoryapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseConstants {

    public static final String DB_NAME = "guinventory.db";
    public static final int DB_VERSION = 1;

    public static final String DEFAULT_SORT_M = Member.LASTNAME + " DESC";
    public static final String DEFAULT_SORT_I = Item.ENTRY_DATE + " ASC";
    public static final String DEFAULT_SORT_L = Loan.START_OF_LOAN + " DESC";


    public static final String AUTHORITY = "com.gui.inventoryapp.database.contentProviders.";

    public static final String TABLE_ITEM ="item";
    public static final String TABLE_MEMBER ="member";
    public static final String TABLE_LOAN ="loan";

    public static final String AUTHORITY_ITEM =  AUTHORITY + "ItemProvider";
    public static final String AUTHORITY_MEMBER =  AUTHORITY + "MemberProvider";
    public static final String AUTHORITY_LOAN =  AUTHORITY + "LoanProvider";

    public static final Uri CONTENT_URI= Uri.parse("content://" + TABLE_ITEM + "/");

    public static final String CONTENT_URI_ITEM = "content://" + AUTHORITY_ITEM + "/" + TABLE_ITEM ;
    public static final String CONTENT_URI_MEMBER= "content://" + AUTHORITY_MEMBER + "/" + TABLE_MEMBER;
    public static final String CONTENT_URI_LOAN = "content://" + AUTHORITY_LOAN + "/" + TABLE_LOAN;

    public static final String COUNT = "count";

    public static final int CASE_MEMBERS = 1;
    public static final int CASE_MEMBER = 2;

    public static final int CASE_ITEMS = 1;
    public static final int CASE_ITEM = 2;

    public static final int CASE_LOANS = 1;
    public static final int CASE_LOAN = 2;

    public static final String ACTIVE_LOAN_SELECTION = DatabaseConstants.Loan.ITEM + "=" + "%d"
            + " and "  + DatabaseConstants.Loan.RETURNED + "=" + "0";


    public class Item {
        public static final String ID = BaseColumns._ID;
        public static final String BARCODE = "barcode";
        public static final String OWNER = "owner";
        public static final String ENTRY_DATE = "entry_date";
        public static final String DAMAGED = "damaged";
    }

    public class Member{
        public static final String ID = BaseColumns._ID;
        public static final String ALIAS = "alias";
        public static final String NAME = "name";
        public static final String LASTNAME = "lastname";
        public static final String DNI= "dni";
        public static final String EMAIL= "email";
        public static final String PHONE= "phone";
    }

    public class Loan{
        public static final String ID = BaseColumns._ID;
        public static final String START_OF_LOAN = "start_of_loan";
        public static final String END_OF_LOAN =  "end_of_loan";
        public static final String ITEM = "item";
        public static final String MEMBER = "member";
        public static final String RETURNED = "returned";
    }



}
