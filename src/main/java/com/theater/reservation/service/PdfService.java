
package com.theater.reservation.service;

import com.theater.reservation.model.Reservation;
import com.theater.reservation.model.Seat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateInvoice(Reservation reservation) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Title Section
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.newLineAtOffset(220, 770);
            contentStream.showText("INVOICE");
            contentStream.endText();

            // Subtitle line
            contentStream.setStrokingColor(0, 0, 0); // Black
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50, 755);
            contentStream.lineTo(545, 755);
            contentStream.stroke();

            // Body content
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.setLeading(16f);
            contentStream.newLineAtOffset(50, 730);

            contentStream.showText("Reservation Code: " + reservation.getReservationCode());
            contentStream.newLine();
            contentStream.showText("Reservation Date: " + reservation.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            contentStream.newLine();
            contentStream.showText("Show Date & Time: " + reservation.getShow().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            contentStream.newLine();
            contentStream.showText("Customer Name: " + reservation.getUser().getFullName());
            contentStream.newLine();
            contentStream.showText("Customer Email: " + reservation.getUser().getEmail());
            contentStream.newLine();
            contentStream.newLine();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.showText("Reserved Seats:");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLine();

            for (Seat seat : reservation.getSeats()) {
                contentStream.showText("- Row " + seat.getRowNumber() + ", Seat " + seat.getSeatNumber());
                contentStream.newLine();
            }

            contentStream.newLine();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText("Total Price: €" + reservation.getTotalPrice());
            contentStream.newLine();
            contentStream.newLine();

            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            contentStream.showText("Thank you for choosing our theater. Enjoy the show!");
            contentStream.endText();

            contentStream.close();
            document.save(outputStream);

            reservation.setInvoiceGenerated(true);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}