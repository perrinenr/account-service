package banking_account_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PdfResponse {

    private String returnCode;
    private String returnDescription;
    private String fileData;
}