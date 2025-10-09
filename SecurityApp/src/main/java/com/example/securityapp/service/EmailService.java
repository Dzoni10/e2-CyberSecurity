package com.example.securityapp.service;

import com.example.securityapp.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Async
    public void sendVerificationEmail(UserDTO userDTO, String link) throws MailException, InterruptedException, MessagingException {

        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);

        helper.setTo(userDTO.email);
        helper.setFrom(env.getProperty("spring.mail.username"));
        helper.setSubject("SecurityCertificateApp™ - Verification mail");

        String verificationUrl = link;
        String htmlMsg = "<p>Hello " + userDTO.name + ",</p>"
                + "<p>Click on the link below to verify your account</p>"
                + "<a href='" + verificationUrl + "'>Verify account</a>"
                + "<p>Thank you!</p>"
                +"<p>Your SecurityCertificateApp team</p>";

        helper.setText(htmlMsg, true);
        mailSender.send(mail);
    }

    @Async
    public void sendVerificationEmailCA(UserDTO userDTO,String randomPassword, String link) throws MailException, InterruptedException, MessagingException {

        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);

        helper.setTo(userDTO.email);
        helper.setFrom(env.getProperty("spring.mail.username"));
        helper.setSubject("SecurityCertificateApp™ - Verification mail");

        String verificationUrl = link;
        String htmlMsg = "<p>Hello " + userDTO.name + ",</p>"
                + "<p>Click on the link below to verify your account</p>"
                + "<a href='" + verificationUrl + "'>Verify account</a>"
                +"<p>This is your password: " +"<b>"+ randomPassword +"</b>   please change password when you log in</p>"
                + "<p>Thank you!</p>"
                +"<p>Your SecurityCertificateApp team</p>";

        helper.setText(htmlMsg, true);
        mailSender.send(mail);
    }

    @Async
    public void sendPasswordRecoveryEmail(UserDTO userDTO, String link) throws MailException, InterruptedException, MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);

        helper.setTo(userDTO.email);
        helper.setFrom(env.getProperty("spring.mail.username"));
        helper.setSubject("SecurityCertificateApp™ - Password recovery ");

        String htmlMsg = "<p>Hello " + userDTO.name + ",</p>"
                + "<p>To verify your new password click on link below</p>"
                + "<a href='" +link + "'>Verify password</a>"
                +"<p>Thank you</p>"
                +"<p>Your SecurityCertificateApp team</p>";

        helper.setText(htmlMsg, true);
        mailSender.send(mail);
    }
}
