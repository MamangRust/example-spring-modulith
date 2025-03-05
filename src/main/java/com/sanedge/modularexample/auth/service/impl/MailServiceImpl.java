package com.sanedge.modularexample.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sanedge.modularexample.auth.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public MailServiceImpl(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmailVerify(String email, String token) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            messageHelper.setSubject("Verify Email");
            messageHelper.setFrom("yanto@hotel.com");
            messageHelper.setTo(email);
            Context context = new Context();
            context.setVariable("token", token);
            messageHelper.setText(templateEngine.process("mail/verify", context), true);
        };

        javaMailSender.send(messagePreparator);
    };

    @Override
    public void sendResetPasswordEmail(String email, String resetLink) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            messageHelper.setSubject("Reset Your Password");
            messageHelper.setFrom("yanto@hotel.com");
            messageHelper.setTo(email);

            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            messageHelper.setText(templateEngine.process("mail/reset-password", context), true);
        };

        javaMailSender.send(messagePreparator);
    }

    public void sendEmailForgotPassword(String email, String resetLink) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            messageHelper.setSubject("Forgot Password");
            messageHelper.setFrom("yanto@hotel.com");
            messageHelper.setTo(email);

            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            messageHelper.setText(templateEngine.process("mail/forgot-password", context), true);
        };

        javaMailSender.send(messagePreparator);
    }
}