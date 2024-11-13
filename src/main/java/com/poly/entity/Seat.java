package com.poly.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "seat_number",columnDefinition = "nvarchar(255)")
    private String seatNumber;

    @Column(name = "seat_type",columnDefinition = "nvarchar(255)")
    private String seatType;
    
    @Column(name = "is_booked")
    private boolean isBooked;
    
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}

