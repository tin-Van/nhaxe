package com.poly.DAO;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.entity.Location;
import com.poly.entity.Route;

public interface RouteDAO extends JpaRepository<Route, Integer>{

//	Page<Route> findByDepartureAndDestination(String departure, String destination, Pageable pageable);

	Page<Route> findByDepartureNameAndDestinationName(String departureName, String destinationName, Pageable pageable);

	
	Page<Route> findByDepartureNameAndDestinationNameAndDepartureDate(String
	  departureName, String destinationName, Date day, Pageable pageable);
	 
	
	@Query("SELECT COUNT(b) FROM Route b")
    Long countTotalRoute();


	List<Route> findByDepartureNameAndDestinationNameAndDepartureDate(String departure_name, String destination_name,
			Date daycheck);



}
