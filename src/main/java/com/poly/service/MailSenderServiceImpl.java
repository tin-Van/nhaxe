package com.poly.service;

import com.poly.DTO.AccountDTO;
import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    @Override
    public void sendEmail(MailInfo info) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Đặt mã hóa UTF-8
            
            helper.setTo(info.getTo());
            helper.setSubject(info.getSubject());
            helper.setText(info.getBody(), true); // 'true' để gửi email HTML
            
            mailSender.send(message);
            logger.info("Email sent successfully to {}", info.getTo());
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", info.getTo(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void queue(MailInfo mail) {
        logger.info("Email queued for {}", mail.getTo());
        // Logic để đưa email vào hàng đợi (nếu cần)
    }

    @Override
    public void queue(String to, String subject, String body) {
        MailInfo mailInfo = new MailInfo(to, subject, body);
        queue(mailInfo);
    }

    @Override
    public void sendHtmlMail1(String name, String email) throws MessagingException {
        String subject = "Chào mừng đến với dịch vụ của chúng tôi";
        String body = "<h1>Chào mừng " + name + "</h1><p>Cảm ơn bạn đã đăng ký với chúng tôi!</p>";
        sendEmail(new MailInfo(email, subject, body));
    }


    @Override
    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
        String subject = "Đặt lại mật khẩu";
        String body = "Mã xác thực là: " + token; // Thêm mã xác thực vào nội dung
        sendEmail(new MailInfo(email, subject, body));
    }
    
    @Override
    public void sendVerificationEmail(String email, String verificationCode) throws MessagingException {
        // Gửi email chứa mã xác thực
        String subjectvr1 = "Xác thực tài khoản";
        String bodyvr1 = "<p>Chào bạn,</p>"
                       + "<p>Mã xác thực của bạn là: <strong>" + verificationCode + "</strong></p>"
                       + "<p>Vui lòng nhập mã này để xác thực tài khoản của bạn.</p>";

        sendEmail(new MailInfo(email, subjectvr1, bodyvr1));
    }

	@Override
	public void sendVerificationEmail(String subjectvr, String bodyvr, String verificationCode)
			throws MessagingException {
		
	}

	@Override
	public void sendHtmlMail(@Valid String string, String email) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void senEmailConfirm(String email,String subject, String body) throws MessagingException {
		 MimeMessage message = mailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Đặt mã hóa UTF-8
         
         helper.setTo(email);
         helper.setSubject(subject);
         helper.setText(body,true); // 'true' để gửi email HTML
         
         mailSender.send(message);
		
	}

	@Override
	public void sendHtmlMail(@Valid AccountDTO account, String email) throws MessagingException {
		// TODO Auto-generated method stub
		
	}




	
}
