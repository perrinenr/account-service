package banking_account_management_service.service;

import banking_account_management_service.model.TransactionData;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
@Service
public class TransactionService {

    private final ObjectMapper objectMapper;

    public TransactionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TransactionData readTransactions() {

        try {

            InputStream inputStream =
                    getClass().getResourceAsStream("/transactions.json");

            if (inputStream == null) {
                throw new RuntimeException(
                        "transactions.json file not found"
                );
            }

            return objectMapper.readValue(
                    inputStream,
                    TransactionData.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error reading transactions.json",
                    e
            );
        }
    }
}