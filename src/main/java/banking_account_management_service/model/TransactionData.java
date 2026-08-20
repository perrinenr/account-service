package banking_account_management_service.model;
import java.util.List;
import lombok.Data;

@Data
public class TransactionData {
    private Account account;
    private Date date;
    private List<Transaction> transactions;
}