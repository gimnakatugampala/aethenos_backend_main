package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.StreamVideoUrlRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface StremingService {
    ResponseEntity<Resource> getResource(StreamVideoUrlRequest streamVideoUrlRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException;
}
