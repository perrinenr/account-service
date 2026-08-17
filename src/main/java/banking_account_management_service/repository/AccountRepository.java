package banking_account_management_service.repository;

import banking_account_management_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    public Optional<Account> findByAccountNumber(String accountNumber);
    public Boolean existsByAccountNumber(String accountNumber);
}
