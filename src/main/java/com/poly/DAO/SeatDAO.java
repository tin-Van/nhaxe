package com.poly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Bus;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import java.util.List;
import java.util.Optional;


public interface SeatDAO extends JpaRepository<Seat, Integer> {

	List<Seat> findByRoute(Route route);

	void save(List<Seat> list);
	//
//	List<Seat> findByBus(Bus bus);
//
//	List<Seat> findByBusId(Integer integer);

	List<Seat> findByRouteId(Integer integer);


	Seat findBySeatNumber(String seat);

}
