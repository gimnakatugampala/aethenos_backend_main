package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.StreamVideoUrlRequest;
import lk.exon.aethenosapi.service.StremingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/videoStreming")
public class VideoStremingController {

    @Autowired
    private StremingService stremingService;

    @GetMapping(value = "/video")
    public ResponseEntity<Resource> streamVideo(
            StreamVideoUrlRequest streamVideoUrlRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        return stremingService.getResource(streamVideoUrlRequest, request, response);
    }
}
