package banking_account_management_service.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
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
}
