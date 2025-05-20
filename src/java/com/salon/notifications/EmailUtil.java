package com.salon.notifications;

import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.mail.MessagingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {
    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    private static final String SMTP_HOST = "smtp.gmail.com"; // Replace with your SMTP host
    private static final int SMTP_PORT = 587;                // Standard TLS port
    private static final String SMTP_USERNAME = ""; // Your email address
    private static final String SMTP_PASSWORD = ""; // Your email password

    // ExecutorService for asynchronous email sending
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Method to generate OTP
    public static String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000 + 100000)); // 6-digit OTP
    }

     // Method to create OTP email body
    public static String createOtpEmailBody(String otp) {
        return "<html>" +
                "<body style='font-family: Lato, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: auto; padding: 20px; border: 2px solid #d4a373; border-radius: 10px;'>" +
                "<img src='Web Pages/images/logo.png' alt='Radiant Locks Hair & Beauty Logo' style='width: 100px; display: block; margin: 0 auto;'>" +
                "<h2 style='font-family: Playfair Display, serif; color: #4b0082; text-align: center;'>Radiant Locks Hair & Beauty</h2>" +
                "<h3 style='text-align: center;'>Registration OTP</h3>" +
                "<p style='text-align: center;'>Dear Customer,</p>" +
                "<p style='text-align: center;'>Your OTP for email verification is:</p>" +
                "<h2 style='color: #d4a373; text-align: center;'>" + otp + "</h2>" +
                "<p style='text-align: center;'>Please enter this OTP to complete your registration. This OTP is valid for 10 minutes.</p>" +
                "<p style='text-align: center; font-size: 12px; color: #777;'>If you did not initiate this registration, please ignore this email.</p>" +
                "<p style='text-align: center; font-size: 14px; color: #4b0082;'>Transform Your Beauty with Radiant Locks</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    // Synchronous email sending
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");

        Transport.send(message);
    }
    
    // Synchronous email sending (with attachment)
    public static void sendEmail(String to, String subject, String body, String attachmentName, byte[] attachment)
            throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Create the message body part
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/html; charset=utf-8");

        // Create the attachment part (if provided)
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        if (attachment != null && attachmentName != null) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setFileName(attachmentName);
            attachmentPart.setContent(attachment, "application/octet-stream");
            multipart.addBodyPart(attachmentPart);
        }

        // Set the multipart content
        message.setContent(multipart);

        Transport.send(message);
    }
    // Asynchronous email sending
    public static void sendEmailAsync(String to, String subject, String body) {
        executorService.submit(() -> {
            try {
                sendEmail(to, subject, body);
                System.out.println("Email sent successfully to: " + to);
            } catch (MessagingException e) {
                System.err.println("Failed to send email to: " + to);
                e.printStackTrace();
            }
        });
    }
    public static String createCancellationEmailBody(String serviceName, String appointmentDate, String appointmentTime, String stylist, String cancellationReason) {
        return "<html>" +
                "<body style='font-family: Lato, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: auto; padding: 20px; border: 2px solid #d4a373; border-radius: 10px;'>" +
                "<img src='Web Pages/images/logo.png' alt='Radiant Locks Logo' style='width: 100px; display: block; margin: 0 auto;'>" +
                "<h2 style='font-family: Playfair Display, serif; color: #4b0082; text-align: center;'>Radiant Locks Hair & Beauty</h2>" +
                "<h3 style='text-align: center;'>Appointment Cancellation Confirmation</h3>" +
                "<p style='text-align: center;'>Dear Customer,</p>" +
                "<p style='text-align: center;'>Your appointment has been successfully cancelled. Below are the details:</p>" +
                "<table style='width: 80%; margin: 20px auto; border-collapse: collapse;'>" +
                "<tr><td style='padding: 8px; font-weight: bold; border: 1px solid #d4a373;'>Service:</td>" +
                "<td style='padding: 8px; border: 1px solid #d4a373;'>" + (serviceName != null ? serviceName : "N/A") + "</td></tr>" +
                "<tr><td style='padding: 8px; font-weight: bold; border: 1px solid #d4a373;'>Date:</td>" +
                "<td style='padding: 8px; border: 1px solid #d4a373;'>" + (appointmentDate != null ? appointmentDate : "N/A") + "</td></tr>" +
                "<tr><td style='padding: 8px; font-weight: bold; border: 1px solid #d4a373;'>Time:</td>" +
                "<td style='padding: 8px; border: 1px solid #d4a373;'>" + (appointmentTime != null ? appointmentTime : "N/A") + "</td></tr>" +
                "<tr><td style='padding: 8px; font-weight: bold; border: 1px solid #d4a373;'>Stylist:</td>" +
                "<td style='padding: 8px; border: 1px solid #d4a373;'>" + (stylist != null ? stylist : "Not assigned") + "</td></tr>" +
                "<tr><td style='padding: 8px; font-weight: bold; border: 1px solid #d4a373;'>Cancellation Reason:</td>" +
                "<td style='padding: 8px; border: 1px solid #d4a373;'>" + (cancellationReason != null ? cancellationReason : "Not provided") + "</td></tr>" +
                "</table>" +
                "<p style='text-align: center;'>We’re sorry to see you cancel. Book another appointment anytime!</p>" +
                "<p style='text-align: center;'><a href='http://your-salon-website.com/booking.jsp' style='color: #d4a373; text-decoration: none; font-weight: bold;'>Book Now</a></p>" +
                "<p style='text-align: center; font-size: 12px; color: #777;'>If you did not initiate this cancellation, please contact us immediately.</p>" +
                "<p style='text-align: center; font-size: 14px; color: #4b0082;'>Transform Your Beauty with Radiant Locks</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    public static String createRescheduleEmailBody(String serviceName, String appointmentDate, String appointmentTime, String stylist) {
        // Format time to user-friendly format (e.g., "10:00 AM")
        String formattedTime;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            java.util.Date time = inputFormat.parse(appointmentTime);
            formattedTime = outputFormat.format(time);
        } catch (Exception e) {
            formattedTime = appointmentTime; // Fallback to raw time if parsing fails
        }

        // Build email body
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear Customer,\n\n");
        emailBody.append("Your appointment has been successfully rescheduled. Here are the updated details:\n\n");
        emailBody.append("Service: ").append(serviceName).append("\n");
        emailBody.append("Date: ").append(appointmentDate).append("\n");
        emailBody.append("Time: ").append(formattedTime).append("\n");
        emailBody.append("Stylist: ").append(stylist).append("\n\n");
        emailBody.append("We look forward to seeing you! If you have any questions or need further assistance, please contact us at support@salon.com.\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("The Salon Team");

        return emailBody.toString();
    }

    
    // Asynchronous email sending (with attachment)
    public static void sendEmailAsync(String to, String subject, String body, String attachmentName,
            byte[] attachment) {
        executorService.submit(() -> {
            try {
                sendEmail(to, subject, body, attachmentName, attachment);
                LOGGER.info("Email with attachment sent successfully to: " + to);
            } catch (MessagingException e) {
                LOGGER.log(Level.SEVERE, "Failed to send email with attachment to: " + to, e);
            }
        });
    }
    // Shutdown the ExecutorService
    public static void shutdown() {
        executorService.shutdown();
    }
}
