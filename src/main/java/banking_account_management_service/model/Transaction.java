package banking_account_management_service.model;
import lombok.Data;

@Data
public class Transaction {
    private String id;
    private String transactionDate;
    private String description;
    private String valueDate;
    private Double amount;
    private Double balance;
}