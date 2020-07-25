package com.himanshu.buckcounter.business;

public class Constants {
    // database constants
    static final int DB_VERSION = 1;
    static final String DB_NAME = "BUCK_COUNTER_DB";
    static final String TABLE_ACCOUNTS = "ACCOUNTS";
    static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    static final String KEY_ACCOUNTS_ID = "_id";
    static final String KEY_ACCOUNTS_NAME = "NAME";
    static final String KEY_ACCOUNTS_BALANCE = "BALANCE";
    static final String KEY_TRANSACTIONS_ID = "_id";
    static final String KEY_TRANSACTIONS_TYPE = "TYPE";
    static final String KEY_TRANSACTIONS_PARTICULARS = "PARTICULARS";
    static final String KEY_TRANSACTIONS_AMOUNT = "AMOUNT";
    static final String KEY_TRANSACTIONS_TIMESTAMP = "TIMESTAMP";
    static final String KEY_TRANSACTIONS_DR_ACCOUNT_ID = "DR_ACCOUNT_ID";
    static final String KEY_TRANSACTIONS_CR_ACCOUNT_ID = "CR_ACCOUNT_ID";
    static final String NEW = "NEW.";
    static final String OLD = "OLD.";
    static final String TRIGGER_TRANSACTIONS_INSERT = "TRIGGER_TRANSACTIONS_INSERT";
    static final String TRIGGER_TRANSACTIONS_DELETE = "TRIGGER_TRANSACTIONS_DELETE";
    static final String TRIGGER_TRANSACTIONS_UPDATE_AMOUNT = "TRIGGER_TRANSACTIONS_UPDATE_AMOUNT";
}
