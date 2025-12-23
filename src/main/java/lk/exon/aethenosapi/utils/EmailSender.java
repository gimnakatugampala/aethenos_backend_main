package lk.exon.aethenosapi.utils;

import com.sendgrid.*;
import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailSender {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public boolean sendEmailWithTemplate(String to, String from, String subject, String template_name, Properties properties) {

        String bodyContent;

        bodyContent = processEmailTemplate(template_name, properties);

        // new code
        Email email_from = new Email(from);
        Email email_to = new Email(to);
        Content content = new Content("text/html", bodyContent);
        Mail mail = new Mail(email_from, subject, email_to, content);
        SendGrid sg = new SendGrid(Config.SEND_EMAIL_API_KEY);
        Request request = new Request();
        try {
            System.out.println("-------- " + to + " subject  " + subject + "emailBody-- " + bodyContent);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email sent: " + response.getStatusCode());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to send email");
            return false;
        }
    }

    private String processEmailTemplate(String templateName, Properties properties) {
        Context context = new Context();
        properties.keySet().forEach(key -> context.setVariable(key.toString(), properties.get(key)));
        templateEngine = new SpringTemplateEngine();
        return templateEngine.process(templateName, context);
    }

    public void sendEmail(String type, String to, String from, String subject, Properties properties) throws MessagingException {

        Context context = new Context();
        properties.keySet().forEach(key -> context.setVariable(key.toString(), properties.get(key)));
        templateEngine = (SpringTemplateEngine) EmailConfig.springTemplateEngine();
        Map<String, String> templateMapping = new HashMap<>();
        templateMapping.put("UserRegistrationCompletedMessage", "UserRegistrationCompletedMessage");
        templateMapping.put("CourseCreationSuccessfulMessage", "CreateCourseMessage");
        templateMapping.put("PurchasedReciept", "PurchasedReciept");
        templateMapping.put("SubmitForReviewMessage", "SubmitForReviewMessage");
        templateMapping.put("DraftCourseApproveMessage", "DraftCourseApproveMessage");
        templateMapping.put("DraftCourseDisapproveMessage", "DraftCourseDisapproveMessage");
        templateMapping.put("CourseApproveMessage", "CourseApproveMessage");
        templateMapping.put("CourseDisapproveMessage", "CourseDisapproveMessage");
        templateMapping.put("NotifyInstructorStudentPurchases", "NotifyInstructorStudentPurchases");
        templateMapping.put("StudentCourseCompleteMessage", "StudentCourseCompleteMessage");
        templateMapping.put("RefundApproveMessage", "RefundApproveMessage");
        templateMapping.put("AnnouncementMessage", "AnnouncementMessage");
        templateMapping.put("ForgotPasswordVerificationCodeSendMessage", "ForgotPasswordVerificationCodeSendMessage");
        templateMapping.put("TestVideoSubmissionForAdminReviewMessage", "TestVideoSubmissionForAdminReviewMessage");
        templateMapping.put("TestVideoSentForReviewMessage", "TestVideoSentForReviewMessage");
        templateMapping.put("CourseSubmissionForReviewMessage", "CourseSubmissionForReviewMessage");
        templateMapping.put("NewStudentQuestionMessage", "NewStudentQuestionMessage");
        templateMapping.put("AddAnswerMessage", "AddAnswerMessage");
        templateMapping.put("NewCourseReviewSubmittedMessage", "NewCourseReviewSubmittedMessage");
        templateMapping.put("NewMessageNotification", "NewMessageNotification");
        templateMapping.put("EmailVerificationMessage", "EmailVerificationMessage");
        templateMapping.put("RefundRequestForCourseMessage", "RefundRequestForCourseMessage");
        templateMapping.put("EmailToStudentFromAdminMessage", "EmailToStudentFromAdminMessage");
        templateMapping.put("EmailToInstructorFromAdminMessage", "EmailToInstructorFromAdminMessage");
        templateMapping.put("RefundRequestRejectedForCourseMessage", "RefundRequestRejectedForCourseMessage");

        String templateName = templateMapping.get(type);
        if (templateName == null) {
            throw new MessagingException("Unknown email type: " + type);
        }
        String htmlContent = templateEngine.process(templateName, context);
        if (htmlContent == null) {
            throw new MessagingException("Failed to generate HTML content for email type: " + type);
        }

        Email email_from = new Email(Config.SEND_EMAIL);
        Email email_to = new Email(to);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail(email_from, subject, email_to, content);
        SendGrid sg = new SendGrid(Config.SEND_EMAIL_API_KEY);
        Request request = new Request();
        try {
            System.out.println("-------- " + to + " subject  " + subject + "emailBody-- " + htmlContent);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Send");
            System.out.println("Sent Automated Incident Email");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
            System.out.println("Failed to Send Automated Incident Email");
        }
    }

    public void sendEmail(String type, String to, String from, String subject, Properties properties, byte[] pdfBytes) throws MessagingException {

        // Set up email context with properties
        Context context = new Context();
        properties.keySet().forEach(key -> context.setVariable(key.toString(), properties.get(key)));

        // Initialize the template engine and process the email template
        templateEngine = (SpringTemplateEngine) EmailConfig.springTemplateEngine();
        String htmlContent = templateEngine.process("StudentCourseCompleteMessage", context);

        // Validate email content
        if (htmlContent == null) {
            throw new MessagingException("Failed to generate HTML content for email type: " + type);
        }

        // Create the email sender and recipient
        Email email_from = new Email(Config.SEND_EMAIL);
        Email email_to = new Email(to);
        Content content = new Content("text/html", htmlContent);

        // Create the mail object
        Mail mail = new Mail(email_from, subject, email_to, content);

        // Add PDF attachment
        if (pdfBytes != null && pdfBytes.length > 0) {
            Attachments attachment = new Attachments();
            attachment.setFilename("Certificate.pdf");
            attachment.setType("application/pdf");
            attachment.setDisposition("attachment");
            attachment.setContent(Base64.getEncoder().encodeToString(pdfBytes));  // Encode the byte array to Base64
            mail.addAttachments(attachment);  // Add the attachment to the mail
        }

        // Send the email via SendGrid
        SendGrid sg = new SendGrid(Config.SEND_EMAIL_API_KEY);
        Request request = new Request();
        try {
            System.out.println("-------- " + to + " subject  " + subject + " emailBody -- " + htmlContent);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Send");
            System.out.println("Sent Automated Incident Email with attachment");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
            System.out.println("Failed to Send Automated Incident Email with attachment");
        }
    }

    public void sendCustomEmail(String to, String from, String subject, Properties properties, MultipartFile attachment) throws MessagingException {
        // Set up email context with properties
        Context context = new Context();
        properties.keySet().forEach(key -> context.setVariable(key.toString(), properties.get(key)));

        // Initialize the template engine and process the email template
        templateEngine = (SpringTemplateEngine) EmailConfig.springTemplateEngine();
        String htmlContent = templateEngine.process("SendCustomEmailMessage", context);

        // Create the email sender and recipient
        Email email_from = new Email(Config.SEND_EMAIL);
        Email email_to = new Email(to);
        Content content = new Content("text/html", htmlContent);

        // Create the mail object
        Mail mail = new Mail(email_from, subject, email_to, content);

        // Add PDF attachment (if any)
        if (attachment != null && !attachment.isEmpty()) {
            Attachments attachmentFile = new Attachments();

            // Set the filename (ensure it has the correct extension)
            String filename = attachment.getOriginalFilename();  // Use the original filename

            attachmentFile.setFilename(filename);
            attachmentFile.setType(attachment.getContentType());
            attachmentFile.setDisposition("attachment");
            attachmentFile.setContentId("Document");

            // Convert the attachment to Base64 (SendGrid requires this)
            try {
                byte[] fileContent = attachment.getBytes();
                String encodedContent = Base64.getEncoder().encodeToString(fileContent);
                attachmentFile.setContent(encodedContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Add the attachment to the mail
            mail.addAttachments(attachmentFile);
        }

        // Send the email via SendGrid
        SendGrid sg = new SendGrid(Config.SEND_EMAIL_API_KEY);
        Request request = new Request();
        try {
            System.out.println("-------- " + to + " subject  " + subject + " emailBody -- " + htmlContent);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Email sent successfully with response code: " + response.getStatusCode());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to send email");
        }
    }

}