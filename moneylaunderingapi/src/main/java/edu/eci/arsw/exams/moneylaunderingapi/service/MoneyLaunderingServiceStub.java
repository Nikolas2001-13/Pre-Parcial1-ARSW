package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
    private List<SuspectAccount> suspectAccounts = new CopyOnWriteArrayList<>();

    public MoneyLaunderingServiceStub() {
        suspectAccounts.add(new SuspectAccount("1234", 500));
        suspectAccounts.add(new SuspectAccount("5678", 200));
        suspectAccounts.add(new SuspectAccount("9101", 1000));
    }

    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) {
        this.getAccountStatus(suspectAccount.getAccountId()).setAmountOfSmallTransactions(suspectAccount.getAmountOfSmallTransactions());
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) {
        for (SuspectAccount suspectAccount:suspectAccounts) {
            if (suspectAccount.getAccountId().equals(accountId)) {
                return suspectAccount;
            }
        }

        return null;
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        return suspectAccounts;
    }

    @Override
    public synchronized void addSuspectAccounts(SuspectAccount account) {
        for (SuspectAccount suspectAccount:suspectAccounts) {
            if (suspectAccount.getAccountId().equals(account.getAccountId())) {
                System.out.println("This Account Already Exist");
            }
        }

        suspectAccounts.add(account);
    }
}
