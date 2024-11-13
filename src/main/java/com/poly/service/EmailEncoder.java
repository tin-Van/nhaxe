package com.poly.service;

import com.poly.DTO.AccountDTO;
import com.poly.model.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface EmailEncoder {

    void sendEmail(MailInfo info) throws MessagingException;

    void queue(MailInfo mail);

    void queue(String to, String subject, String body);

    void sendHtmlMail(@Valid AccountDTO account) throws MessagingException;

    void sendPasswordResetEmail(String email, String resetUrl) throws MessagingException;

	String encoder(Object newverificationCode);

	void sendPasswordResetEmail(String email) throws MessagingException;

	void sendVerificationEmail(String to, String subject, String body) throws MessagingException;
}
