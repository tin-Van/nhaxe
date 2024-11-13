package com.poly.service;

import com.poly.DTO.AccountDTO;
import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class EmailEncoderImpl implements EmailEncoder {
    // Các phương thức của bạn


    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailEncoderImpl.class);

    @Override
    public void sendEmail(MailInfo info) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

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
        // Logic để thêm email vào hàng đợi (nếu có)
        logger.info("Email queued for {}", mail.getTo());
    }

    @Override
    public void queue(String to, String subject, String body) {
        MailInfo mailInfo = new MailInfo(to, subject, body);
        queue(mailInfo);
    }

    @Override
    public void sendHtmlMail(@Valid AccountDTO account) throws MessagingException {
        String subject = "Welcome to our service";
        String body = "<h1>Welcome " + account.getUsername() + "</h1><p>Thank you for registering with us!</p>";
        sendEmail(new MailInfo(account.getEmail(), subject, body));
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetUrl) throws MessagingException {
        String subject = "Password Reset Request";
        String body = "<p>Click the following link to reset your password:</p>" + 
                      "<a href='" + resetUrl + "'>Reset Password</a>";
        sendEmail(new MailInfo(email, subject, body));
    }

    @Override
    public void sendVerificationEmail(String to, String subject, String body) throws MessagingException {
        sendEmail(new MailInfo(to, subject, body)); // Sử dụng lại phương thức sendEmail
    }

    @Override
    public String encoder(Object newverificationCode) {
        // Nếu không sử dụng phương thức này, có thể xóa hoặc triển khai logic phù hợp
        return null;
    }

	@Override
	public void sendPasswordResetEmail(String email) throws MessagingException {
		// TODO Auto-generated method stub
		
	}
}
