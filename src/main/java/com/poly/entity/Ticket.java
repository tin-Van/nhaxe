package com.poly.entity;

import java.util.*;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
    
    @Column(name = "username",columnDefinition = "nvarchar(255)")
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
     
    @Column(name = "ticket_Code",columnDefinition = "nvarchar(255)")
    private String ticketCode;
    
    @Column(name = "booking_date")
    private LocalDateTime  bookingDate;

    @Column(name = "customer",columnDefinition = "nvarchar(255)")
    private String customer;
    
    @Column(name = "phonenumber",columnDefinition = "nvarchar(255)")
    private String phonenumber;
    
    @Column(name = "price")
    private double price;
    
    @Column(name = "payment_status")
    private String paymentstatus;

    public class TicketCodeGenerator {
        // Tạo bộ ký tự bao gồm chữ cái và số
        private static final String CHARACTERS = "0123456789";
        private static final int CODE_LENGTH = 8;

        // Phương thức để sinh mã đơn vé
        public static String generateTicketCode() {
            SecureRandom random = new SecureRandom();
            StringBuilder ticketCode = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                int index = random.nextInt(CHARACTERS.length());
                ticketCode.append(CHARACTERS.charAt(index));
            }

            return ticketCode.toString();
        }
    }
    public String getFormattedBookingDate() {
        if (bookingDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
            return bookingDate.format(formatter);
        }
        return "N/A"; // Hoặc bạn có thể chọn trả về một thông điệp khác
    }


}


