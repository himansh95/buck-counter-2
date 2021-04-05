package com.himanshu.buckcounter.beans;

import java.util.List;

public class Backup {
    private List<Account> accountList;
    private List<Transaction> transactionList;

    public Backup(List<Account> accountList, List<Transaction> transactionList) {
        this.accountList = accountList;
        this.transactionList = transactionList;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }
}
