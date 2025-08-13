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
        String htmlMsg = "<p>Pozdrav " + userDTO.name + ",</p>"
                + "<p>Klikni na link ispod kako bi izvršio verifikaciju:</p>"
                + "<a href='" + verificationUrl + "'>Verifikuj svoj nalog</a>"
                + "<p>Hvala!</p>";

        helper.setText(htmlMsg, true);
        mailSender.send(mail);
    }

    public void SendInactivityEmail(UserDTO userDTO, String link) throws MailException, InterruptedException, MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);

        helper.setTo(userDTO.email);
        helper.setFrom(env.getProperty("spring.mail.username"));
        helper.setSubject("SecurityCertificateApp™ - Password recovery ");

        String htmlMsg = "<p>Pozdrav " + userDTO.name + ",</p>"
                + "<p>To create your new password click on linky below</p>"
                + "<a href='" +link + "'>Change password</a>";
    }
}
