package banking_account_management_service.controller;

import banking_account_management_service.service.HtmlPdfService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HtmlPdfController {

    private final HtmlPdfService htmlPdfService;

    public HtmlPdfController(
            HtmlPdfService htmlPdfService
    ) {
        this.htmlPdfService = htmlPdfService;
    }

    @GetMapping(
            value = "/transactions/pdf/html",
            produces = "application/pdf"
    )
    public ResponseEntity<byte[]> viewHtmlPdf() {

        byte[] pdf =
                htmlPdfService.generatePdfBytes();

        return ResponseEntity
                .ok()
                .header(
                        "Content-Disposition",
                        "inline; filename=statement-html.pdf"
                )
                .body(pdf);
    }
}