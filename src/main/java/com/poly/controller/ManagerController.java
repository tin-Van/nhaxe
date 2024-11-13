package com.poly.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Ticket;
import com.poly.service.ReportService;

@Controller
@RequestMapping("/admin")
public class ManagerController {

    @Autowired
    ReportService service;
    


    @Autowired
    AccountDAO accountDAO;
    
    @Autowired
    BusDAO busDAO;
    
    @Autowired
    RouteDAO routeDAO;
    
    @Autowired
    TicketDAO ticketDAO;
    
    @GetMapping("")
    public String Manager(Model model) {
        Long bus = busDAO.countTotalBuses();
        model.addAttribute("totalbus", bus);

        Long route = routeDAO.countTotalRoute();
        model.addAttribute("totalroute", route);

        Long account = accountDAO.countTotalAccount();
        model.addAttribute("totalaccount", account);

        List<Account> accnew = accountDAO.findTop4ByOrderByCreatedDateDesc();
        model.addAttribute("accnew", accnew);

        List<Ticket> top4tknew = ticketDAO.findTop4ByOrderByBookingDateDesc();
        model.addAttribute("top4tknew", top4tknew);

        Long ticket = ticketDAO.countTotalTickets();
        model.addAttribute("totalticket", ticket);

        Long Sumticket = ticketDAO.countTotalSum();
        model.addAttribute("totalsum", Sumticket);

        Long customer = ticketDAO.countTotalcustomer();
        model.addAttribute("totalcustomer", customer);

       

        return "/views/items/manager";
    }
}