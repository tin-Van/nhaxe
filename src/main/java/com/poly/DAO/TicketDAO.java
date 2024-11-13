package com.poly.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Account;
import com.poly.entity.Ticket;

public interface TicketDAO extends JpaRepository<Ticket, Integer>{
	
	@Query("SELECT COUNT(b) FROM Ticket b")
    Long countTotalTickets();
	
	@Query("SELECT SUM(r.price) FROM Ticket r")
    Long countTotalSum();
	
	@Query("SELECT COUNT(r.customer) FROM Ticket r")
    Long countTotalcustomer();
	
	@Query("SELECT t.route, COUNT(t) as count FROM Ticket t GROUP BY t.route HAVING COUNT(t) >= 2 ORDER BY count DESC")
	List<Object[]> findMostBookedRoutes();
	   

	List<Ticket> findByUsername(Account username);

	List<Ticket> findByUsername(String username);
	List<Ticket> findTop4ByOrderByBookingDateDesc();
}
