package edu.eci.arsw.exams.moneylaunderingapi.model;

import java.util.concurrent.atomic.AtomicInteger;

public class SuspectAccount {
    public String accountId;
    public int amountOfSmallTransactions;

    public suspectAccount(String accountId, int amountOfSmallTransactions){
        this.accountId = accountId;
        this.amountOfSmallTransactions = new AtomicInteger(amountOfSmallTransactions);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAmountOfSmallTransactions() {
        return amountOfSmallTransactions.get();
    }

    public void setAmountOfSmallTransactions(int amountOfSmallTransactions) {
        this.amountOfSmallTransactions.getAndAdd(amountOfSmallTransactions);
    }
}
