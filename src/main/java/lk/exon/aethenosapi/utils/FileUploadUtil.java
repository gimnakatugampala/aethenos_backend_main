package lk.exon.aethenosapi.utils;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.payload.response.FileUploadResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class FileUploadUtil {

    public static FileUploadResponse saveFile(MultipartFile file) throws IOException {
        try {
            String randomFilename = new Date().getTime() + "_" + UUID.randomUUID().toString().concat(".")
                    .concat(Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename())));
            Path image1SavePath = Paths.get(Config.UPLOAD_URL, randomFilename);
            Files.write(image1SavePath, file.getBytes());

            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setFilename(randomFilename);
            fileUploadResponse.setUrl(file.getOriginalFilename());
            return fileUploadResponse;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file", e);
        }
    }

    public static FileUploadResponse saveFile(MultipartFile file, String type) throws IOException {
        String savePath;
        File imageDirectory;
        if (type.equals("courses-images")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.IMAGES_UPLOAD_URL);

            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.COURSE_IMAGES_UPLOAD_URL);

            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            savePath = Config.COURSE_IMAGES_UPLOAD_URL;
        } else if (type.equals("profile-images")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.IMAGES_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PROFILE_IMAGES_UPLOAD_URL);

            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            savePath = Config.PROFILE_IMAGES_UPLOAD_URL;
        } else if (type.equals("promotional-video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PROMOTIONAL_VIDEO_UPLOAD_URL);

            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            savePath = Config.PROMOTIONAL_VIDEO_UPLOAD_URL;
        } else if (type.equals("downloadable-file")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.DOWNLOADABLE_FILE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.DOWNLOADABLE_FILE_UPLOAD_URL;
        } else if (type.equals("Source Code")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.SOURCE_CODE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.SOURCE_CODE_UPLOAD_URL;
        } else if (type.equals("Practice Test Question Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PRACTICE_TEST_DOCUMENTS_UPLOAD_PATH_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PRACTICE_TEST_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.PRACTICE_TEST_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL;
        } else if (type.equals("Practice Test Solution Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PRACTICE_TEST_DOCUMENTS_UPLOAD_PATH_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.PRACTICE_TEST_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.PRACTICE_TEST_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
        } else if (type.equals("Coding Video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_CODING_VIDEO_UPLOAD_URL;
        }else if (type.equals("Coding Resource")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_RESOURCES_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_CODING_RESOURCES_DOCUMENTS_UPLOAD_URL;
        }else if (type.equals("Coding Exercise Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_SHEET_DOCUMENTS_UPLOAD_URL;
        }else if (type.equals("Coding Exercise Video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_VIDEO_UPLOAD_URL;
        }else if (type.equals("Coding Solution Video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_SOLUTION_VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_SOLUTION_VIDEO_UPLOAD_URL;
        }else if (type.equals("Coding Solution Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.CODING_EXERCISE_CODING_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.CODING_EXERCISE_CODING_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
        }else if (type.equals("Assignment Video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.ASSIGNMENT_VIDEO_UPLOAD_URL;
        }else if (type.equals("Assignment Resource")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_RESOURCES_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.ASSIGNMENT_RESOURCES_DOCUMENTS_UPLOAD_URL;
        }else if (type.equals("Assignment Question Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENTS_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.ASSIGNMENTS_QUESTION_SHEET_DOCUMENTS_UPLOAD_URL;
        }else if (type.equals("Assignment Solution Video")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_SOLUTION_VIDEO_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.ASSIGNMENT_SOLUTION_VIDEO_UPLOAD_URL;
        }else if (type.equals("Assignment Solution Sheet")) {
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENT_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            imageDirectory = new File(Config.UPLOAD_URL + Config.ASSIGNMENTS_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            savePath = Config.ASSIGNMENTS_SOLUTION_SHEET_DOCUMENTS_UPLOAD_URL;
        } else {
            imageDirectory = new File(Config.UPLOAD_URL + Config.OTHER_UPLOAD_URL);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            savePath = Config.OTHER_UPLOAD_URL;
        }
        try {
//            String randomFilename = new Date().getTime() + "_" + UUID.randomUUID().toString().concat(".")

//            String randomFilename = FilenameUtils.getBaseName(file.getOriginalFilename()) + "_" + UUID.randomUUID().toString().concat(".")
//                    .concat(Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename())));

            String randomFilename = UUID.randomUUID().toString() + "_" + FilenameUtils.getBaseName(file.getOriginalFilename())
                                        + "." + FilenameUtils.getExtension(file.getOriginalFilename());


            Path image1SavePath = Paths.get(Config.UPLOAD_URL + savePath, randomFilename);
            Files.write(image1SavePath, file.getBytes());

            FileUploadResponse fileUploadResponse = new FileUploadResponse();
            fileUploadResponse.setFilename(savePath + randomFilename);
            fileUploadResponse.setUrl(file.getOriginalFilename());
            return fileUploadResponse;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file", e);
        }
    }

    public static FileUploadResponse deleteFile(String filename) {
        String targetPath = Config.UPLOAD_URL + filename;
        try {
            Path filePath = Paths.get(targetPath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);

                FileUploadResponse fileUploadResponse = new FileUploadResponse();
                fileUploadResponse.setFilename(filename);
                fileUploadResponse.setUrl(null);
                return fileUploadResponse;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete the file: " + filename, e);
        }
    }
}
