package com.himanshu.buckcounter.beans;

import java.util.Date;

public class Transaction {
    private int id;
    private TransactionType transactionType;
    private String particulars;
    private double amount;
    private Date timestamp;
    private String debitAccount;
    private String creditAccount;
    private double accountBalance;

    public Transaction() {
    }

    public Transaction(int id, TransactionType transactionType, String particulars, double amount, Date timestamp, String account) {
        this.id = id;
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        if (transactionType == TransactionType.DR) {
            this.debitAccount = account;
        } else if (transactionType == TransactionType.CR) {
            this.creditAccount = account;
        } else if (transactionType == TransactionType.CONTRA) {
            throw new IllegalArgumentException("Use the constructor Transaction(int, TransactionType, String, double, Date, int, int) for TransactionType CONTRA");
        }
    }

    public Transaction(TransactionType transactionType, String particulars, double amount, Date timestamp, String account) {
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        if (transactionType == TransactionType.DR) {
            this.debitAccount = account;
        } else if (transactionType == TransactionType.CR) {
            this.creditAccount = account;
        }  else if (transactionType == TransactionType.CONTRA) {
            throw new IllegalArgumentException("Use the constructor Transaction(TransactionType, String, double, Date, int, int) for TransactionType CONTRA");
        }
    }

    public Transaction(TransactionType transactionType, String particulars, double amount, Date timestamp, String debitAccount, String creditAccount) {
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
    }

    public Transaction(int id, TransactionType transactionType, String particulars, double amount, Date timestamp, String debitAccount, String creditAccount) {
        this.id = id;
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        this.creditAccount = creditAccount;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", particulars='" + particulars + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", debitAccount=" + debitAccount +
                ", creditAccount=" + creditAccount +
                '}';
    }

    public enum TransactionType {
        CR("credit"),
        DR("debit"),
        CONTRA("contra");

        private final String name;
        TransactionType(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }

        public static TransactionType getTransactionType(String name){
            switch (name){
                case "credit":
                    return CR;
                case "debit":
                    return DR;
                case "contra":
                    return CONTRA;
                default:
                    return null;
            }
        }
    }
}
