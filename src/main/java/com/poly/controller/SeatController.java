package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.BusDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.entity.Bus;
import com.poly.entity.Route;
import com.poly.entity.Seat;

@Controller
public class SeatController {

	@Autowired
	SeatDAO seatDAO;

	@Autowired
	BusDAO busDAO;

	@Autowired
	RouteDAO routeDAO;

	@GetMapping("/routes/{routeId}/seats")
    public String getSeatsByRoute(@PathVariable int routeId, Model model) {
        Optional<Route> routeOpt = routeDAO.findById(routeId);
        if (!routeOpt.isPresent()) {
            // Handle case where route is not found
            model.addAttribute("error", "Route not found");
            return "error";
        }
        Route route = routeOpt.get();
        model.addAttribute("route", route);
        model.addAttribute("seats", seatDAO.findByRouteId(routeId));
        return "/views/bus_items/seat_order";
    }
//	}

}
