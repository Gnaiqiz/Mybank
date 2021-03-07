package com.example.mybank;

import java.io.Serializable;

public class UserAccount implements Serializable {

    private String id;
    private String accountName;
    private String amount;
    private String iban;
    private String currency;

    public UserAccount(String id, String accountName, String amount, String iban, String currency) {
        this.id = id;
        this.accountName = accountName;
        this.amount = amount;
        this.iban = iban;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAmount() {
        return amount;
    }

    public String getIban() {
        return iban;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "id=" + id  +
                ", accountName=" + accountName +
                ", amount=" + amount +
                ", iban=" + iban +
                ", currency=" + currency ;
    }
}