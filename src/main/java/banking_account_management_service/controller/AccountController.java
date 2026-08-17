package banking_account_management_service.controller;

import banking_account_management_service.dto.BalanceUpdateRequest;
import banking_account_management_service.entity.Account;
import banking_account_management_service.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    public final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/api/v1/accounts/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable("accountNumber") String accountNumber){
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        //return ResponseEntity.ok(new Account("gfgfgf","ghghg","7.8","hjhj","hjhj"));
        return ResponseEntity.ok(account);
    }
    @PutMapping("/api/v1/accounts/{accountNumber}/balance")
    public ResponseEntity<Account> updateBalance(
            @PathVariable String accountNumber,
            @RequestBody BalanceUpdateRequest request) {//transfrom json in java

        Account updatedAccount = accountService.updateBalance(
                accountNumber,
                request.getAmount()
        );
        return ResponseEntity.ok(updatedAccount);
    }
}
