
package com.theater.reservation.service;

import com.theater.reservation.model.Reservation;
import com.theater.reservation.model.Seat;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateInvoice(Reservation reservation) {
        // In a real application, you would use a PDF library like iText or PDFBox
        // For simplicity, let's just simulate PDF generation
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // This is just placeholder content - in a real app you'd generate actual PDF content
            String invoiceContent = "INVOICE\n\n" +
                    "Reservation Code: " + reservation.getReservationCode() + "\n" +
                    "Date: " + reservation.getReservationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n\n" +
                    "Show: " + reservation.getShow().getTitle() + "\n" +
                    "Date & Time: " + reservation.getShow().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n\n" +
                    "Customer: " + reservation.getUser().getFullName() + "\n" +
                    "Email: " + reservation.getUser().getEmail() + "\n\n" +
                    "Seats:\n";

            for (Seat seat : reservation.getSeats()) {
                invoiceContent += "- Row " + seat.getRowNumber() + ", Seat " + seat.getSeatNumber() + "\n";
            }

            invoiceContent += "\nTotal Price: €" + reservation.getTotalPrice() + "\n\n" +
                    "Thank you for your purchase!";

            // In a real application, convert this content to a PDF
            outputStream.write(invoiceContent.getBytes());

            // Mark the invoice as generated
            reservation.setInvoiceGenerated(true);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}