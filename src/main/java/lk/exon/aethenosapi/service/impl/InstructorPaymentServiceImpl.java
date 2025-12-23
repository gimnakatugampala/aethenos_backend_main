package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddInstructorPaymentDetailsRequest;
import lk.exon.aethenosapi.payload.request.UpdateInstructorTermsAgreeRequest;
import lk.exon.aethenosapi.payload.response.CheckAllInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.GetInstructorPaymentDetailsResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.InstructorPaymentService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InstructorPaymentServiceImpl implements InstructorPaymentService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private InstructorPaymentsRepository instructorPaymentsRepository;
    @Autowired
    private InstructorProfileRepository instructorProfileRepository;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private CourseRepository courseRepository;

    private SuccessResponse successResponse = new SuccessResponse();

    @Override
    public SuccessResponse addInstructorPaymentDetails(AddInstructorPaymentDetailsRequest addInstructorPaymentDetailsRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {
                    String paypalUserName = addInstructorPaymentDetailsRequest.getPaypalUserName();
                    String paypalEmail = addInstructorPaymentDetailsRequest.getPaypalEmail();
                    String payoneerUserName = addInstructorPaymentDetailsRequest.getPayoneerUserName();
                    String payoneerEmail = addInstructorPaymentDetailsRequest.getPayoneerEmail();
                    String accountNumber = addInstructorPaymentDetailsRequest.getAccountNumber();
                    String sort1 = addInstructorPaymentDetailsRequest.getSort1();
                    String sort2 = addInstructorPaymentDetailsRequest.getSort2();
                    String sort3 = addInstructorPaymentDetailsRequest.getSort3();
                    String bankAccountName = addInstructorPaymentDetailsRequest.getBankAccountName();
                    Integer paymentMethodId = addInstructorPaymentDetailsRequest.getPaymentMethodId();

                    boolean isPaypalValid = (paypalUserName == null || paypalUserName.isEmpty()) || (paypalEmail == null || paypalEmail.isEmpty());
                    boolean isPayoneerValid = (payoneerUserName == null || payoneerUserName.isEmpty()) || (payoneerEmail == null || payoneerEmail.isEmpty());
                    boolean isUkBankValid = (accountNumber == null || accountNumber.isEmpty()) || (sort1 == null || sort1.isEmpty()) || (sort2 == null || sort2.isEmpty()) || (sort3 == null || sort3.isEmpty());

                    if (paymentMethodId == null || paymentMethodId.toString().isEmpty()) {
                        throw new ErrorException("Invalid request: Payment method id is invalid", VarList.RSP_NO_DATA_FOUND);
                    }
                    if (isPaypalValid) {
                        if (isPayoneerValid && isUkBankValid) {
                            throw new ErrorException("Invalid request: Both PayPal, Payoneer and uk bank details are invalid", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else if (isPayoneerValid) {
                        if (isPaypalValid && isUkBankValid) {
                            throw new ErrorException("Invalid request: Both PayPal, Payoneer and uk bank details are invalid", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else if (isUkBankValid) {
                        if (isPaypalValid && isPayoneerValid) {
                            throw new ErrorException("Invalid request: Both PayPal, Payoneer and uk bank details are invalid", VarList.RSP_NO_DATA_FOUND);
                        }
                    }
                    if (isPaypalValid || isPayoneerValid || isUkBankValid) {
                        if (isPaypalValid) {
                            if (!((paypalUserName == null || paypalUserName.isEmpty()) && (paypalEmail == null || paypalEmail.isEmpty()))) {
                                throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                        if (isPayoneerValid) {
                            if (!((payoneerUserName == null || payoneerUserName.isEmpty()) && (payoneerEmail == null || payoneerEmail.isEmpty()))) {
                                throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                        if (isUkBankValid) {
                            if (!((accountNumber == null || accountNumber.toString().isEmpty()) && (sort1 == null || sort1.toString().isEmpty()) && (sort2 == null || sort2.toString().isEmpty()) && (sort3 == null || sort3.toString().isEmpty()) && (bankAccountName == null || bankAccountName.isEmpty()))) {
                                throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);
                            }
                        }
                    }

                    if (paymentMethodId == 2) {
                        if (isPaypalValid) {
                            throw new ErrorException("Please add paypal payment method details", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else if (paymentMethodId == 4) {
                        if (isPayoneerValid) {
                            throw new ErrorException("Please add payoneer payment method details", VarList.RSP_NO_DATA_FOUND);
                        }
                    } else if (paymentMethodId == 5) {
                        if (isUkBankValid) {
                            throw new ErrorException("Please add uk bank payment method details", VarList.RSP_NO_DATA_FOUND);
                        }
                    }

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile == null) {
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
                    }

                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                    if (instructorPayments == null) {
                        instructorPayments = new InstructorPayments();
                        instructorPayments.setInstructorProfile(instructorProfile);
                    }
                    successResponse.setMessage("Instructor payment details added successfully");
                    PaymentMethod paymentMethod = paymentMethodRepository.getPaymentMethodById(paymentMethodId);
                    instructorPayments.setPaypalUserName(paypalUserName);
                    instructorPayments.setPaypalEmail(paypalEmail);
                    instructorPayments.setPayoneerUserName(payoneerUserName);
                    instructorPayments.setPayoneerEmail(payoneerEmail);
                    instructorPayments.setAccountNumber((accountNumber == null || accountNumber.isEmpty()) ? "" : accountNumber);
                    instructorPayments.setSort1((sort1 == null || sort1.isEmpty()) ? "" : sort1);
                    instructorPayments.setSort2((sort2 == null || sort2.isEmpty()) ? "" : sort2);
                    instructorPayments.setSort3((sort3 == null || sort3.isEmpty()) ? "" : sort3);
                    instructorPayments.setBankAccountName((bankAccountName == null || bankAccountName.isEmpty()) ? "" : bankAccountName);
                    instructorPayments.setPaymentMethod(paymentMethod);

                    instructorPaymentsRepository.save(instructorPayments);

                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetInstructorPaymentDetailsResponse getInstructorPaymentDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile == null) {
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);
                    }

                    InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                    GetInstructorPaymentDetailsResponse getInstructorPaymentDetailsResponse = new GetInstructorPaymentDetailsResponse();
                    getInstructorPaymentDetailsResponse.setPaypalUserName("");
                    getInstructorPaymentDetailsResponse.setPaypalEmail("");
                    getInstructorPaymentDetailsResponse.setPayoneerUserName("");
                    getInstructorPaymentDetailsResponse.setPayoneerEmail("");
                    getInstructorPaymentDetailsResponse.setAccountNumber("");
                    getInstructorPaymentDetailsResponse.setSort1("");
                    getInstructorPaymentDetailsResponse.setSort2("");
                    getInstructorPaymentDetailsResponse.setSort3("");
                    getInstructorPaymentDetailsResponse.setBankAccountName("");
                    getInstructorPaymentDetailsResponse.setSelected("");
                    if (instructorPayments != null) {
                        getInstructorPaymentDetailsResponse.setPaypalUserName(instructorPayments.getPaypalUserName() == null || instructorPayments.getPaypalUserName().isEmpty() ? "" : instructorPayments.getPaypalUserName());
                        getInstructorPaymentDetailsResponse.setPaypalEmail(instructorPayments.getPaypalEmail() == null || instructorPayments.getPaypalEmail().isEmpty() ? "" : instructorPayments.getPaypalEmail());
                        getInstructorPaymentDetailsResponse.setPayoneerUserName(instructorPayments.getPayoneerUserName() == null || instructorPayments.getPayoneerUserName().isEmpty() ? "" : instructorPayments.getPayoneerUserName());
                        getInstructorPaymentDetailsResponse.setPayoneerEmail(instructorPayments.getPayoneerEmail() == null || instructorPayments.getPayoneerEmail().isEmpty() ? "" : instructorPayments.getPayoneerEmail());
                        getInstructorPaymentDetailsResponse.setAccountNumber(instructorPayments.getAccountNumber());
                        getInstructorPaymentDetailsResponse.setSort1(instructorPayments.getSort1());
                        getInstructorPaymentDetailsResponse.setSort2(instructorPayments.getSort2());
                        getInstructorPaymentDetailsResponse.setSort3(instructorPayments.getSort3());
                        getInstructorPaymentDetailsResponse.setBankAccountName(instructorPayments.getBankAccountName());
                        getInstructorPaymentDetailsResponse.setSelected((instructorPayments.getPaymentMethod().getId() == 6) ? "" : instructorPayments.getPaymentMethod().getMethod());

                    }


                    return getInstructorPaymentDetailsResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse updateInstructorTermsAgree(UpdateInstructorTermsAgreeRequest updateInstructorTermsAgreeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    final String courseCode = updateInstructorTermsAgreeRequest.getCourseCode();

                    if (courseCode == null || courseCode.isEmpty())
                        throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile == null)
                        throw new ErrorException("Instructor profile not found", VarList.RSP_NO_DATA_FOUND);

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().equals(instructorProfile))
                        throw new ErrorException("You cannot do this process because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    course.setInstructorTerms((byte) 1);
                    courseRepository.save(course);

                    successResponse.setMessage("Successfully agreed with instructor terms");
                    successResponse.setVariable(VarList.RSP_SUCCESS);
                    return successResponse;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public CheckAllInstructorPaymentDetailsResponse checkAllInstructorPaymentDetailsComplete(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot do this process because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    CheckAllInstructorPaymentDetailsResponse checkAllInstructorPaymentDetailsResponse = new CheckAllInstructorPaymentDetailsResponse();

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    boolean isComplete = false;
                    if (course.getIsPaid() == 1) {
                        checkAllInstructorPaymentDetailsResponse.setPaidCourse(false);

                        if (instructorProfile != null && instructorProfile.getIsProfileCompleted() == 1) {
                            if (course.getInstructorTerms() != null && course.getInstructorTerms() == 1)
                                isComplete = true;
                        }
                    } else {
                        checkAllInstructorPaymentDetailsResponse.setPaidCourse(true);
                        if (instructorProfile != null && instructorProfile.getIsProfileCompleted() == 1) {
                            InstructorPayments instructorPayments = instructorPaymentsRepository.getInstructorPaymentsByInstructorProfile(instructorProfile);
                            if (instructorPayments != null) {
                                isComplete = isPaymentDetailsComplete(instructorPayments, course);
                            }
                        }
                    }
                    checkAllInstructorPaymentDetailsResponse.setPaymentDetails(isComplete);
                    return checkAllInstructorPaymentDetailsResponse;
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    private boolean isPaymentDetailsComplete(InstructorPayments payments, Course course) {
        Byte instructorTerms = (course.getInstructorTerms() == null || course.getInstructorTerms() == 0) ? (byte) 0 : (byte) 1;
        if (instructorTerms == 1) {
            boolean paypalComplete = payments.getPaypalUserName() != null && !payments.getPaypalUserName().isEmpty() &&
                    payments.getPaypalEmail() != null && !payments.getPaypalEmail().isEmpty();
            boolean payoneerComplete = payments.getPayoneerEmail() != null && !payments.getPayoneerEmail().isEmpty() &&
                    payments.getPayoneerUserName() != null && !payments.getPayoneerUserName().isEmpty();
            boolean bankComplete = payments.getAccountNumber() != null && !payments.getAccountNumber().isEmpty() &&
                    payments.getSort1() != null && !payments.getSort1().isEmpty() && payments.getSort2() != null &&
                    !payments.getSort2().isEmpty() && payments.getSort3() != null && !payments.getSort3().isEmpty();
            return paypalComplete || payoneerComplete || bankComplete;
        }
        return false;
    }

    @Override
    public boolean checkInstructorPaymentDetails(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot do this process because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    return course.getInstructorTerms() == 1;

                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public boolean checkInstructorTermsForFreeCourse(String courseCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(username);
        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 2) {

                    Course course = courseRepository.getCourseByCode(courseCode);
                    if (course == null)
                        throw new ErrorException("Invalid course code", VarList.RSP_NO_DATA_FOUND);

                    if (!course.getInstructorId().getGeneralUserProfile().equals(profile))
                        throw new ErrorException("You cannot do this process because this course is not yours", VarList.RSP_NO_DATA_FOUND);

                    InstructorProfile instructorProfile = instructorProfileRepository.getInstructorProfileByGeneralUserProfile(profile);
                    if (instructorProfile == null || instructorProfile.getIsProfileCompleted() != 1)
                        return false;
                    return course.getInstructorTerms() == 1;
                } else {
                    throw new ErrorException("You are not a instructor to this operation", VarList.RSP_NO_DATA_FOUND);
                }
            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
