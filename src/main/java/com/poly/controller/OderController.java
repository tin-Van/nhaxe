package com.poly.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.BusDAO;
import com.poly.DAO.ContenDAO;
import com.poly.DAO.LocationDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.entity.Account;
import com.poly.entity.Bus;
import com.poly.entity.Location;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.entity.Ticket.TicketCodeGenerator;
import com.poly.model.ContentMap;
import com.poly.model.MailInfo;
import com.poly.model.Order;
import com.poly.service.MailSenderService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/order")
public class OderController {

	@Autowired
	RouteDAO routeDAO;
	
	@Autowired
	SeatDAO seatDAO;
	
	@Autowired
	ContenDAO contenDAO;
	@Autowired
 	AccountDAO accountDAO;
	@Autowired
	BusDAO busDAO;

	@Autowired
	LocationDAO locationDAO;

	@Autowired
	HttpSession session;

	@Autowired
	MailSenderService mailSenderService;
	
	@Autowired
	TicketDAO ticketDAO;
	
	@GetMapping("")
	public String getMethodName(Model model,
			@ModelAttribute("route") Route attributes,
			@ModelAttribute("orders") List<Route> orders
			) {
		

		
		List<Route> products = orders;
		model.addAttribute("orders", products);

		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
//		List<Seat> busList = seatDAO.findByBusId(Integer.valueOf(3));
//		model.addAttribute("busList",busList);
		return "/views/items/order";
	}
	@PostMapping("")
	public String postMethodName(Model model,
			@ModelAttribute("route") Route attributes,
			@ModelAttribute("orders") Route oders,
			@RequestParam("userID") String userID,
			@RequestParam("seatID") String seatID,
	        @RequestParam("routeID") int routeId) {
				
		List<Route> products = routeDAO.findAll();
		model.addAttribute("orders", products);

		List<Bus> bus = busDAO.findAll();
		model.addAttribute("bus", bus);
		
		List<Location> locations = locationDAO.findAll();
		model.addAttribute("locations", locations);
		
		return "redirect:/order/bookingconfirmation?userID=" + userID + "&routeID=" + routeId
				+ "&seatID=" + seatID
				;
	}
	@PostMapping("/list")
	public String findRoute(Model model,
			RedirectAttributes redirectAttributes,
	        @Valid @ModelAttribute("order") Order order, BindingResult result) {
	    // Kiểm tra lỗi validation
	    if (result.hasErrors()) {
	        model.addAttribute("order", order);
	        
	        // Lấy tất cả các địa điểm và xe buýt để hiển thị lại trên trang
	        List<Location> locations = locationDAO.findAll();
	        model.addAttribute("locations", locations);

	        List<Bus> buses = busDAO.findAll();
	        model.addAttribute("buses", buses);

	        ContentMap FeaturedNewsinfo = contenDAO.findAllFeaturedNews();
	        ContentMap TravelTipsinfo = contenDAO.findAllTravelTips();
	        model.addAttribute("FeaturedNewsinfo", FeaturedNewsinfo);
	        model.addAttribute("TravelTipsinfo", TravelTipsinfo);

	        return "Home"; // Trả về trang Home với dữ liệu đã nhập
	    }

	    // Lấy tất cả các địa điểm
	    List<Location> locations = locationDAO.findAll();
	    model.addAttribute("locations", locations);

	    // Lấy tất cả các xe buýt
	    List<Bus> buses = busDAO.findAll();
	    model.addAttribute("buses", buses);

	    ContentMap FeaturedNewsinfo = contenDAO.findAllFeaturedNews();
	    ContentMap TravelTipsinfo = contenDAO.findAllTravelTips();
	    model.addAttribute("FeaturedNewsinfo", FeaturedNewsinfo);
	    model.addAttribute("TravelTipsinfo", TravelTipsinfo);

	    // Phân trang

	    List<Route> routes = routeDAO.findByDepartureNameAndDestinationNameAndDepartureDate(
	            order.getDeparture_name(), order.getDestination_name(), order.getDaycheck());

	    // Thêm danh sách tuyến đường vào redirect attributes
	    redirectAttributes.addFlashAttribute("orders", routes);

	    // Kiểm tra nếu danh sách rỗng
	    if (routes.isEmpty()) {
	        redirectAttributes.addFlashAttribute("message", "Opps Không tìm thấy tuyến đường của bạn");
	        return "redirect:/home";
	    }

	    return "redirect:/order";
	}





	@GetMapping("bookingconfirmation")
	public String getBookingConfirmation(Model model,
	                                     @RequestParam("userID") String userID,
	                                     @RequestParam("routeID") int routeID,
	                                     @RequestParam("seatID") List<Integer> seatID) {
	    Route route = routeDAO.findById(routeID)
	                          .orElseThrow(() -> new NoSuchElementException("Route not found with id " + routeID));
	    Account account = accountDAO.findByUsername(userID)
	                                .orElseThrow(() -> new NoSuchElementException("Account not found with username " + userID));
	    
	    
	    while (seatID.remove(null)) { 
        } 
	    // Danh sách chứa các ghế đã chọn
	    List<Seat> selectedSeats = new ArrayList<Seat>();

	    for (Integer id : seatID) {
	        Seat seat = seatDAO.findById(id)
	                           .orElseThrow(() -> new NoSuchElementException("Seat not found with id " + id));
	        selectedSeats.add(seat);
	    }
	    
	    long count = seatID.stream().count();
	    model.addAttribute("route", route);
	    model.addAttribute("account", account);
	    model.addAttribute("seats", seatID);
	    model.addAttribute("selectedSeats", selectedSeats);
	    model.addAttribute("count", count);
	    return "/views/items/info_contact";
	}

	@PostMapping("bookingconfirmation")
	public String postbookingconfirmation(Model model,
			 @RequestParam("email") String email,
		        @RequestParam("name") String name,
		        @RequestParam("userID") String userID,
		        @RequestParam("routeID") String routeID,
		        @RequestParam("count") int count,
		        @RequestParam("seatID") String seatID,
		        @RequestParam("phonenumber") String phonenumber) throws MessagingException {
			 	Optional<Route> routeOptional = routeDAO.findById(Integer.valueOf(routeID));
			    Optional<Account> accountOptional = accountDAO.findByUsername(userID);

			    List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
			    String seatListString = String.join(",", seatList);
			    Route route = routeOptional.get();
			    Account account = accountOptional.get();
	    Ticket ticket = new Ticket();
	    return "redirect:/pay?price="+route.getPrice()*count+"&email="+email+"&routeID="+routeID+"&userID="+userID+"&seatID="+seatListString; // Trả về view 'confirmation'
	}


	@PostMapping("cashpayment")
	public String postMethodName(Model model,
	        @RequestParam("email") String email,
	        @RequestParam("name") String name,
	        @RequestParam("userID") String userID,
	        @RequestParam("routeID") String routeID,
	        @RequestParam("count") int count,
	        @RequestParam("seatID") String seatID,
	        @RequestParam("phonenumber") String phonenumber) throws MessagingException {

	    // Tìm tuyến đường và tài khoản từ CSDL
	    Optional<Route> routeOptional = routeDAO.findById(Integer.valueOf(routeID));
	    Optional<Account> accountOptional = accountDAO.findByUsername(userID);

	    // Lấy danh sách ghế từ chuỗi seatID
	    List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
	    Route route = routeOptional.get();
	    Account account = accountOptional.get();

	    // Danh sách mã vé
	    List<String> ticketCodes = new ArrayList<>();

	    // Lặp qua danh sách ghế, tạo vé cho mỗi ghế
	    for (String seatNumber : seatList) {
	        Ticket ticket = new Ticket();
	        Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatNumber));
	        Seat seat = seatOptional.get();
	        ticket.setPrice(route.getPrice());
	        ticket.setRoute(route);
	        ticket.setPaymentstatus("Pending");
	        ticket.setUsername(account.getUsername());
	        ticket.setCustomer(account.getFullname());
	        ticket.setPhonenumber(phonenumber);
	        ticket.setBookingDate(LocalDateTime.now());
	        ticket.setSeat(seat);
	        String ticketCode = TicketCodeGenerator.generateTicketCode();
	        ticket.setTicketCode(ticketCode);
	        ticketCodes.add(ticketCode);  // Thêm mã vé vào danh sách
	        seat.setBooked(true);
	        seatDAO.save(seat);
	        ticketDAO.save(ticket);
	    }

	    // Tạo chuỗi mã vé
	    String ticketCodesString = String.join(", ", ticketCodes);

	    // Tạo email
	    
	    String subject = "Hóa Đơn Điện Tử";
	    String body = "<html>" +
	            "<head>" +
	            "<style>" +
	            "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
	            ".container { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }" +
	            ".header { text-align: center; color: #4CAF50; }" +
	            ".footer { margin-top: 20px; text-align: center; font-size: 0.9em; color: #777; }" +
	            ".bold { font-weight: bold; }" +
	            "</style>" +
	            "</head>" +
	            "<body>" +
	            "<div class='container'>" +
	            "<h2 class='header'>Hóa Đơn Điện Tử</h2>" +
	            "<p>Cảm ơn bạn đã mua hàng, <span class='bold'>" + account.getFullname() + "</span>!</p>" +
	            "<p>Thông tin vé của bạn:</p>" +
	            "<ul>" +
//	            "<li><span class='bold'>Tên xe:</span> " + bus.name + "</li>" + // tên xe
	            "<li><span class='bold'>Mã vé xe:</span> " + ticketCodesString + "</li>" + // Hiển thị tất cả mã vé
	            "<li><span class='bold'>Ghế số:</span> " + seatID + "</li>" + // bị []
	            "<li><span class='bold'>Địa điểm khởi hành:</span> " + route.getDeparture().getName() + "</li>" +
	            "<li><span class='bold'>Địa điểm đến:</span> " + route.getDestination().getName() + "</li>" +
	            "<li><span class='bold'>Thời gian khởi hành:</span> " + route.getTime() + "</li>" +
	            "<li><span class='bold'>Ngày khởi hành:</span> " + route.getDepartureDate() + "</li>" +
	            "<li><span class='bold'>Khoảng cách:</span> " + route.getDistance() + " km</li>" +
	            "<li><span class='bold'>Thời gian dự kiến:</span> " + route.getEstimatedDuration() + "</li>" +
	            "<li><span class='bold'>Giá tiền:</span> " + route.getPrice() + " VND</li>" +
	            "</ul>" +
	            "<p><span class='bold'>Trạng thái thanh toán:</span> Thanh Toán bằng tiền mặt</p>" +
	            "<p>Chúc bạn có chuyến đi vui vẻ!</p>" +
	            "</div>" +
	            "<div class='footer'>" +
	            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
	            "</div>" +
	            "</body>" +
	            "</html>";

	    // Gửi email
	    MailInfo info = new MailInfo(email, subject, body);
	    mailSenderService.senEmailConfirm(email, subject, body);

	    // Thêm thông báo cho model
	    model.addAttribute("message", "Đặt vé thành công! Hóa đơn sẽ được gửi về mail của bạn.");
	    return "/views/items/confirmation";
	}


	
}
