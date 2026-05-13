package com.casey.applyflow.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.casey.applyflow.model.EmailVerificationToken;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.EmailTokenRepository;

import jakarta.mail.internet.MimeMessage;

@Service
@EnableAsync
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    private EmailTokenRepository emailTokenRepository;
    private String mailFrom = "ApplyFlow";

    public EmailService(JavaMailSender mailSender, EmailTokenRepository emailTokenRepository) {
        this.mailSender = mailSender;
        this.emailTokenRepository = emailTokenRepository;
    }

    @Async
    public void sendVerificationEmail(User user, String token) {
        log.warn("EMAIL_SEND_START to={}", user.getEmail());
        String verificationLink = "http://localhost:5173/email-verify?token=" + token;
        String body = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9fafb; }
                        .email-content { background-color: white; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 30px; }
                        h1 { margin: 0; color: #2563eb; font-size: 24px; }
                        .greeting { font-size: 16px; margin: 20px 0; }
                        .message { color: #555; margin: 20px 0; line-height: 1.8; }
                        .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #999; }
                        .link-text { color: #2563eb; word-break: break-all; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="email-content">
                            <div class="header">
                                <h1>Verify Your Email</h1>
                            </div>
                            <p class="greeting">Hi %s,</p>
                            <p class="message">
                                Welcome to ApplyFlow! Please verify your email address to activate your account.
                            </p>
                            <a href="%s" style="background-color: #2563eb; color: white; padding: 12px 30px; border-radius: 5px; text-decoration: none; display: inline-block; margin: 30px 0; font-weight: 600;">Verify Email Address</a>
                            <p class="message">
                                Or copy and paste this link in your browser:<br>
                                <span class="link-text">%s</span>
                            </p>
                            <p class="message">
                                This verification link expires in 24 hours.
                            </p>
                            <div class="footer">
                                <p>If you didn't create this account, you can safely ignore this email.</p>
                                <p>© 2026 ApplyFlow. All rights reserved.</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """, user.getName(), verificationLink, verificationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify your account");
            helper.setText(body, true); // true means this is HTML
            mailSender.send(message);
            log.warn("EMAIL_SEND_SUCCESS to={}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send verification email to {}", user.getEmail(), ex);
        }
    }

    @Async
    public void sendInvitationEmail(User user, Long jobBoarId) {
        // token generation
        String token = UUID.randomUUID().toString(); // TODO: Make token more secure
        String to = user.getEmail();
        String subject = "You're invited to a job board";
        String invitationLink = "http://localhost:5173/job-boards/" + jobBoarId + "/invitation?token=" + token;
        String body = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9fafb; }
                        .email-content { background-color: white; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 30px; }
                        h1 { margin: 0; color: #2563eb; font-size: 24px; }
                        .greeting { font-size: 16px; margin: 20px 0; }
                        .message { color: #555; margin: 20px 0; line-height: 1.8; }
                        .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #999; }
                        .link-text { color: #2563eb; word-break: break-all; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="email-content">
                            <div class="header">
                                <h1>Job Board Invitation</h1>
                            </div>
                            <p class="greeting">Hi %s,</p>
                            <p class="message">
                                You've been invited to join a job board! Click the button below to accept the invitation and get started.
                            </p>
                            <a href="%s" style="background-color: #2563eb; color: white; padding: 12px 30px; border-radius: 5px; text-decoration: none; display: inline-block; margin: 30px 0; font-weight: 600;">Accept Invitation</a>
                            <p class="message">
                                Or copy and paste this link in your browser:<br>
                                <span class="link-text">%s</span>
                            </p>
                            <p class="message">
                                This invitation expires in 24 hours.
                            </p>
                            <div class="footer">
                                <p>If you didn't expect this invitation, you can safely ignore this email.</p>
                                <p>© 2026 ApplyFlow. All rights reserved.</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """, user.getName(), invitationLink, invitationLink);

        // one-to-one token mapping allows only one token row per user, so update existing token if present
        EmailVerificationToken verificationToken = emailTokenRepository.findByUserId(user.getId())
            .orElseGet(EmailVerificationToken::new);

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24hr token expiry

        // save to db
        emailTokenRepository.save(verificationToken);
        log.warn("JOB_BOARD_INVITATION_TOKEN_SAVED email={} token={}...", user.getEmail(), token.substring(0, 8));

        log.warn("INVITATION_EMAIL_SEND_START to={}", user.getEmail());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true means this is HTML
            mailSender.send(message);
            log.warn("INVITATION_EMAIL_SEND_SUCCESS to={}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send invitation email to {}", user.getEmail(), ex);
        }
    }

}
