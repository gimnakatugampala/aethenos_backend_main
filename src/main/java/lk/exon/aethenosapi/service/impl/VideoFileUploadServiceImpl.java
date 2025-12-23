package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.payload.response.ApiResponse;
import lk.exon.aethenosapi.service.VideoFileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


@Service
public class VideoFileUploadServiceImpl implements VideoFileUploadService {
    @Override
    public ApiResponse saveFileChunk(MultipartFile fileObj, String index, String totalChunks, String fileName, String type) {
        boolean status = false;
        String msg = "";

        File directory = new File(Config.UPLOAD_URL + Config.VIDEO_UPLOAD_URL);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            String folderName = fileName.replace('.', 'V');
            String savePath;
            if (type != null && !type.isEmpty() && type.equals("test-video")) {
                savePath = Config.UPLOAD_URL + Config.TEST_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("lesson-video")) {
                savePath = Config.UPLOAD_URL + Config.LESSON_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("promotional-videos")) {
                savePath = Config.UPLOAD_URL + Config.PROMOTIONAL_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("downloadable-file")) {
                savePath = Config.UPLOAD_URL + Config.DOWNLOADABLE_FILE_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("source-code")) {
                savePath = Config.UPLOAD_URL + Config.SOURCE_CODE_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("practice-test-question-sheet")) {
                savePath = Config.UPLOAD_URL + Config.PRACTICE_TEST_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("practice-test-solution-sheet")) {
                savePath = Config.UPLOAD_URL + Config.PRACTICE_TEST_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("coding-video")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("coding-resource")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_RESOURCES_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            }  else if (type != null && !type.isEmpty() && type.equals("coding-exercise-sheet")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("coding-exercise-video")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("coding-solution-video")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_SOLUTION_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            }  else if (type != null && !type.isEmpty() && type.equals("coding-solution-sheet")) {
                savePath = Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            } else if (type != null && !type.isEmpty() && type.equals("assignment-video")) {
                savePath = Config.UPLOAD_URL + Config.ASSIGNMENT_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            }else if (type != null && !type.isEmpty() && type.equals("assignment-resource")) {
                savePath = Config.UPLOAD_URL + Config.ASSIGNMENT_RESOURCES_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            }else if (type != null && !type.isEmpty() && type.equals("assignment-question-sheet")) {
                savePath = Config.UPLOAD_URL + Config.ASSIGNMENTS_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            }else if (type != null && !type.isEmpty() && type.equals("assignment-solution-video")) {
                savePath = Config.UPLOAD_URL + Config.ASSIGNMENT_SOLUTION_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            }else if (type != null && !type.isEmpty() && type.equals("assignment-solution-sheet")) {
                savePath = Config.UPLOAD_URL + Config.ASSIGNMENTS_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
                directory = new File(savePath);
            } else {
                savePath = Config.UPLOAD_URL + Config.LESSON_VIDEO_UPLOAD_URL;
                directory = new File(savePath);
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }


            // Create folder if it doesn't exist
            File f1 = new File(savePath + folderName);


            if (!f1.exists()) {
                f1.mkdir();
            }

            File file = new File(savePath + folderName + "/" + index);

//            System.out.println(index + "/" + totalChunks + " " + fileName + " " + file1.getSize());

            // Transfer data from the input stream to the file output stream
            try (InputStream is = fileObj.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(is);
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192]; // You can adjust the buffer size according to your needs
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle or log the exception appropriately
                status = false;
                msg = "Failed to process the file.";
            }

            // Delete temporary files and append if necessary
            if (f1.listFiles().length == Integer.parseInt(totalChunks)) {
                System.out.println("Appending...." + f1.listFiles().length);
                File finalFile = new File(savePath + fileName);

                try (FileOutputStream fos2 = new FileOutputStream(finalFile)) {
                    for (int i = 1; i <= Integer.parseInt(totalChunks); i++) {
                        File chunk = new File(savePath + folderName + "/" + i);
                        try (InputStream is2 = new FileInputStream(chunk);
                             BufferedInputStream bis2 = new BufferedInputStream(is2)) {

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = bis2.read(buffer)) != -1) {
                                fos2.write(buffer, 0, bytesRead);
                            }
                        }
                        if (!chunk.delete()) {
                            System.out.println("Failed to delete chunk: " + chunk.getAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Handle or log the exception appropriately
                    status = false;
                    msg = "Failed to process the file chunks.";
                }

                if (!f1.delete()) {
                    System.out.println("Failed to delete folder: " + f1.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            status = false;
            msg = e.getMessage();
            e.printStackTrace();
        }

        return new ApiResponse(status, msg);
    }
}
