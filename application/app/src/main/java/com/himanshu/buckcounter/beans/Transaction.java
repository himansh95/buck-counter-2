package com.himanshu.buckcounter.beans;

import java.util.Date;

public class Transaction {
    private int id;
    private TransactionType transactionType;
    private String particulars;
    private double amount;
    private Date timestamp;
    private int debitAccountId;
    private int creditAccountId;

    public Transaction() {
    }

    public Transaction(int id, TransactionType transactionType, String particulars, double amount, Date timestamp, int accountId) {
        this.id = id;
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        if (transactionType == TransactionType.DR) {
            this.debitAccountId = accountId;
        } else if (transactionType == TransactionType.CR) {
            this.creditAccountId = accountId;
        } else if (transactionType == TransactionType.CONTRA) {
            throw new IllegalArgumentException("Use the constructor Transaction(int, TransactionType, String, double, Date, int, int) for TransactionType CONTRA");
        }
    }

    public Transaction(TransactionType transactionType, String particulars, double amount, Date timestamp, int accountId) {
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        if (transactionType == TransactionType.DR) {
            this.debitAccountId = accountId;
        } else if (transactionType == TransactionType.CR) {
            this.creditAccountId = accountId;
        }  else if (transactionType == TransactionType.CONTRA) {
            throw new IllegalArgumentException("Use the constructor Transaction(TransactionType, String, double, Date, int, int) for TransactionType CONTRA");
        }
    }

    public Transaction(TransactionType transactionType, String particulars, double amount, Date timestamp, int debitAccountId, int creditAccountId) {
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
    }

    public Transaction(int id, TransactionType transactionType, String particulars, double amount, Date timestamp, int debitAccountId, int creditAccountId) {
        this.id = id;
        this.transactionType = transactionType;
        this.particulars = particulars;
        this.amount = amount;
        this.timestamp = timestamp;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", particulars='" + particulars + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", debitAccountId=" + debitAccountId +
                ", creditAccountId=" + creditAccountId +
                '}';
    }

    public int getDebitAccountId() {
        return debitAccountId;
    }

    public void setDebitAccountId(int debitAccountId) {
        this.debitAccountId = debitAccountId;
    }

    public int getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(int creditAccountId) {
        this.creditAccountId = creditAccountId;
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
