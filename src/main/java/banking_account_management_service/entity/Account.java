package banking_account_management_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table( name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accountnumber")
    private String accountNumber;
    @Column(name = "accountholdername")
    private String accountHolderName;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "currency")
    private String currency;
    @Column(name = "status")
    private String status;

    public Account() {};

    public Account(String accountNumber, String accountHolderName, String balance, String currency, String status) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = 0.0;
        this.currency = currency;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountHolderName() {
        return accountHolderName;
    }
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    public Double getBalance() {
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
