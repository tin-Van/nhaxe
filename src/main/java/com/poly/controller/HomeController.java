package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.ContenDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.model.ContentInfo;
import com.poly.model.ContentMap;
import com.poly.model.Order;
import com.poly.service.service;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("home")
public class HomeController {
	@Autowired
	HttpSession session;
//	
	@Autowired
	service service;
//	
	@Autowired
	LocationDAO locationDAO;
	
	@Autowired
	ContenDAO contenDAO;
	
	@Autowired
	TicketDAO ticketDAO; 
	@GetMapping("")
	public String getMethodName(Model model,@ModelAttribute("order") Order attributes) {
		List<Location> locations = locationDAO.findAll();
		ContentMap FeaturedNewsinfo = contenDAO.findAllFeaturedNews();
		ContentMap TravelTipsinfo = contenDAO.findAllTravelTips();
		
		List<Object[]> MostBookedRoutes = ticketDAO.findMostBookedRoutes();
		model.addAttribute("MostBookedRoutes", MostBookedRoutes) ;
		model.addAttribute("FeaturedNewsinfo", FeaturedNewsinfo);
		model.addAttribute("TravelTipsinfo", TravelTipsinfo);
		model.addAttribute("locations", locations);
		Order order = (attributes!=null) ? attributes : new Order();
	    model.addAttribute("order", order);
		return "Home";
	}
}
