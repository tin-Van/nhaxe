package com.poly.entity;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

import ch.qos.logback.core.util.Duration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name",columnDefinition = "nvarchar(255)")
    private String name; //Tên Tuyến Đường

    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Location departure; // Địa điểm khởi hành

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Location destination; // Địa điểm đến 

    @Column(name = "time")
    private LocalTime time; //Thời gian khởi hành
    
    @Column(name = "departuredate")
    private Date departureDate; //Mgày khởi hành
    
    @Column(name = "is_active")
    private Boolean isActive; // Trạng thái hoạt động

    @Column(name = "distance")
    private double distance; // Khoảng cách (ví dụ: số km)

    @Column(name = "price")
    private Double price; // Số tiền 
    
    @Column(name = "estimated_duration")
    private LocalTime estimatedDuration; // Thời gian dự kiến 

    @ManyToMany
    @Column(name = "seats")
    private List<Seat> seats; // Số lượng ghế trống

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

   
}
