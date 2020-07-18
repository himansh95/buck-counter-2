package com.himanshu.buckcounter.business;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.himanshu.buckcounter.business.Constants.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean isCreating = false;
    private SQLiteDatabase currentDB = null;

    public static DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_ACCOUNTS + "("
                + KEY_ACCOUNTS_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + KEY_ACCOUNTS_NAME + " text NOT NULL,"
                + KEY_ACCOUNTS_BALANCE + " real NOT  NULL"
                + ")"
        );
        sqLiteDatabase.execSQL("create table " + TABLE_TRANSACTIONS + "("
                + KEY_TRANSACTIONS_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRANSACTIONS_TYPE + " text NOT NULL,"
                + KEY_TRANSACTIONS_PARTICULARS + " text,"
                + KEY_TRANSACTIONS_AMOUNT + " real NOT NULL,"
                + KEY_TRANSACTIONS_TIMESTAMP + " datetime NOT NULL,"
                + KEY_TRANSACTIONS_DR_ACCOUNT_ID + " integer NOT NULL,"
                + KEY_TRANSACTIONS_CR_ACCOUNT_ID + " integer NOT NULL,"
                + "FOREIGN KEY(" + KEY_TRANSACTIONS_DR_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + KEY_ACCOUNTS_ID + "),"
                + "FOREIGN KEY(" + KEY_TRANSACTIONS_CR_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + KEY_ACCOUNTS_ID + ")"
                + ")"
        );
        isCreating = true;
        currentDB = sqLiteDatabase;
        insertDummyContent(sqLiteDatabase);
        // release var
        isCreating = false;
        currentDB = null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_ACCOUNTS);
        sqLiteDatabase.execSQL("drop table " + TABLE_TRANSACTIONS);
        sqLiteDatabase.execSQL("drop trigger if exists " + TRIGGER_TRANSACTIONS_INSERT);
        sqLiteDatabase.execSQL("drop trigger if exists " + TRIGGER_TRANSACTIONS_DELETE);
        sqLiteDatabase.execSQL("drop trigger if exists " + TRIGGER_TRANSACTIONS_UPDATE_AMOUNT);
        onCreate(sqLiteDatabase);
    }

    private void insertDummyContent(SQLiteDatabase sqLiteDatabase) {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("cash"));
        accounts.add(new Account("bank"));
        accounts.add(new Account("credit card"));
        ContentValues contentValues = new ContentValues();
        for (Account account : accounts) {
            contentValues.put(KEY_ACCOUNTS_NAME, account.getName());
            contentValues.put(KEY_ACCOUNTS_BALANCE, account.getBalance());
            sqLiteDatabase.insert(TABLE_ACCOUNTS, null, contentValues);
            contentValues.clear();
        }
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(Transaction.TransactionType.DR, "opening balance", 230.0, new Date(), 1));
        transactions.add(new Transaction(Transaction.TransactionType.CR, "opening balance", 3000.0, new Date(), 3));
        transactions.add(new Transaction(Transaction.TransactionType.DR, "opening balance", 2500.0, new Date(), 2));
        transactions.add(new Transaction(Transaction.TransactionType.CONTRA, "ATM Withdrawal", 500.0, new Date(), 1, 2));
        for (Transaction transaction : transactions) {
            contentValues.put(KEY_TRANSACTIONS_TYPE, transaction.getTransactionType().getName());
            contentValues.put(KEY_TRANSACTIONS_PARTICULARS, transaction.getParticulars());
            contentValues.put(KEY_TRANSACTIONS_AMOUNT, transaction.getAmount());
            Date date = new Date();
            contentValues.put(KEY_TRANSACTIONS_TIMESTAMP, dateFormat.format(date));
            if (transaction.getTransactionType() == Transaction.TransactionType.DR || transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
                contentValues.put(KEY_TRANSACTIONS_DR_ACCOUNT_ID, transaction.getDebitAccountId());
            }
            if (transaction.getTransactionType() == Transaction.TransactionType.CR || transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
                contentValues.put(KEY_TRANSACTIONS_CR_ACCOUNT_ID, transaction.getCreditAccountId());
            }
            sqLiteDatabase.insert(TABLE_TRANSACTIONS, null, contentValues);
            contentValues.clear();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if(isCreating && currentDB != null){
            return currentDB;
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if(isCreating && currentDB != null){
            return currentDB;
        }
        return super.getReadableDatabase();
    }
}
