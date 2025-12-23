package lk.exon.aethenosapi.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class PdfGenerater {

    public Context generateContext(String userName, String courseTitle, String instructor, String date, String totalLength, String genCertificateCode, boolean logo) throws IOException {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("courseTitle", courseTitle);
        context.setVariable("instructor", instructor);
        context.setVariable("date", date);
        context.setVariable("totalLength", totalLength);
        context.setVariable("certificateCode", genCertificateCode);
        context.setVariable("logo", logo);

        return context;
    }

    //    public byte[] htmlToPdf(String processedHtml) {
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            PdfWriter pdfWriter = new PdfWriter(outputStream);
//            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
//
//            ConverterProperties converterProperties = new ConverterProperties();
//
//            // Use an absolute path to your static resources directory
//            String baseUri = Paths.get("src/main/resources/static").toUri().toString();
//            converterProperties.setBaseUri(baseUri);
//
//            // Set the page size to A4 landscape
//            pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
//
//            // Convert HTML to PDF
//            Document document = HtmlConverter.convertToDocument(processedHtml, pdfDocument, converterProperties);
//            document.setMargins(0, 0, 0, 0);
//            document.close();
//
//            return outputStream.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    public byte[] htmlToPdf(String processedHtml) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.setDefaultPageSize(PageSize.A4.rotate()); // Landscape

            Document document = new Document(pdfDocument);
            document.setMargins(0, 0, 0, 0);

            ConverterProperties converterProperties = new ConverterProperties();
            String baseUri = Paths.get("src/main/resources/static").toUri().toString();
            converterProperties.setBaseUri(baseUri);

            HtmlConverter.convertToPdf(processedHtml, pdfDocument, converterProperties);

            document.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String encodePdfToBase64(byte[] pdfBytes) {
        return Base64.getEncoder().encodeToString(pdfBytes);
    }
}
