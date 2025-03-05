package com.sanedge.modularexample.booking.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sanedge.modularexample.booking.service.BookingMailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingMailServiceImpl implements BookingMailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public BookingMailServiceImpl(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailCheckIn(String orderId, String email, String formattedDate) {
        try {
            log.info("Sending check-in email for orderId: {} to email: {}", orderId, email);

          
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("formattedDate", formattedDate);

            String content = templateEngine.process("checkin", context);


            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Check-In Confirmation");
            helper.setText(content, true);

   
            javaMailSender.send(message);

            log.info("Check-in email sent successfully to: {}", email);

        } catch (Exception e) {
            throw new RuntimeException("Error sending check-in email", e);
        }
    }

    public void sendEmailCheckOut(String orderId, String email, String formattedDate) {
        try {
            log.info("Sending check-out email for orderId: {} to email: {}", orderId, email);

            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("formattedDate", formattedDate);

            String content = templateEngine.process("checkout", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Check-Out Confirmation");
            helper.setText(content, true);

            javaMailSender.send(message);

            log.info("Check-out email sent successfully to: {}", email);

        } catch (Exception e) {
            throw new RuntimeException("Error sending check-out email", e);
        }
    }

    public void sendEmailBookingTime(String orderId, String email, LocalDateTime localDateTime) {
        try {
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("formattedDate",
                    localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            String content = templateEngine.process("checkin_remainder", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Booking Time Notification");
            helper.setText(content, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error sending booking time notification email", e);
        }
    }
}