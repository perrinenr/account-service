package banking_account_management_service.service;

import banking_account_management_service.model.Transaction;
import banking_account_management_service.model.TransactionData;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class HtmlPdfService {

    private final TransactionService transactionService;

    public HtmlPdfService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public byte[] generatePdfBytes() {

        try {

            // 1. Read JSON
            TransactionData data =
                    transactionService.readTransactions();

            // 2. Read HTML template
            InputStream htmlStream =
                    getClass().getResourceAsStream(
                            "/templates/statement.html"
                    );

            if (htmlStream == null) {
                throw new RuntimeException(
                        "statement.html not found"
                );
            }

            String html =
                    new String(
                            htmlStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            // 3. Replace account information
            html = html.replace(
                    "{{ACCOUNT_HOLDER_NAME}}",
                    data.getAccount().getAccountHolderName()
            );

            html = html.replace(
                    "{{CUSTOMER_ID}}",
                    data.getAccount().getCustomerId()
            );

            html = html.replace(
                    "{{ACCOUNT_NUMBER}}",
                    data.getAccount().getAccountNumber()
            );

            html = html.replace(
                    "{{ACCOUNT_TYPE}}",
                    data.getAccount().getAccountType()
            );

            html = html.replace(
                    "{{ACCOUNT_CURRENCY}}",
                    data.getAccount().getAccountCurrency()
            );
            html = html.replace(
                    "{{DATE_REQUEST}}",
                    data.getDate().getRequestDate()
            );
            html = html.replace(
                    "{{DATE_FROM}}",
                    data.getDate().getFromDate()
            );
            html = html.replace(
                    "{{DATE_TO}}",
                    data.getDate().getToDate()
            );
            html = html.replace(
                    "{{DATE_PAGES}}",
                    data.getDate().getPages()
            );


            // 4. Replace logo
            String logoUrl =
                    getClass()
                            .getResource("/logo.png")
                            .toExternalForm();

            html = html.replace(
                    "{{LOGO}}",
                    logoUrl
            );

            // 5. Build transaction tables
            String transactionTables =
                    buildTransactionTables(
                            data.getTransactions()
                    );

            html = html.replace(
                    "{{TRANSACTION_TABLES}}",
                    transactionTables
            );

            // 6. Replace transaction table header image
            String tableHeaderUrl =
                    getClass()
                            .getResource("/table-header.png")
                            .toExternalForm();

            html = html.replace(
                    "{{TABLE_HEADER}}",
                    tableHeaderUrl
            );

            // 7. Generate PDF
            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            PdfRendererBuilder builder =
                    new PdfRendererBuilder();

            builder.withHtmlContent(
                    html,
                    null
            );

            builder.toStream(
                    outputStream
            );

            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generating HTML PDF",
                    e
            );
        }
    }

    public String generatePdfBase64() {

        byte[] pdf =
                generatePdfBytes();

        return Base64
                .getEncoder()
                .encodeToString(pdf);
    }

    // =========================================================
    // BUILD TRANSACTION TABLES
    // =========================================================

    private String buildTransactionTables(
            List<Transaction> transactions
    ) {

        StringBuilder html =
                new StringBuilder();

        int index = 0;
        int page = 1;

        while (index < transactions.size()) {

            int pageSize;

            // First page = 5 transactions
            if (page == 1) {
                pageSize = 5;
            } else {
                // Other pages = 8 transactions
                pageSize = 8;
            }

            int end =
                    Math.min(
                            index + pageSize,
                            transactions.size()
                    );

            // Start table
            html.append(
                    """
                    <table class="transactions">

                        <thead>
                            <tr>
                                <th colspan="5" class="header-image-cell">
                                    <img
                                        src="{{TABLE_HEADER}}"
                                        class="table-header-image"
                                    />
                                </th>
                            </tr>
                        </thead>

                        <tbody>
                    """
            );

            // Add transactions
            for (int i = index; i < end; i++) {

                Transaction transaction =
                        transactions.get(i);

                html.append("<tr>");

                // Transaction Date
                html.append("<td>")
                        .append(
                                transaction.getTransactionDate()
                        )
                        .append("</td>");

                // Description
                html.append("<td>")
                        .append(
                                transaction.getDescription()
                        )
                        .append("</td>");

                // Value Date
                html.append("<td>")
                        .append(
                                transaction.getValueDate()
                        )
                        .append("</td>");

                // Amount
                html.append("<td>")
                        .append(
                                String.format(
                                        "%,.2f",
                                        transaction.getAmount()
                                )
                        )
                        .append("</td>");

                // Balance
                html.append("<td>")
                        .append(
                                String.format(
                                        "%,.2f",
                                        transaction.getBalance()
                                )
                        )
                        .append("</td>");

                html.append("</tr>");
            }

            // Close table
            html.append(
                    """
                        </tbody>
                    </table>
                    """
            );

            index = end;

            // Add page break if more transactions exist
            if (index < transactions.size()) {

                html.append(
                        """
                        <div class="page-break"></div>
                        """
                );
            }

            page++;
        }

        return html.toString();
    }
}