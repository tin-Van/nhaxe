	package com.poly.controller;

import java.security.SecureRandom;
import java.util.Optional;

import org.eclipse.angus.mail.imap.protocol.BODY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.poly.DAO.AccountDAO;
import com.poly.DAO.AccountdtoDAO;
import com.poly.DAO.PasswordResetTokenDAO;
import com.poly.DAO.VerificationTokenDAO;
import com.poly.DTO.AccountDTO;
import com.poly.entity.Account;
import com.poly.entity.Route;
import com.poly.mapper.AccountMapper;
import com.poly.model.PasswordResetToken;
import com.poly.model.VerificationToken;
import com.poly.service.AccountService;
import com.poly.service.EmailEncoder;
import com.poly.service.MailSenderService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/req")
public class AccountController {
    private static final SecureRandom secureRandom = new SecureRandom();
    @Autowired
    private HttpServletRequest req;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailEncoder emailEncoder;
    
    @Autowired
    AccountdtoDAO accountdtoDAO;
    
    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private PasswordResetTokenDAO passwordResetTokenRepository;

    @Autowired
    private VerificationTokenDAO verificationTokenRepository;

    private String generateVerificationCode() {
        int code = secureRandom.nextInt(999999); // Số ngẫu nhiên từ 0 đến 999999
        return String.format("%06d", code); // Chuyển thành chuỗi gồm 6 chữ số
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        Account ac = new Account();
        model.addAttribute("account", ac);
        return "/views/home_items/Login";
    }

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        AccountDTO ac = new AccountDTO();
        model.addAttribute("account", ac);
        return "/views/home_items/registration";
    }

    @PostMapping("/registration")
    public String registerAccount(
            RedirectAttributes redirectAttributes,Model model,
            @Valid @ModelAttribute("account") AccountDTO account,
            BindingResult result) throws MessagingException {
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!account.getPassword().equals(account.getConfirmpassword())) {
            result.addError(new FieldError("account", "confirmpassword", "Mật khẩu không khớp"));
        }

        // Kiểm tra xem username đã tồn tại chưa
        if (accountDAO.existsByUsername(account.getUsername())) {
            result.addError(new FieldError("account", "username", "Tên người dùng đã tồn tại"));
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (accountDAO.existsByEmail(account.getEmail())) {
            result.addError(new FieldError("account", "email", "Email đã tồn tại"));
        }

        // Nếu có lỗi, quay lại trang đăng ký
        if (result.hasErrors()) {
            return "/views/home_items/registration";
        }
        System.out.print(account.getFullname());
        // Mã hóa mật khẩu và lưu tài khoản chưa kích hoạt
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        AccountDTO accountDTO = account;
        accountDTO.setPassword(encodedPassword);
		accountdtoDAO.save(accountDTO);
        String verificationCode = generateVerificationCode();
        VerificationToken token = new VerificationToken(verificationCode,account);
//        AccountDTO accountDTO = new AccountDTO(account);

        verificationTokenRepository.save(token);
        // Gửi email với mã xác nhận
        mailSenderService.sendVerificationEmail(account.getEmail(), verificationCode);

        // Chuyển hướng đến trang thông báo rằng email đặt lại mật khẩu đã được gửi
        model.addAttribute("message", "Email xác thực đã được gửi đến " + account.getEmail() + ".");
        return "redirect:/req/verify-account";
    }


    @GetMapping("/forget")
    public String getForget(Model model) {
        Account ac = new Account();
        model.addAttribute("account", ac);
        return "/views/home_items/forget";
    
    }
    
    @PostMapping("/forget")
    public String forgotPassword(@RequestParam String email, Model model) throws MessagingException {
        // Kiểm tra xem email có tồn tại trong hệ thống hay không
        Optional<Account> userOptional = accountDAO.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "error";  // Quay lại trang quên mật khẩu với thông báo lỗi
            
        }
        
        // Tạo mã xác nhận (token) duy nhất và lưu vào cơ sở dữ liệu
        String token = generateUniqueToken();  
        PasswordResetToken resetToken = new PasswordResetToken(token, userOptional.get());
        
        try {
            passwordResetTokenRepository.save(resetToken);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Có lỗi xảy ra, vui lòng thử lại.");
            return "/views/home_items/forget";  // Trả về trang quên mật khẩu nếu xảy ra lỗi
        }

        // Gửi email với mã xác nhận
        mailSenderService.sendPasswordResetEmail(email, token);

        // Chuyển hướng đến trang thông báo rằng email đặt lại mật khẩu đã được gửi
        model.addAttribute("message", "Email đặt lại mật khẩu đã được gửi đến " + email + ".");
        return "redirect:/req/ResetPassword?token=" + token;
    }
      
 

    private String generateUniqueToken() {
        String token;
        do {
            token = generateVerificationCode(); // Tạo mã 6 chữ số
        } while (passwordResetTokenRepository.existsByToken(token)); // Kiểm tra xem mã đã tồn tại trong DB chưa
        return token;
    }
    @GetMapping("/verify-account")
    public String getVerifyAccount( @RequestParam(required = false) String token, 
                                   Model model) {
        model.addAttribute("token", token); // Thêm token vào model
        return "/views/home_items/VerifyAccount"; // Chuyển đến trang xác thực
    }

    @PostMapping("/verify-account")
    public String verifyAccount(@RequestParam String verificationCode,
                                @RequestParam String token,
             
                                RedirectAttributes redirectAttributes, Model model) throws MessagingException {
        VerificationToken vrtoken = verificationTokenRepository.findByToken(verificationCode);
        if (vrtoken != null && !vrtoken.isExpired()) {
            // Kiểm tra mã xác thực nhập vào
            if (verificationCode.equals(vrtoken.getToken())) {
            	accountDAO.save(AccountMapper.toAccount(vrtoken.getAccount(),vrtoken.getAccount().getPassword()));
                // Xóa mã xác thực đã sử dụng
                verificationTokenRepository.delete(vrtoken);
                accountdtoDAO.delete(vrtoken.getAccount());
                // Gửi email chào mừng đến tài khoản
                mailSenderService.sendHtmlMail(vrtoken.getAccount().getFullname(), vrtoken.getAccount().getEmail());
                redirectAttributes.addFlashAttribute("message", "Tài khoản của bạn đã được xác thực thành công.");
                
                return "redirect:http://localhost:8080/home"; // Chuyển đến trang đăng nhập
            } else {
                redirectAttributes.addFlashAttribute("error", "Mã xác thực không hợp lệ.");
                return "error" ; // Quay lại trang xác thực với token
            }
            }
        return "redirect:http://localhost:8080/home"; // Chuyển đến trang đăng nhập
    }

    @GetMapping("/ResetPassword")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "error"; // Trả về trang lỗi nếu token không hợp lệ
            
        }

        model.addAttribute("token", token);
        return "/views/home_items/ResetPassword";
    }

    @PostMapping("/ResetPassword")
    public String handleResetPassword(@RequestParam String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            redirectAttributes.addFlashAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/req/ResetPassword?token=" + token;
            // Quay lại trang đăng nhập nếu token không hợp lệ
        }

        // Lấy người dùng từ token và đặt mật khẩu mới
        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountDAO.save(account);

        // Vô hiệu hóa token sau khi sử dụng
        passwordResetTokenRepository.delete(resetToken);

        redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được đặt lại thành công.");
        return "redirect:/req/login"; // Chuyển đến trang đăng nhập
    }
}
