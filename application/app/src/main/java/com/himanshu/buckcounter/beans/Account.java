package com.himanshu.buckcounter.beans;

public class Account {
    private String name;
    private double balance;
    private boolean isCreditCard;
    private double creditLimit;

    public Account() {}

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Account(String name) {
        this.name = name;
        this.balance = 0;
    }

    public Account(String name, boolean isCreditCard, double creditLimit) {
        this.name = name;
        this.isCreditCard = isCreditCard;
        this.creditLimit = creditLimit;
    }

    public Account(String name, double balance, boolean isCreditCard, double creditLimit) {
        this.name = name;
        this.balance = balance;
        this.isCreditCard = isCreditCard;
        this.creditLimit = creditLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isCreditCard() {
        return isCreditCard;
    }

    public void setCreditCard(boolean creditCard) {
        isCreditCard = creditCard;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
