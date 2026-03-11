package lk.exon.aethenosapi.config;


import lk.exon.aethenosapi.entity.Course;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class EmailConfig {

    public static Properties getEmailProperties(String transactionDate, String transactionNumber, List<String> courseNames, String subTotal, String taxRate, String tax, String total, String purchasedBy, String paymentMethod) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("template_name", "VerificationEmailBody");
        properties.setProperty("seller_otp", "SellerOtpEmail");

        properties.put("template_name", "email-invoice-template.ftl");
        properties.setProperty("subject", "Aethenos order confirmation");

        //parameters for the invoice
        properties.setProperty("transactionDate", transactionDate);
        properties.setProperty("transactionNumber", transactionNumber);
        properties.put("courseNames", courseNames);
        properties.setProperty("subTotal", subTotal);
        properties.setProperty("taxRate", taxRate);
        properties.setProperty("tax", tax);
        properties.setProperty("total", total);
        properties.setProperty("purchasedBy", purchasedBy);
        properties.setProperty("paymentMethod", paymentMethod);

        return properties;
    }

    public static Properties getEmailProperties(String userName, String subject) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("subject", subject);

        properties.setProperty("userName", userName);

        return properties;
    }

    public static Properties getVerificationEmailProperties(String userName, String subject, String verificationCode) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("subject", subject);

        properties.setProperty("userName", userName);
        properties.setProperty("verificationCode", verificationCode);

        return properties;
    }


    public static Properties getEmailProperties(String title, String subject, String content) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("subject", subject);

        properties.setProperty("title", title);
        properties.setProperty("content", content);

        return properties;
    }

    public static Properties getEmailProperties(String InstructorUserName, String studentName, List<Course> courses, String subject) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("subject", subject);

        properties.setProperty("InstructorUserName", InstructorUserName);
        properties.setProperty("studentName", studentName);
        String combinedCourseNames = courses.stream()
                .map(Course::getCourseTitle)
                .collect(Collectors.joining(", "));
        boolean isPaidCourse = courses.stream()
                .anyMatch(course -> course.getIsPaid() == 2);
        properties.setProperty("courseName", combinedCourseNames);
        properties.setProperty("isPaidCourse", String.valueOf(isPaidCourse));
        properties.setProperty("courseName", combinedCourseNames);

        return properties;
    }

    public static Properties getEmailProperties(String userName, String courseTitle, String instructor, String date, Double length, String base64Pdf, String subject) {
        Properties properties = new Properties();
        properties.setProperty("from", Config.SEND_EMAIL);
        properties.setProperty("subject", subject);
        properties.setProperty("courseTitle", courseTitle);
        properties.setProperty("instructor", instructor);
        properties.setProperty("date", date);
        properties.setProperty("totalLengths", length.toString());
        properties.setProperty("base64Pdf", base64Pdf);


        properties.setProperty("userName", userName);

        return properties;
    }
}