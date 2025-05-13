
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

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 750);

            contentStream.showText("INVOICE");
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Reservation Code: " + reservation.getReservationCode());
            contentStream.newLine();
            contentStream.showText("Date: " + reservation.getReservationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            contentStream.newLine();
            contentStream.showText("Show Date & Time: " + reservation.getShow().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            contentStream.newLine();
            contentStream.showText("Customer: " + reservation.getUser().getFullName());
            contentStream.newLine();
            contentStream.showText("Email: " + reservation.getUser().getEmail());
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Seats:");
            contentStream.newLine();

            for (Seat seat : reservation.getSeats()) {
                contentStream.showText("- Row " + seat.getRowNumber() + ", Seat " + seat.getSeatNumber());
                contentStream.newLine();
            }

            contentStream.newLine();
            contentStream.showText("Total Price: €" + reservation.getTotalPrice());
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("Thank you for your purchase!");

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