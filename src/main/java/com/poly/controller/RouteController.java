package com.poly.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.poly.DAO.BusDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Seat;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin")
public class RouteController {
	
	@Autowired 
	RouteDAO routeDAO;
	
	@Autowired 
	SeatDAO seatDAO;
	
	@Autowired
	BusDAO busDAO; 
	
	@Autowired
	LocationDAO locationDAO;
	@GetMapping("route")
	public String getRoute(Model model,@ModelAttribute("route")Route attributes,
			@RequestParam("page") Optional<Integer> page,@RequestParam("field") Optional<String> field) {
		
		Route route = (attributes!=null) ? attributes : new Route();
		model.addAttribute("route", route);
		
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
		List<Route> routes = routeDAO.findAll();
		model.addAttribute("routes", routes);
		
		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		
		return "/views/items/Route";
	}
	
	@PostMapping("route")
	public String save(@ModelAttribute("route") Route route) {
		System.out.print(route.getBus().getId());
		Optional<Bus> busOptional = busDAO.findById(route.getBus().getId());
		Bus bus = busOptional.get();
	    try {
	        // Lưu route trước để có ID hợp lệ
	        routeDAO.save(route);
	        for (int i = 1; i < bus.getCapacity(); i++) {
	            Seat seat = new Seat();
	            seat.setSeatNumber("Seat " + i);
	            seat.setSeatType("Standard");
	            seat.setBooked(false);
	            seat.setBus(route.getBus());
	            seat.setRoute(route);
	            seatDAO.save(seat);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return "redirect:/admin/route";
	}

	
	@GetMapping("/route/edit")
	public String editform(Model model,@ModelAttribute("route")Route attributes,
			@RequestParam("page") Optional<Integer> page,@RequestParam("field") Optional<String> field) {
		Route route = (attributes!=null) ? attributes : new Route();
		model.addAttribute("route", route);
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
		Pageable pageable = paginate(page, field);
		Page<Route> products = routeDAO.findAll(pageable);
		model.addAttribute("routes", products);
		
		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		return "/views/route_items/manager";
	}
	
	@GetMapping("/route/edit/{id}")
	public String edit(@PathVariable("id") Integer id, RedirectAttributes attributes ) {
		Route route = routeDAO.findById(id).get();
		attributes.addFlashAttribute("route", route);
		return "redirect:/admin/route/edit";
	}
	@PostMapping("route/remove")
	public String getremove(@ModelAttribute("route")Route route) {
		routeDAO.delete(route);
		return "redirect:/admin/route";
	}
	
	@PostMapping("route/reset")
	public String getMethodName() {
		return "redirect:/admin/route";
	}
	@PostMapping("route/list")
	public String findByPrice(Model model,
			   @RequestParam("departure_name")String departureName,
		        @RequestParam("destination_name")String destinationName) {
		
		Route route = new Route();
		model.addAttribute("route", route);
	
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		
		Pageable pageable = PageRequest.of(0, 3);
		Page<Route> list = routeDAO.findByDepartureNameAndDestinationName(departureName, destinationName, pageable);
		model.addAttribute("routes", list);
		
		return "/views/items/Route";
	}
	private Sort sort(@RequestParam("field") Optional<String> field) {
		String name = field.orElse("id"); 
		Direction direction = Direction.DESC;
		return Sort.by(direction, name);
	}
	
	private Pageable paginate(@RequestParam("page") Optional<Integer> page,@RequestParam("field") Optional<String> field) {
		int pageNumber = page.orElse(0);
		if (pageNumber < 0) {
			pageNumber = 0;
		}
		Sort sorting = sort(field); // Lấy thông tin sắp xếp từ tham số field
		return PageRequest.of(pageNumber, 3,sorting); // Điều chỉnh kích thước trang theo nhu cầu
	}
	
}
