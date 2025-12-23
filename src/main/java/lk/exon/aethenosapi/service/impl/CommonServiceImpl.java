package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.Config;
import lk.exon.aethenosapi.config.EmailConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddVatRequest;
import lk.exon.aethenosapi.payload.request.Exceltobase64ByexcelFileNameRequest;
import lk.exon.aethenosapi.payload.request.SendEmailRequest;
import lk.exon.aethenosapi.payload.response.GetAdminDashboardCardsResponse;
import lk.exon.aethenosapi.payload.response.GetCourseAndInstructorDetailsResponse;
import lk.exon.aethenosapi.payload.response.GetVatResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.CommonService;
import lk.exon.aethenosapi.utils.EmailSender;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.*;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private VatRepository vatRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private ApprovalTypeRepository approvalTypeRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private CurriculumItemFileRepository curriculumItemFileRepository;

    SuccessResponse successResponse;

    @Override
    public String exceltobase64ByexcelFileName(Exceltobase64ByexcelFileNameRequest exceltobase64ByexcelFileNameRequest) {
        try {
            final String excelFileName = exceltobase64ByexcelFileNameRequest.getExcelFileName();
            Path filePath = Paths.get(Config.UPLOAD_URL, excelFileName);

            if (!(excelFileName.endsWith(".xls") || excelFileName.endsWith(".xlsx") || excelFileName.endsWith(".xlsm"))) {
                return "File is not an excel file";
            }

            byte[] fileContent = Files.readAllBytes(filePath);

            String base64String = Base64.getEncoder().encodeToString(fileContent);

            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Failed to read the file or file not found";
        }
    }

    @Override
    public List<GetVatResponse> getVat() {
        List<Vat> vats = vatRepository.findAll();
        List<GetVatResponse> getVatResponses = new ArrayList<>();
        for (Vat vat : vats) {
            GetVatResponse getVatResponse = new GetVatResponse();
            getVatResponse.setCountry(vat.getCountry());
            getVatResponse.setVat(vat.getVat());
            getVatResponses.add(getVatResponse);
        }

        return getVatResponses;
    }

    @Override
    public SuccessResponse updateVat(List<AddVatRequest> addVatRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 3) {
                if (profile.getIsActive() == 1) {

                    for (AddVatRequest addVatRequest : addVatRequests) {
                        Vat vat = vatRepository.getVatBycountry(addVatRequest.getCountry().toLowerCase());
                        if (vat != null) {
                            vat.setVat(addVatRequest.getVat());
                        } else {
                            vat = new Vat();
                            vat.setCountry(addVatRequest.getCountry());
                            vat.setVat(addVatRequest.getVat());
                        }
                        vatRepository.save(vat);
                    }
                    successResponse = new SuccessResponse();
                    successResponse.setMessage("vat updated successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;
                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("The user is not an administrator", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetAdminDashboardCardsResponse getAdminDashboardCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getGupType().getId() == 3) {
                if (profile.getIsActive() == 1) {

                    GupType gupType = gupTypeRepository.getGupTypeById(2);

                    GetAdminDashboardCardsResponse getAdminDashboardCardsResponse = new GetAdminDashboardCardsResponse();

                    List<GeneralUserProfile> generalUserProfiles = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                    getAdminDashboardCardsResponse.setInstructorsCount(generalUserProfiles.size());
                    gupType = gupTypeRepository.getGupTypeById(1);
                    generalUserProfiles = generalUserProfileRepository.getGeneralUserProfileByGupType(gupType);
                    getAdminDashboardCardsResponse.setStudentsCount(generalUserProfiles.size());

                    ApprovalType approvalType = approvalTypeRepository.getApprovalTypeById(1);
                    List<Course> courses = courseRepository.getCourseByApprovalType(approvalType);
                    getAdminDashboardCardsResponse.setDraftCoursesCount(courses.size());
                    approvalType = approvalTypeRepository.getApprovalTypeById(5);
                    courses = courseRepository.getCourseByApprovalType(approvalType);
                    getAdminDashboardCardsResponse.setCoursesSubmissionsCount(courses.size());

                    return getAdminDashboardCardsResponse;

                } else {
                    throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("The user is not an administrator", VarList.RSP_NO_DATA_FOUND);
            }

        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetCourseAndInstructorDetailsResponse> searchCourseAndInstructorDetails(String keyword) {
        List<GetCourseAndInstructorDetailsResponse> responses = new ArrayList<>();

        // Search for courses by partial match in title
        List<Course> courses = courseRepository.findCoursesByCourseTitleContainingIgnoreCase(keyword);
        if (!courses.isEmpty()) {
            loadGetCourseDetailsResponse(responses, courses, "course");
        }

        // Search for instructors by partial match in first or last name
        List<InstructorProfile> instructorProfiles = instructorProfileRepository
                .findInstructorProfilesByGeneralUserProfile_FirstNameContainingIgnoreCaseOrGeneralUserProfile_LastNameContainingIgnoreCase(keyword, keyword);

        if (!instructorProfiles.isEmpty()) {
            loadGetInstructorDetailsResponse(responses, instructorProfiles, "Instructor");
        }

        return responses;
    }


    @Override
    public SuccessResponse sendEmail(SendEmailRequest sendEmailRequest) {
        final String senderEmail = sendEmailRequest.getSenderEmail();
        final String email = sendEmailRequest.getEmail();
        final String name = sendEmailRequest.getName();
        final String subject = sendEmailRequest.getSubject();
        final String message = sendEmailRequest.getMessage();
        final String whoAreYou = sendEmailRequest.getWhoAreYou();
        final MultipartFile attachment = sendEmailRequest.getAttachment();

        if (senderEmail == null || senderEmail.isEmpty() || email == null || email.isEmpty() || name == null || name.isEmpty() || subject == null || subject.isEmpty() || message == null || message.isEmpty())
            throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
        Properties properties = EmailConfig.getEmailProperties(name, subject);
        properties.put("message", message);
        properties.put("email", email);
        properties.put("name", name);
        if (whoAreYou != null && !whoAreYou.isEmpty()) {
            properties.put("whoAreYou", whoAreYou);
        }
        try {
            EmailSender emailSender = new EmailSender();
            emailSender.sendCustomEmail(senderEmail, (String) properties.getProperty("from"), subject, properties, attachment);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        successResponse = new SuccessResponse();
        successResponse.setMessage("Email sent successfully");
        successResponse.setVariable(VarList.RSP_SUCCESS);
        return successResponse;
    }

       private List<GetCourseAndInstructorDetailsResponse> loadGetCourseDetailsResponse(List<GetCourseAndInstructorDetailsResponse> getCourseAndInstructorDetailsResponses, List<Course> courses, String searchType) {
        for (Course course : courses) {
            if (course.getApprovalType().getId() == 5) {
                GetCourseAndInstructorDetailsResponse getCourseAndInstructorDetailsResponse = new GetCourseAndInstructorDetailsResponse();
                getCourseAndInstructorDetailsResponse.setCourseCode(course.getCode());
                getCourseAndInstructorDetailsResponse.setSearchType(searchType);
                getCourseAndInstructorDetailsResponse.setCourseTitle(course.getCourseTitle());
                getCourseAndInstructorDetailsResponse.setCourseImg(course.getImg());
                getCourseAndInstructorDetailsResponse.setInstructorName(course.getInstructorId().getGeneralUserProfile().getFirstName() + " " + course.getInstructorId().getGeneralUserProfile().getLastName());
                getCourseAndInstructorDetailsResponse.setInstructorCode(course.getInstructorId().getGeneralUserProfile().getUserCode());
                getCourseAndInstructorDetailsResponse.setInstructorImg(course.getInstructorId().getGeneralUserProfile().getProfileImg());
                getCourseAndInstructorDetailsResponses.add(getCourseAndInstructorDetailsResponse);
            }
        }
        return getCourseAndInstructorDetailsResponses;
    }

    private List<GetCourseAndInstructorDetailsResponse> loadGetInstructorDetailsResponse(List<GetCourseAndInstructorDetailsResponse> getCourseAndInstructorDetailsResponses, List<InstructorProfile> instructorProfiles, String searchType) {
        for (InstructorProfile instructorProfile : instructorProfiles) {
            GetCourseAndInstructorDetailsResponse getCourseAndInstructorDetailsResponse = new GetCourseAndInstructorDetailsResponse();
            getCourseAndInstructorDetailsResponse.setCourseCode("");
            getCourseAndInstructorDetailsResponse.setSearchType(searchType);
            getCourseAndInstructorDetailsResponse.setCourseTitle("");
            getCourseAndInstructorDetailsResponse.setCourseImg("");
            getCourseAndInstructorDetailsResponse.setInstructorName(instructorProfile.getGeneralUserProfile().getFirstName() + " " + instructorProfile.getGeneralUserProfile().getLastName());
            getCourseAndInstructorDetailsResponse.setInstructorCode(instructorProfile.getGeneralUserProfile().getUserCode());
            getCourseAndInstructorDetailsResponse.setInstructorImg(instructorProfile.getGeneralUserProfile().getProfileImg());
            getCourseAndInstructorDetailsResponses.add(getCourseAndInstructorDetailsResponse);
        }
        return getCourseAndInstructorDetailsResponses;
    }
    @Override
    public ResponseEntity<Resource> downloadFile(String filePath) {
        try {
            // Query database to get title based on the filePath (URL)
            CurriculumItemFile curriculumItemFile = curriculumItemFileRepository.getCurriculumItemFileByUrl(filePath);
            if (curriculumItemFile == null) {
                throw new RuntimeException("File not found in database: " + filePath);
            }

            // Construct the full path based on the filePath
            Path fileStorageLocation = Paths.get(Config.UPLOAD_URL).resolve(filePath).normalize();
            Resource resource = new UrlResource(fileStorageLocation.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found on server: " + filePath);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + curriculumItemFile.getTitle() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

}
