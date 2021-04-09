package com.himanshu.buckcounter.business;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    // database constants
    static final int DB_VERSION = 1;
    static final String DB_NAME = "BUCK_COUNTER_DB";
    static final String TABLE_ACCOUNTS = "ACCOUNTS";
    static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    static final String KEY_ACCOUNTS_NAME = "NAME";
    static final String KEY_ACCOUNTS_BALANCE = "BALANCE";
    static final String KEY_ACCOUNTS_IS_CREDIT_CARD = "IS_CREDIT_CARD";
    static final String KEY_ACCOUNTS_CREDIT_LIMIT = "CREDIT_LIMIT";
    static final String KEY_TRANSACTIONS_ID = "_id";
    static final String KEY_TRANSACTIONS_TYPE = "TYPE";
    static final String KEY_TRANSACTIONS_PARTICULARS = "PARTICULARS";
    static final String KEY_TRANSACTIONS_AMOUNT = "AMOUNT";
    static final String KEY_TRANSACTIONS_TIMESTAMP = "TIMESTAMP";
    static final String KEY_TRANSACTIONS_DR_ACCOUNT = "DR_ACCOUNT";
    static final String KEY_TRANSACTIONS_CR_ACCOUNT = "CR_ACCOUNT";
    static final String NEW = "NEW.";
    static final String OLD = "OLD.";
    static final String TRIGGER_TRANSACTIONS_INSERT = "TRIGGER_TRANSACTIONS_INSERT";
    static final String TRIGGER_TRANSACTIONS_DELETE = "TRIGGER_TRANSACTIONS_DELETE";
    static final String TRIGGER_TRANSACTIONS_UPDATE_AMOUNT = "TRIGGER_TRANSACTIONS_UPDATE_AMOUNT";

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("₹ ##,##,##,##,###.##");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    public static final String VALID_TEXT_REGEX = "[\\w\\s\\d\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\/\\-\\+\\=\\{\\}\\[\\]\\|\\\\\\:\\;\\'\\\"\\<\\>\\,\\.\\?]+";
    public static final String VALID_AMOUNT_REGEX = "^[0-9]+(\\.[0-9]+)?$";
    public static final String VALID_NEGATIVE_AMOUNT_REGEX = "^-?[0-9]+(\\.[0-9]+)?$";
}
