package banking_account_management_service.controller;

import banking_account_management_service.dto.PdfResponse;
import banking_account_management_service.service.PdfService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TransactionPdfController {

    private final PdfService pdfService;

    public TransactionPdfController(
            PdfService pdfService
    ) {

        this.pdfService =
                pdfService;
    }

    @GetMapping("/transactions/pdf")
    public ResponseEntity<PdfResponse>
    generateTransactionsPdf() {

        String base64 =
                pdfService.generatePdfBase64();

        PdfResponse response =
                new PdfResponse(
                        "0x0000",
                        "SUCCESS",
                        base64
                );

        return ResponseEntity.ok(
                response
        );
    }
    @GetMapping(
            value = "/transactions/pdf/view",
            produces = "application/pdf"
    )
    public ResponseEntity<byte[]> viewPdf() {

        byte[] pdf =
                pdfService.generatePdfBytes();

        return ResponseEntity
                .ok()
                .header(
                        "Content-Disposition",
                        "inline; filename=transactions.pdf"
                )
                .body(pdf);
    }
}