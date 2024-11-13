package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Ticket;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/profile")
public class UserController {
	
	@Autowired
	AccountDAO accountDAO;
	@Autowired
	TicketDAO ticketDAO;
	
	@GetMapping("")
	public String getUserProfile(Model model, @RequestParam("username") String username) {
	    Optional<Account> accountOptional = accountDAO.findByUsername(username);
	    Account account = accountOptional.get();	    
	    model.addAttribute("accounts", account);
	    
	    return "/views/profile/profile";
	}
	@GetMapping("/history")
	public String getHistory(Model model, @RequestParam("username") String username) {
		List<Ticket> tickets = ticketDAO.findByUsername(username);
	    model.addAttribute("tickets", tickets);
	    return "/views/profile/buy";
	}
	@PostMapping("")
	public String postMethodName(@ModelAttribute("accounts") Account account) {
		accountDAO.save(account);
		return "redirect:/profile?username=" + account.getUsername();
	}
	
	
}
