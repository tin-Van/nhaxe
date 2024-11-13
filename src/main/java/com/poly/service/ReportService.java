package com.poly.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Bus;
import com.poly.entity.Report;

public interface ReportService extends JpaRepository<Report, String> {
    @Query("SELECT new Report(o.route.name, sum(o.price), count(o)) "
            + " FROM Ticket o "
            + " GROUP BY o.route.name"
            + " ORDER BY sum(o.price) DESC, count(o) DESC")
    List<Report> getInventoryByCategory();
    
    


    
    
    
    
}



