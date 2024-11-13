package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Report;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.service.ReportService;

@Controller
@RequestMapping("/admin")
public class TicketController {
	
	@Autowired
	TicketDAO dao;
	
	@Autowired
	SeatDAO seatDAO;

	@GetMapping("ticket")
	public String inventory(Model model) {
	List<Ticket> items = dao.findAll();
	List<Seat> seats = seatDAO.findAll();
	model.addAttribute("seats", seats);
	model.addAttribute("tickets", items);
	return "/views/items/ticket";
	}
	@PostMapping("ticket")
	public String postMethodName(
			@RequestParam String paystatus,
			@RequestParam String seatID,
			@RequestParam String id) {
	Optional<Ticket> ticketOptional = dao.findById(Integer.valueOf(id));
	Ticket ticket= ticketOptional.get();
	Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatID));
	Seat seat= seatOptional.get();
	ticket.setPaymentstatus(paystatus);
	if (paystatus.equals("true")) {
		seat.setBooked(true);
	}else {
		seat.setBooked(false);
	}
	seatDAO.save(seat);
	dao.save(ticket);
	System.out.print(paystatus);
	return "redirect:/admin/ticket";
	}
	
}
