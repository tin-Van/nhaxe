package com.poly.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.RouteDAO;
import com.poly.DAO.SeatDAO;
import com.poly.DAO.TicketDAO;
import com.poly.config.Config;
import com.poly.entity.Account;
import com.poly.entity.Route;
import com.poly.entity.Seat;
import com.poly.entity.Ticket;
import com.poly.entity.Ticket.TicketCodeGenerator;
import com.poly.service.MailSenderService;

import ch.qos.logback.core.status.Status;

import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;

@Controller
public class PaymentController {
	@Autowired
	RouteDAO routeDAO;
	@Autowired
 	AccountDAO accountDAO;
	@Autowired
 	TicketDAO ticketDAO;
	@Autowired
 	SeatDAO seatDAO;
	@Autowired
	MailSenderService mailSenderService;
	@GetMapping("/pay")
	
	public String getPay(@RequestParam String price ,@RequestParam String routeID,@RequestParam String userID,@RequestParam String seatID, 
			@RequestParam(value = "status", required = false, defaultValue = "unpaid") String status
			) throws UnsupportedEncodingException{
		
		String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = (long) (Double.parseDouble(price) * 100);
        String bankCode = "NCB";
        
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.getVnpReturnUrl(userID,routeID,status, seatID));
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
		
		//return paymentUrl;
        return "redirect:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?"+queryUrl;
       
	}
//	@GetMapping("/comfirm")
//	public String getcomfirm(Model model,
//	                         @RequestParam("userID") String userID,
//	                         @RequestParam("routeID") String routeID,
//	                         @RequestParam("seatID") String seatID,
//	                         @RequestParam("vnp_Amount") String vnpAmount,
//	                         @RequestParam("vnp_BankCode") String vnpBankCode,
//	                         @RequestParam("vnp_OrderInfo") String vnpOrderInfo,
//	                         @RequestParam("vnp_ResponseCode") String vnpResponseCode,
//	                         @RequestParam("vnp_TmnCode") String vnpTmnCode,
//	                         @RequestParam("vnp_TransactionStatus") String vnpTransactionStatus,
//	                         @RequestParam("vnp_TxnRef") String vnpTxnRef,
//	                         @RequestParam(value = "status", required = false, defaultValue = "unpaid") String status) throws MessagingException {
//	    
//	    List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
//	    String seatListString = String.join(",", seatList);
//	    Optional<Route> routeOptional = routeDAO.findById(Integer.valueOf(routeID));
//	    Optional<Account> accountOptional = accountDAO.findByUsername(userID);
//
//	    if (!routeOptional.isPresent() || !accountOptional.isPresent()) {
//	        model.addAttribute("error", "Không tìm thấy thông tin tài khoản hoặc tuyến đường.");
//	        return "error"; // Trả về trang error.html với thông báo lỗi
//	    }
//
//	    Route route = routeOptional.get();
//	    Account account = accountOptional.get();
//
//	    // Danh sách mã vé
//	    List<String> ticketCodes = new ArrayList<>();
//	    
//	    // Xử lý vé và trạng thái thanh toán
//	    for (String seatNumber : seatList) {
//	        Ticket ticket = new Ticket();
//	        ticket.setUsername(account.getFullname());
//	        ticket.setPrice(route.getPrice());
//	        ticket.setRoute(route);
//	        ticket.setCustomer(account.getFullname());
//	        ticket.setPhonenumber(account.getPhone());
//	        ticket.setBookingDate(LocalDateTime.now());
//	        String ticketCode = TicketCodeGenerator.generateTicketCode();
//	        ticket.setTicketCode(ticketCode);
//	        ticketCodes.add(ticketCode);  // Thêm mã vé vào danh sách
//	        Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatNumber));
//	        Seat seat = seatOptional.get();
//	        ticket.setSeat(seat);
////	        ticket.setSeatNumber(seatNumber); // Gán số ghế vào vé
//	        seat.setBooked(true);
//	        seatDAO.save(seat);
//	        
//	        // Kiểm tra trạng thái thanh toán
//	        if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
//	            ticket.setPaymentstatus("True");
//
//	         // Tạo chuỗi mã vé
//	    	    String ticketCodesString = String.join(", ", ticketCodes);
//
//	    	    // Tạo email
//	    	    String subject = "Hóa Đơn Điện Tử";
//	    	    String body = "<html>" +
//	    	            "<head>" +
//	    	            "<style>" +
//	    	            "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
//	    	            ".container { background-color: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }" +
//	    	            ".header { text-align: center; color: #4CAF50; }" +
//	    	            ".footer { margin-top: 20px; text-align: center; font-size: 0.9em; color: #777; }" +
//	    	            ".bold { font-weight: bold; }" +
//	    	            "</style>" +
//	    	            "</head>" +
//	    	            "<body>" +
//	    	            "<div class='container'>" +
//	    	            "<h2 class='header'>Hóa Đơn Điện Tử</h2>" +
//	    	            "<p>Cảm ơn bạn đã mua hàng, <span class='bold'>" + account.getFullname() + "</span>!</p>" +
//	    	            "<p>Thông tin vé của bạn:</p>" +
//	    	            "<ul>" +
//	    	            "<li><span class='bold'>Mã vé xe:</span> " + ticketCodesString + "</li>" + // Hiển thị tất cả mã vé
//	    	            "<li><span class='bold'>Tên tuyến đường:</span> " + route.getName() + "</li>" +
//	    	            "<li><span class='bold'>Địa điểm khởi hành:</span> " + route.getDeparture().getName() + "</li>" +
//	    	            "<li><span class='bold'>Địa điểm đến:</span> " + route.getDestination().getName() + "</li>" +
//	    	            "<li><span class='bold'>Thời gian khởi hành:</span> " + route.getTime() + "</li>" +
//	    	            "<li><span class='bold'>Ngày khởi hành:</span> " + route.getDepartureDate() + "</li>" +
//	    	            "<li><span class='bold'>Khoảng cách:</span> " + route.getDistance() + " km</li>" +
//	    	            "<li><span class='bold'>Thời gian dự kiến:</span> " + route.getEstimatedDuration() + "</li>" +
//	    	            "<li><span class='bold'>Giá tiền:</span> " + route.getPrice() + " VND</li>" +
//	    	            "</ul>" +
//	    	            "<p><span class='bold'>Trạng thái thanh toán:</span> Thanh Toán online</p>" +
//	    	            "<p>Chúc bạn có chuyến đi vui vẻ!</p>" +
//	    	            "</div>" +
//	    	            "<div class='footer'>" +
//	    	            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
//	    	            "</div>" +
//	    	            "</body>" +
//	    	            "</html>";
//
//		        mailSenderService.senEmailConfirm(account.getEmail(), subject, body);
//
//		        // Thêm thông báo thành công vào model
//		        model.addAttribute("message", "Đặt vé thành công! Hóa đơn sẽ được gửi về mail của bạn.");
//		    } else {
//		        // Giao dịch thất bại hoặc bị hủy
//		        ticket.setPaymentstatus("False");
//		        model.addAttribute("message", "Thanh toán không thành công.");
//		    }
//
//		    // Lưu vé vào cơ sở dữ liệu
//		    ticketDAO.save(ticket);
//	    }
//	    
//	    return "/views/items/confirmation.html"; // Trang thông báo thành công
//	}
	@GetMapping("/comfirm")
	public String getcomfirm(Model model,
	                         @RequestParam("userID") String userID,
	                         @RequestParam("routeID") String routeID,
	                         @RequestParam("seatID") String seatID,
	                         @RequestParam("vnp_Amount") String vnpAmount,
	                         @RequestParam("vnp_BankCode") String vnpBankCode,
	                         @RequestParam("vnp_OrderInfo") String vnpOrderInfo,
	                         @RequestParam("vnp_ResponseCode") String vnpResponseCode,
	                         @RequestParam("vnp_TmnCode") String vnpTmnCode,
	                         @RequestParam("vnp_TransactionStatus") String vnpTransactionStatus,
	                         @RequestParam("vnp_TxnRef") String vnpTxnRef,
	                         @RequestParam(value = "status", required = false, defaultValue = "unpaid") String status) throws MessagingException {

	    List<String> seatList = Arrays.asList(seatID.replaceAll("[\\[\\]\\s]", "").split(","));
	    String seatListString = String.join(",", seatList);
	    Optional<Route> routeOptional = routeDAO.findById(Integer.valueOf(routeID));
	    Optional<Account> accountOptional = accountDAO.findByUsername(userID);

	    if (!routeOptional.isPresent() || !accountOptional.isPresent()) {
	        model.addAttribute("error", "Không tìm thấy thông tin tài khoản hoặc tuyến đường.");
	        return "error"; // Trả về trang error.html với thông báo lỗi
	    }

	    Route route = routeOptional.get();
	    Account account = accountOptional.get();

	    // Danh sách chứa thông tin các vé
	    List<Ticket> tickets = new ArrayList<>();

	    // Xử lý vé cho từng ghế
	    for (String seatNumber : seatList) {
	        Ticket ticket = new Ticket();
	        ticket.setUsername(account.getUsername());
	        ticket.setPrice(route.getPrice());
	        ticket.setRoute(route);
	        ticket.setCustomer(account.getFullname());
	        ticket.setPhonenumber(account.getPhone());
	        ticket.setBookingDate(LocalDateTime.now());
	        ticket.setTicketCode(TicketCodeGenerator.generateTicketCode()); // Tạo mã vé
	        Optional<Seat> seatOptional = seatDAO.findById(Integer.valueOf(seatNumber));
	        Seat seat = seatOptional.get();
	        ticket.setSeat(seat);
	        seat.setBooked(true);
	        seatDAO.save(seat);
	        ticketDAO.save(ticket);
	        tickets.add(ticket); // Thêm vé vào danh sách
	    }

	    // Kiểm tra trạng thái thanh toán và cập nhật thông tin
	    StringBuilder ticketCodesBuilder = new StringBuilder();
	    if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
	        for (Ticket ticket : tickets) {
	            ticket.setPaymentstatus("True");
	            ticketDAO.save(ticket);

	            // Thêm mã vé vào chuỗi
	            ticketCodesBuilder.append(ticket.getTicketCode()).append(", ");
	        }

	        // Tạo nội dung email với thông tin tất cả các vé
	        String ticketCodesString = ticketCodesBuilder.toString().replaceAll(", $", ""); // Loại bỏ dấu phẩy cuối
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
//    	            "<li><span class='bold'>Tên xe:</span> " + bus.name + "</li>" + // tên xe
    	            "<li><span class='bold'>Mã vé xe:</span> " + ticketCodesString + "</li>" + // Hiển thị tất cả mã vé
    	            "<li><span class='bold'>Ghế số:</span> " + seatID + "</li>" +
    	            "<li><span class='bold'>Địa điểm khởi hành:</span> " + route.getDeparture().getName() + "</li>" +
    	            "<li><span class='bold'>Địa điểm đến:</span> " + route.getDestination().getName() + "</li>" +
    	            "<li><span class='bold'>Thời gian khởi hành:</span> " + route.getTime() + "</li>" +
    	            "<li><span class='bold'>Ngày khởi hành:</span> " + route.getDepartureDate() + "</li>" +
    	            "<li><span class='bold'>Khoảng cách:</span> " + route.getDistance() + " km</li>" +
    	            "<li><span class='bold'>Thời gian dự kiến:</span> " + route.getEstimatedDuration() + "</li>" +
    	            "<li><span class='bold'>Giá tiền:</span> " + route.getPrice() + " VND</li>" +
    	            "</ul>" +
    	            "<p><span class='bold'>Trạng thái thanh toán:</span> Thanh Toán online</p>" +
    	            "<p>Chúc bạn có chuyến đi vui vẻ!</p>" +
    	            "</div>" +
    	            "<div class='footer'>" +
    	            "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>" +
    	            "</div>" +
    	            "</body>" +
    	            "</html>";

	        // Gửi email cho khách hàng
	        mailSenderService.senEmailConfirm(account.getEmail(), subject, body);

	        // Thêm thông báo thành công vào model
	        model.addAttribute("message", "Đặt vé thành công! Hóa đơn sẽ được gửi về mail của bạn.");
	    } else {
	        // Giao dịch thất bại hoặc bị hủy
	        for (Ticket ticket : tickets) {
	            ticket.setPaymentstatus("False");
	            ticketDAO.save(ticket);
	        }

	        model.addAttribute("message", "Thanh toán không thành công. Mã phản hồi: " + vnpResponseCode);
	    }

	    return "/views/items/confirmation.html"; // Trả về trang xác nhận sau khi xử lý
	}

}

