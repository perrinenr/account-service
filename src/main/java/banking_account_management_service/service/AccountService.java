package banking_account_management_service.service;

import banking_account_management_service.entity.Account;
import banking_account_management_service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import banking_account_management_service.exception.AccountNotFoundException;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public Account updateBalance(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
}
