package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.payload.request.StreamVideoUrlRequest;
import lk.exon.aethenosapi.service.StremingService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Service
public class StremingServiceImpl implements StremingService {

    @Override
    public ResponseEntity<Resource> getResource(StreamVideoUrlRequest streamVideoUrlRequest,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        final String videoPath = Config.UPLOAD_URL + streamVideoUrlRequest.getUrl();
//        final String videoPath = Config.UPLOAD_URL + Config.TEST_VIDEO_UPLOAD_URL + streamVideoUrlRequest.getUrl();
        File videoFile = new File(videoPath);

        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        long length = videoFile.length();
        long start = 0;
        long end = length - 1;
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);

        if (rangeHeader != null) {
            List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
            HttpRange range = ranges.get(0);
            start = range.getRangeStart(length);
            end = range.getRangeEnd(length);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }

        long contentLength = end - start + 1;
        String contentRange = "bytes " + start + "-" + end + "/" + length;

        response.setContentType("video/mp4");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoFile.getName() + "\"");
        response.setHeader("X-Content-Type-Options", "nosniff");

        try (InputStream inputStream = new FileInputStream(videoFile);
             OutputStream outputStream = response.getOutputStream()) {

            inputStream.skip(start);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        }

        Resource resource = new InputStreamResource(new FileInputStream(videoFile));
        return ResponseEntity.ok().body(resource);
    }
}
