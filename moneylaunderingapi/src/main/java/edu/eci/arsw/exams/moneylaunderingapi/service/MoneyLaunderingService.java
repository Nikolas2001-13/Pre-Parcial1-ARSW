package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MoneyLaunderingService {
    void updateAccountStatus(SuspectAccount suspectAccount);
    SuspectAccount getAccountStatus(String accountId);
    List<SuspectAccount> getSuspectAccounts();
    void addSuspectAccounts(SuspectAccount account);
}