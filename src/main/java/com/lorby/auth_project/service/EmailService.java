package com.lorby.auth_project.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Value("${EMAIL_USERNAME}")
    private String from;
    public void sendEmail(String to, String subject, String text){
        try{
            logger.info("Sending email to: {}", to);
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            logger.debug("Preparing to send email to: {}", to);
            emailSender.send(message);
            log.info("Email sent to: {}", to);
            logger.info("Email sent successfully to: {}", to);
        } catch (MessagingException ex){
            logger.error("Failed to send email to: {}", to, ex);
            throw new MailSendException("Failed to send email");
        }
    }
}
