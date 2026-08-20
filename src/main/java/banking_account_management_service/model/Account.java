package banking_account_management_service.model;
import lombok.Data;

@Data
public class Account {
    private String accountHolderName;
    private String customerId;
    private String accountNumber;
    private String accountType;
    private String accountCurrency;
}