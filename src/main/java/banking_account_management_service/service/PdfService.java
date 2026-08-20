package banking_account_management_service.service;

import banking_account_management_service.model.Transaction;
import banking_account_management_service.model.TransactionData;

import org.openpdf.text.*;
import org.openpdf.text.pdf.*;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Service
public class PdfService {

    private final TransactionService transactionService;

    public PdfService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // =========================================================
    // PDF -> BYTE[]
    // =========================================================

    public byte[] generatePdfBytes() {

        String base64 = generatePdfBase64();

        return Base64
                .getDecoder()
                .decode(base64);
    }

    // =========================================================
    // GENERATE PDF
    // =========================================================

    public String generatePdfBase64() {

        try {

            TransactionData data =
                    transactionService.readTransactions();

            List<Transaction> transactions =
                    data.getTransactions();

            int totalPages =
                    calculateTotalPages(
                            transactions.size()
                    );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            50,
                            50,
                            60,
                            130
                    );

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            outputStream
                    );

            writer.setPageEvent(
                    new FooterEvent(totalPages)
            );

            document.open();

            int currentTransaction = 0;
            int currentPage = 1;

            // =================================================
            // PAGE 1
            // =================================================

            addHeader(
                    document,
                    data
            );

            int firstPageLimit =
                    Math.min(
                            5,
                            transactions.size()
                    );

            List<Transaction> firstPageTransactions =
                    transactions.subList(
                            0,
                            firstPageLimit
                    );

            addTransactionTable(
                    document,
                    firstPageTransactions
            );

            currentTransaction =
                    firstPageLimit;

            // =================================================
            // OTHER PAGES
            // =================================================

            while (
                    currentTransaction
                            < transactions.size()
            ) {

                document.newPage();

                currentPage++;

                addSmallHeader(
                        document,
                        data,
                        currentPage,
                        totalPages
                );

                int end =
                        Math.min(
                                currentTransaction + 8,
                                transactions.size()
                        );

                List<Transaction> pageTransactions =
                        transactions.subList(
                                currentTransaction,
                                end
                        );

                addTransactionTable(
                        document,
                        pageTransactions
                );

                currentTransaction =
                        end;
            }

            document.close();

            return Base64
                    .getEncoder()
                    .encodeToString(
                            outputStream.toByteArray()
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generating PDF",
                    e
            );
        }
    }

    // =========================================================
    // PAGE NUMBER CALCULATION
    // =========================================================

    private int calculateTotalPages(
            int totalTransactions
    ) {

        if (totalTransactions <= 5) {
            return 1;
        }

        int remaining =
                totalTransactions - 5;

        int otherPages =
                (int) Math.ceil(
                        remaining / 8.0
                );

        return 1 + otherPages;
    }

    // =========================================================
    // FIRST PAGE HEADER
    // =========================================================

    private void addHeader(
            Document document,
            TransactionData data
    ) throws Exception {

        // =====================================================
        // LOGO
        // =====================================================

        InputStream logoStream =
                getClass()
                        .getResourceAsStream(
                                "/logo.png"
                        );

        if (logoStream != null) {

            byte[] logoBytes =
                    logoStream.readAllBytes();

            Image logo =
                    Image.getInstance(
                            logoBytes
                    );

            logo.scaleToFit(
                    110,
                    110
            );

            logo.setAlignment(
                    Element.ALIGN_LEFT
            );

            document.add(logo);
        }

        // =====================================================
        // TITLE
        // =====================================================

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Paragraph title =
                new Paragraph(
                        "Statement of Account",
                        titleFont
                );

        title.setAlignment(
                Element.ALIGN_RIGHT
        );

        title.setSpacingAfter(20);

        document.add(title);

        // =====================================================
        // ACCOUNT INFO
        // =====================================================

        Font infoFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10
                );

        Font infoBoldFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10
                );

        PdfPTable infoTable =
                new PdfPTable(2);

        infoTable.setWidthPercentage(75);

        infoTable.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        infoTable.setWidths(
                new float[]{
                        1.5f,
                        2.5f
                }
        );

        addInfoRow(
                infoTable,
                "Account Holder Name",
                data.getAccount()
                        .getAccountHolderName(),
                infoBoldFont,
                infoFont
        );

        addInfoRow(
                infoTable,
                "Customer ID",
                data.getAccount()
                        .getCustomerId(),
                infoBoldFont,
                infoFont
        );

        addInfoRow(
                infoTable,
                "Account Number",
                data.getAccount()
                        .getAccountNumber(),
                infoBoldFont,
                infoFont
        );

        addInfoRow(
                infoTable,
                "Account Type",
                data.getAccount()
                        .getAccountType(),
                infoBoldFont,
                infoFont
        );

        addInfoRow(
                infoTable,
                "Account Currency",
                data.getAccount()
                        .getAccountCurrency(),
                infoBoldFont,
                infoFont
        );

        /*
         * Distance entre les infos du compte
         * et le tableau.
         */
        infoTable.setSpacingAfter(20);

        document.add(infoTable);
    }

    // =========================================================
    // OTHER PAGE HEADER
    // =========================================================

    private void addSmallHeader(
            Document document,
            TransactionData data,
            int currentPage,
            int totalPages
    ) {

        Font headerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12
                );

        Paragraph paragraph =
                new Paragraph(
                        data.getAccount()
                                .getAccountHolderName()
                                + " Transactions",
                        headerFont
                );

        paragraph.setSpacingAfter(10);

        document.add(paragraph);
    }

    // =========================================================
    // ACCOUNT INFO ROW
    // =========================================================

    private void addInfoRow(
            PdfPTable table,
            String label,
            String value,
            Font labelFont,
            Font valueFont
    ) {

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(
                                label + ":",
                                labelFont
                        )
                );

        labelCell.setBorder(
                Rectangle.NO_BORDER
        );

        labelCell.setPaddingTop(1);
        labelCell.setPaddingBottom(1);
        labelCell.setPaddingRight(1);

        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                value,
                                valueFont
                        )
                );

        valueCell.setBorder(
                Rectangle.NO_BORDER
        );

        valueCell.setPaddingTop(1);
        valueCell.setPaddingBottom(1);
        valueCell.setPaddingLeft(1);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // =========================================================
    // TRANSACTION TABLE
    // =========================================================

    private void addTransactionTable(
            Document document,
            List<Transaction> transactions
    ) throws Exception {

        PdfPTable table =
                new PdfPTable(5);

        table.setWidthPercentage(100);

        table.setWidths(
                new float[]{
                        1.7f,
                        2.3f,
                        1.4f,
                        1.1f,
                        1.2f
                }
        );

        table.setSpacingBefore(0);
        table.setSpacingAfter(0);

        // =====================================================
        // HEADER PHOTO
        // =====================================================

        Image headerImage =
                loadAndCropHeaderImage();

        if (headerImage != null) {

            /*
             * Important:
             * on NE FAIT PAS scaleAbsolute().
             *
             * PdfPCell(image, true)
             * va adapter automatiquement
             * l'image à la largeur du tableau.
             */
            PdfPCell headerCell =
                    new PdfPCell(
                            headerImage,
                            true
                    );

            // L'image prend les 5 colonnes
            headerCell.setColspan(5);

            // Aucun espace
            headerCell.setPadding(0);

            // Pas de bordure
            headerCell.setBorder(
                    Rectangle.NO_BORDER
            );

            table.addCell(
                    headerCell
            );
        }

        // =====================================================
        // TRANSACTIONS
        // =====================================================

        for (Transaction transaction : transactions) {

            addNormalCell(
                    table,
                    transaction.getTransactionDate()
            );

            addNormalCell(
                    table,
                    transaction.getDescription()
            );

            addNormalCell(
                    table,
                    transaction.getValueDate()
            );

            addNormalCell(
                    table,
                    String.format(
                            "%,.2f",
                            transaction.getAmount()
                    )
            );

            addNormalCell(
                    table,
                    String.format(
                            "%,.2f",
                            transaction.getBalance()
                    )
            );
        }

        document.add(table);
    }

    // =========================================================
    // CROP TABLE HEADER IMAGE
    // =========================================================

    private Image loadAndCropHeaderImage()
            throws Exception {

        InputStream inputStream =
                getClass()
                        .getResourceAsStream(
                                "/table-header.png"
                        );

        if (inputStream == null) {
            return null;
        }

        BufferedImage original =
                ImageIO.read(inputStream);

        if (original == null) {
            return null;
        }

        int width =
                original.getWidth();

        int height =
                original.getHeight();

        int minX = width;
        int minY = height;

        int maxX = -1;
        int maxY = -1;

        /*
         * Cherche la vraie zone colorée
         * et ignore le blanc/transparence.
         */
        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int argb =
                        original.getRGB(
                                x,
                                y
                        );

                int alpha =
                        (argb >> 24)
                                & 0xFF;

                int red =
                        (argb >> 16)
                                & 0xFF;

                int green =
                        (argb >> 8)
                                & 0xFF;

                int blue =
                        argb
                                & 0xFF;

                boolean transparent =
                        alpha < 20;

                boolean white =
                        red > 245
                                && green > 245
                                && blue > 245;

                /*
                 * On garde uniquement
                 * la vraie partie du header.
                 */
                if (!transparent && !white) {

                    minX =
                            Math.min(
                                    minX,
                                    x
                            );

                    minY =
                            Math.min(
                                    minY,
                                    y
                            );

                    maxX =
                            Math.max(
                                    maxX,
                                    x
                            );

                    maxY =
                            Math.max(
                                    maxY,
                                    y
                            );
                }
            }
        }

        if (
                maxX < minX
                        || maxY < minY
        ) {

            return null;
        }

        /*
         * Petite marge seulement.
         */
        int padding = 1;

        minX =
                Math.max(
                        0,
                        minX - padding
                );

        minY =
                Math.max(
                        0,
                        minY - padding
                );

        maxX =
                Math.min(
                        width - 1,
                        maxX + padding
                );

        maxY =
                Math.min(
                        height - 1,
                        maxY + padding
                );

        int croppedWidth =
                maxX - minX + 1;

        int croppedHeight =
                maxY - minY + 1;

        BufferedImage cropped =
                original.getSubimage(
                        minX,
                        minY,
                        croppedWidth,
                        croppedHeight
                );

        ByteArrayOutputStream croppedOutput =
                new ByteArrayOutputStream();

        ImageIO.write(
                cropped,
                "png",
                croppedOutput
        );

        return Image.getInstance(
                croppedOutput.toByteArray()
        );
    }

    // =========================================================
    // NORMAL TRANSACTION CELL
    // =========================================================

    private void addNormalCell(
            PdfPTable table,
            String text
    ) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        9
                );

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text,
                                font
                        )
                );
        cell.setBorder(Rectangle.NO_BORDER);

        cell.setPaddingTop(8);
        cell.setPaddingBottom(8);

        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        table.addCell(cell);
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private static class FooterEvent
            extends PdfPageEventHelper {

        private final int totalPages;

        public FooterEvent(
                int totalPages
        ) {

            this.totalPages =
                    totalPages;
        }

        @Override
        public void onEndPage(
                PdfWriter writer,
                Document document
        ) {

            PdfContentByte canvas =
                    writer.getDirectContent();

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            8
                    );

            float left =
                    document.left();

            // =================================================
            // FOOTER LINE
            // =================================================

            canvas.moveTo(
                    document.left(),
                    110
            );

            canvas.lineTo(
                    document.right(),
                    110
            );

            canvas.stroke();

            // =================================================
            // FOOTER TEXT 1
            // =================================================

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Phrase(
                            "- This statement of account is digitally signed by SGBL",
                            boldFont
                    ),
                    left,
                    90,
                    0
            );

            // =================================================
            // FOOTER TEXT 2
            // =================================================

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_LEFT,
                    new Phrase(
                            "- Please inform us of any objection within 30 days following the date of this statement",
                            normalFont
                    ),
                    left,
                    75,
                    0
            );

            // =================================================
            // COMPANY
            // =================================================

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_CENTER,
                    new Phrase(
                            "LEAP by SGBL | "
                                    + "SOCIETE GENERALE DE BANQUE AU LIBAN SAL | "
                                    + "Digital Banking Service | "
                                    + "BDL License 8/19/26 | "
                                    + "Tel 1674 | "
                                    + "www.leap.com.lb",
                            normalFont
                    ),
                    PageSize.A4.getWidth() / 2,
                    45,
                    0
            );

            // =================================================
            // PAGE NUMBER
            // =================================================

            String pageNumber =
                    "Page "
                            + writer.getPageNumber()
                            + " / "
                            + totalPages;

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    new Phrase(
                            pageNumber,
                            normalFont
                    ),
                    document.right(),
                    30,
                    0
            );
        }
    }
}