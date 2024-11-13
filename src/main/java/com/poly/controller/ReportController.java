package com.poly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Bus;
import com.poly.entity.Report;
import com.poly.service.ReportService;

@Controller
@RequestMapping("/admin")
public class ReportController {
	
	@Autowired
	ReportService service;
	
	@Autowired
	BusDAO busDAO;
	
	@Autowired
	RouteDAO routeDAO;
	
	@Autowired
	TicketDAO ticketDAO;
	
	@GetMapping("report")
	public String inventory(Model model) {
	List<Report> items = service.getInventoryByCategory();
	model.addAttribute("reports", items);
	
	Long bus = busDAO.countTotalBuses();
	model.addAttribute("totalbus", bus);
	
	Long route = routeDAO.countTotalRoute();
	model.addAttribute("totalroute", route);
	
	Long ticket = ticketDAO.countTotalTickets();
	model.addAttribute("totalticket", ticket);
	
	Long Sumticket =  ticketDAO.countTotalSum();
	model.addAttribute("totalsum", Sumticket);
	return "/views/items/report";
	}
}
