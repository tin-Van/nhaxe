package com.poly.controller;

import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.poly.DAO.BusDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.service.HomeService;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class BusController {
	
//	@Autowired 
//	RouteDAO routeDAO;
	
	@Autowired
	BusDAO busDAO; 
	
	@Autowired
	HomeService service;
	
	@Autowired
	LocationDAO locationDAO;
	@GetMapping("/admin/bus")
	public String getRoute(Model model,@ModelAttribute("bus")Bus attributes) {
		
		Bus bus = (attributes!=null) ? attributes : new Bus();
		model.addAttribute("bus", bus);
		
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
//		List<Route> routes = routeDAO.findAll();
//		model.addAttribute("routes", routes);
		
		List<Bus> busList = busDAO.findAll();
		model.addAttribute("busList", busList);
		
		return "/views/items/bus";
	}
	
	@PostMapping("admin/bus")
	public String save(@ModelAttribute("bus")Bus bus,Model model,
			@RequestParam("file") MultipartFile attach) {
		if (!attach.getContentType().startsWith("image/")) {
			model.addAttribute("messageimg", "Vui lòng chọn ảnh để tiếp tục");
		} else {
			String filename = attach.getOriginalFilename();
			bus.setAvatar(filename);
//			model.addAttribute("img", filename);
			service.saveImage(attach);
			busDAO.save(bus);
		}
		
		return "redirect:/admin/bus";
	}
	
	@GetMapping("/admin/bus/edit")
	public String editform(Model model,@ModelAttribute("bus")Bus attributes ) {
		
		Bus bus = (attributes!=null) ? attributes : new Bus();
		model.addAttribute("bus", bus);
		
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
//		List<Route> routes = routeDAO.findAll();
//		model.addAttribute("routes", routes);
		
		List<Bus> busList = busDAO.findAll();
		model.addAttribute("busList", busList);
		return "/views/bus_items/manager";
	}
	
	@GetMapping("/admin/bus/edit/{id}")
	public String edit(@PathVariable("id") Integer id, RedirectAttributes attributes ) {
		Bus bus = busDAO.findById(id).get();
		attributes.addFlashAttribute("bus", bus);
		return "redirect:/admin/bus/edit";
	}
	@PostMapping("admin/bus/reset")
	public String getMethodName() {
		Bus bus = new Bus();
		return "redirect:/admin/bus/edit";
	}
	@PostMapping("admin/bus/remove")
	public String getRemove(@ModelAttribute("bus") Bus bus, Model model) {
	    try {
	        busDAO.delete(bus);
	    } catch (Exception e) {
	        // Thêm thông báo lỗi vào Model
	        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xóa bus: " + e.getMessage());
	    }
	    
	    return "redirect:/admin/bus";
	}

//	@PostMapping("bus/list")
//	public String findByPrice(Model model,
//			   @RequestParam("departure_name")String departureName,
//		        @RequestParam("destination_name")String destinationName) {
//		
//		Route route = new Route();
//		model.addAttribute("route", route);
//	
//		List<Location> locations = locationDAO.findAll();
//		model.addAttribute("locations", locations);
//		
//		List<Bus> bus = busDAO.findAll();
//		model.addAttribute("bus", bus);
//		
//		Pageable pageable = PageRequest.of(0, 3);
//		Page<Route> list = routeDAO.findByDepartureNameAndDestinationName(departureName, destinationName, pageable);
//		model.addAttribute("routes", list);
//		
//		return "Route";
//	}
//	private Sort sort(@RequestParam("field") Optional<String> field) {
//		String name = field.orElse("id"); 
//		Direction direction = Direction.DESC;
//		return Sort.by(direction, name);
//	}
//	
//	private Pageable paginate(@RequestParam("page") Optional<Integer> page,@RequestParam("field") Optional<String> field) {
//		int pageNumber = page.orElse(0);
//		if (pageNumber < 0) {
//			pageNumber = 0;
//		}
//		Sort sorting = sort(field); // Lấy thông tin sắp xếp từ tham số field
//		return PageRequest.of(pageNumber, 3,sorting); // Điều chỉnh kích thước trang theo nhu cầu
//	}
}
