package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoFileUploadService {
    ApiResponse saveFileChunk(MultipartFile file, String index, String totalChunks, String fileName,String type);
}
