package lk.exon.aethenosapi.utils;

import lk.exon.aethenosapi.config.EmailConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailSender implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final Map<String, String> templateMapping;

    static {
        templateMapping = new HashMap<>();
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
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    private SpringTemplateEngine getTemplateEngine() {
        if (context != null) {
            return context.getBean(SpringTemplateEngine.class);
        }
        throw new IllegalStateException("SpringTemplateEngine not available");
    }

    private JavaMailSender getJavaMailSender() {
        if (context != null) {
            return context.getBean(JavaMailSender.class);
        }
        throw new IllegalStateException("JavaMailSender not available");
    }

    public void sendEmail(String type, String to, String from, String subject, Properties properties)
            throws MessagingException {
        String templateName = templateMapping.get(type);
        if (templateName == null) {
            throw new MessagingException("Unknown email type: " + type);
        }
        Context ctx = new Context();
        properties.forEach((k, v) -> ctx.setVariable(k.toString(), v));
        String htmlContent = getTemplateEngine().process(templateName, ctx);
        if (htmlContent == null) {
            throw new MessagingException("Failed to generate HTML content for email type: " + type);
        }
        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        getJavaMailSender().send(message);
    }

    public void sendEmail(String type, String to, String from, String subject, Properties properties, byte[] pdfBytes)
            throws MessagingException {
        String templateName = templateMapping.get(type);
        if (templateName == null) {
            throw new MessagingException("Unknown email type: " + type);
        }
        Context ctx = new Context();
        properties.forEach((k, v) -> ctx.setVariable(k.toString(), v));
        String htmlContent = getTemplateEngine().process(templateName, ctx);
        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        if (pdfBytes != null && pdfBytes.length > 0) {
            helper.addAttachment("Certificate.pdf", new ByteArrayResource(pdfBytes), "application/pdf");
        }
        getJavaMailSender().send(message);
    }

    public void sendCustomEmail(String to, String from, String subject, Properties properties, MultipartFile attachment)
            throws MessagingException {
        Context ctx = new Context();
        properties.forEach((k, v) -> ctx.setVariable(k.toString(), v));
        String htmlContent = getTemplateEngine().process("SendCustomEmailMessage", ctx);
        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        if (attachment != null && !attachment.isEmpty()) {
            helper.addAttachment(attachment.getOriginalFilename(), attachment);
        }
        getJavaMailSender().send(message);
    }
}