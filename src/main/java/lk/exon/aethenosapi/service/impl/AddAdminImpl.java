package lk.exon.aethenosapi.service.impl;

import lk.exon.aethenosapi.config.PasswordEncoderConfig;
import lk.exon.aethenosapi.entity.*;
import lk.exon.aethenosapi.exception.ErrorException;
import lk.exon.aethenosapi.payload.request.AddAdminRequest;
import lk.exon.aethenosapi.payload.request.AddRevenuePricesSplitRequest;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.repository.*;
import lk.exon.aethenosapi.service.AddAdminService;
import lk.exon.aethenosapi.utils.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AddAdminImpl implements AddAdminService {
    @Autowired
    private UserProfileServiceImpl userProfileServiceImpl;
    @Autowired
    private GeneralUserProfileRepository generalUserProfileRepository;
    @Autowired
    private GupTypeRepository gupTypeRepository;
    @Autowired
    private RevenueSplitRepository revenueSplitRepository;
    @Autowired
    private RevenueSplitTypeRepository revenueSplitTypeRepository;
    @Autowired
    private RevenueSplitHistoryRepository revenueSplitHistoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private OrderHasCourseRepository orderHasCourseRepository;

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    DateTimeFormatter updatedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public SuccessResponse addAdmin(AddAdminRequest addAdminRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getIsActive() == 1) {
                if (profile.getGupType().getId() == 3) {
                    GeneralUserProfile newAdmin = new GeneralUserProfile();
                    newAdmin.setUserCode(UUID.randomUUID().toString());
                    newAdmin.setRegisteredDate(new Date());
                    newAdmin.setFirstName(addAdminRequest.getFirstName());
                    newAdmin.setLastName(addAdminRequest.getLastName());
                    newAdmin.setEmail(addAdminRequest.getEmail());
                    newAdmin.setIsActive((byte) 1);

                    PasswordEncoderConfig by = new PasswordEncoderConfig();
                    String encryptedpassword = by.passwordEncoder().encode(addAdminRequest.getPassword());
                    newAdmin.setPassword(encryptedpassword);


                    GupType gupType = gupTypeRepository.getGupTypeById(addAdminRequest.getGup_type_id());
                    if (gupType != null) {
                        newAdmin.setGupType(gupType);
                    } else {
                        throw new ErrorException("General user profile type not available", VarList.RSP_NO_DATA_FOUND);
                    }

                    generalUserProfileRepository.save(newAdmin);

                    SuccessResponse successResponse = new SuccessResponse();
                    successResponse.setMessage("New admin added successfully");
                    successResponse.setVariable(VarList.RSP_SUCCESS);

                    return successResponse;

                } else {
                    throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("User not active", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GeneralUserProfile> getAllAdmins() {
        return generalUserProfileRepository.findByGupType_Id(3);
    }

    @Override
    public SuccessResponse getAdminDetails(int adminId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getGupType().getId() == 3) {
                GeneralUserProfile admin = generalUserProfileRepository.getGeneralUserProfileById(adminId);
                if (admin != null) {
                    if (admin.getIsActive() == 1) {
                        admin.setIsActive((byte) 0);
                        generalUserProfileRepository.save(admin);
                        SuccessResponse successResponse = new SuccessResponse();
                        successResponse.setMessage("Admin deactivated successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);

                        return successResponse;
                    } else {
                        admin.setIsActive((byte) 1);
                        generalUserProfileRepository.save(admin);

                        SuccessResponse successResponse = new SuccessResponse();
                        successResponse.setMessage("Admin activated successfully");
                        successResponse.setVariable(VarList.RSP_SUCCESS);

                        return successResponse;
                    }
                } else {
                    throw new ErrorException("Admin profile not found", VarList.RSP_NO_DATA_FOUND);
                }

            } else {
                throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public SuccessResponse addRevenuePricesSplit(AddRevenuePricesSplitRequest addRevenuePricesSplitRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getGupType().getId() == 3) {

                final Double aethenosRevenueReferralLinkSplit = addRevenuePricesSplitRequest.getAethenosRevenueReferralLinkSplit();
                final Double instructorRevenueReferralLinkSplit = addRevenuePricesSplitRequest.getInstructorRevenueReferralLinkSplit();
                final Double aethenosRevenueAethenosSplit = addRevenuePricesSplitRequest.getAethenosRevenueAethenosSplit();
                final Double instructorRevenueAethenosSplit = addRevenuePricesSplitRequest.getInstructorRevenueAethenosSplit();

                if (aethenosRevenueReferralLinkSplit == null || aethenosRevenueReferralLinkSplit.toString().isEmpty() || instructorRevenueReferralLinkSplit == null ||
                        instructorRevenueReferralLinkSplit.toString().isEmpty() || aethenosRevenueAethenosSplit == null || aethenosRevenueAethenosSplit.toString().isEmpty()
                        || instructorRevenueAethenosSplit == null || instructorRevenueAethenosSplit.toString().isEmpty())
                    throw new ErrorException("Invalid request", VarList.RSP_NO_DATA_FOUND);

                if ((aethenosRevenueReferralLinkSplit + instructorRevenueReferralLinkSplit) != 100 || (aethenosRevenueAethenosSplit + instructorRevenueAethenosSplit) != 100)
                    throw new ErrorException("Invalid revenue split ratios, please add valid revenue ratios", VarList.RSP_NO_DATA_FOUND);

                RevenueSplitType revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(1);
                if (revenueSplitType == null)
                    throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);

                RevenueSplit revenueSplits = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);
                SuccessResponse successResponse = new SuccessResponse();

                boolean isUpdate = false;
                if (revenueSplits == null) {
                    revenueSplits = new RevenueSplit();
                    revenueSplits.setRevenueSplitType(revenueSplitType);
                    successResponse.setMessage("The revenue price split was added successfully");
                    isUpdate = true;
                } else {
                    successResponse.setMessage("The revenue price split was successfully updated");

                    if ((revenueSplits.getAethenosRevenue() != aethenosRevenueAethenosSplit ||
                            revenueSplits.getInstructorRevenue() != instructorRevenueAethenosSplit) &&
                            revenueSplits.getRevenueSplitType().equals(revenueSplitType))
                        isUpdate = true;

                }

                revenueSplits.setAethenosRevenue(aethenosRevenueAethenosSplit);
                revenueSplits.setInstructorRevenue(instructorRevenueAethenosSplit);

                revenueSplitRepository.save(revenueSplits);

                revenueSplitType = revenueSplitTypeRepository.getRevenueSplitTypeById(2);
                if (revenueSplitType == null)
                    throw new ErrorException("Revenue split type not found", VarList.RSP_NO_DATA_FOUND);

                isUpdate = false;
                revenueSplits = revenueSplitRepository.getRevenueSplitByRevenueSplitType(revenueSplitType);

                if (revenueSplits == null) {
                    revenueSplits = new RevenueSplit();
                    revenueSplits.setRevenueSplitType(revenueSplitType);
                    isUpdate = true;
                } else {
                    if ((revenueSplits.getAethenosRevenue() != aethenosRevenueReferralLinkSplit ||
                            revenueSplits.getInstructorRevenue() != instructorRevenueReferralLinkSplit) &&
                            revenueSplits.getRevenueSplitType().equals(revenueSplitType))
                        isUpdate = true;
                }

                if (isUpdate) {
                    RevenueSplitHistory revenueSplitHistory = new RevenueSplitHistory();
                    revenueSplitHistory.setChangedDate(new Date());
                    revenueSplitHistory.setAethenosRevenueAethenosSplit(aethenosRevenueAethenosSplit);
                    revenueSplitHistory.setInstructorRevenueAethenosSplit(instructorRevenueAethenosSplit);
                    revenueSplitHistory.setAethenosRevenueReferralLinkSplit(aethenosRevenueReferralLinkSplit);
                    revenueSplitHistory.setInstructorRevenueReferralLinkSplit(instructorRevenueReferralLinkSplit);
                    revenueSplitHistoryRepository.save(revenueSplitHistory);
                }

                revenueSplits.setAethenosRevenue(aethenosRevenueReferralLinkSplit);
                revenueSplits.setInstructorRevenue(instructorRevenueReferralLinkSplit);

                revenueSplitRepository.save(revenueSplits);


                successResponse.setVariable(VarList.RSP_SUCCESS);

                return successResponse;

            } else {
                throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public GetRevenuePricesSplitResponse getRevenuePricesSplit() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getGupType().getId() == 3) {

                List<RevenueSplit> revenueSplits = revenueSplitRepository.findAll();
                if (revenueSplits == null)
                    throw new ErrorException("Income distribution data is not included.", VarList.RSP_NO_DATA_FOUND);
                GetRevenuePricesSplitResponse getRevenuePricesSplitResponse = new GetRevenuePricesSplitResponse();
                for (RevenueSplit revenueSplit : revenueSplits) {
                    if (revenueSplit.getRevenueSplitType().getId() == 1) {
                        getRevenuePricesSplitResponse.setAethenosRevenueAethenosSplit(revenueSplit.getAethenosRevenue());
                        getRevenuePricesSplitResponse.setInstructorRevenueAethenosSplit(revenueSplit.getInstructorRevenue());
                    }
                    if (revenueSplit.getRevenueSplitType().getId() == 2) {
                        getRevenuePricesSplitResponse.setAethenosRevenueReferralLinkSplit(revenueSplit.getAethenosRevenue());
                        getRevenuePricesSplitResponse.setInstructorRevenueReferralLinkSplit(revenueSplit.getInstructorRevenue());
                    }
                }

                return getRevenuePricesSplitResponse;

            } else {
                throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetRevenueSplitHistoryResponse> getRevenueSplitHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getGupType().getId() == 3) {

                List<RevenueSplitHistory> revenueSplitHistories = revenueSplitHistoryRepository.findAll();
                List<GetRevenueSplitHistoryResponse> getRevenueSplitHistoryResponses = new ArrayList<>();
                for (RevenueSplitHistory revenueSplitHistory : revenueSplitHistories) {
                    GetRevenueSplitHistoryResponse getRevenueSplitHistoryResponse = new GetRevenueSplitHistoryResponse();
                    getRevenueSplitHistoryResponse.setChangedDate(revenueSplitHistory.getChangedDate());
                    getRevenueSplitHistoryResponse.setAethenosRevenueAethenosSplit(revenueSplitHistory.getAethenosRevenueAethenosSplit());
                    getRevenueSplitHistoryResponse.setInstructorRevenueAethenosSplit(revenueSplitHistory.getInstructorRevenueAethenosSplit());
                    getRevenueSplitHistoryResponse.setAethenosRevenueReferralLinkSplit(revenueSplitHistory.getAethenosRevenueReferralLinkSplit());
                    getRevenueSplitHistoryResponse.setInstructorRevenueReferralLinkSplit(revenueSplitHistory.getInstructorRevenueReferralLinkSplit());
                    getRevenueSplitHistoryResponses.add(getRevenueSplitHistoryResponse);
                }
                return getRevenueSplitHistoryResponses;

            } else {
                throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }

    @Override
    public List<GetAllTransactionHistoryResponse> getAllTransactionHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        GeneralUserProfile profile = userProfileServiceImpl.getProfile(email);

        if (profile != null) {
            if (profile.getGupType().getId() == 3) {
                List<GetAllTransactionHistoryResponse> getAllTransactionHistoryResponses = new ArrayList<>();
                List<Transaction> transactions = transactionRepository.findAll();
                GetAllTransactionHistoryResponse getAllTransactionHistoryResponse;
                for (Transaction transaction : transactions) {
                    getAllTransactionHistoryResponse = new GetAllTransactionHistoryResponse();
                    getAllTransactionHistoryResponse.setTransactionCode(transaction.getTransactionCode());
                    getAllTransactionHistoryResponse.setStudentName(transaction.getOrder().getGeneralUserProfile().getFirstName() + " " + transaction.getOrder().getGeneralUserProfile().getLastName());
                    getAllTransactionHistoryResponse.setPurchaseCountry(transaction.getOrder().getGeneralUserProfile().getCountry() == null ? "" : transaction.getOrder().getGeneralUserProfile().getCountry());
                    getAllTransactionHistoryResponse.setTotalAmount(transaction.getOrder().getCurrency() + decimalFormat.format(transaction.getAmount()));
                    getAllTransactionHistoryResponse.setPurchasedDate(transaction.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(updatedFormatter));
                    getAllTransactionHistoryResponse.setPaymentMethod(transaction.getOrder().getPaymentMethod().getMethod());
                    List<OrderHasCourse> orderHasCourses = orderHasCourseRepository.getOrderHasCoursesByOrder(transaction.getOrder());
                    List<GetExpandedTransactionResponse> getExpandedTransactionResponses = new ArrayList<>();
                    for (OrderHasCourse orderHasCourse : orderHasCourses) {
                        GetExpandedTransactionResponse getExpandedTransactionResponse = new GetExpandedTransactionResponse();
                        getExpandedTransactionResponse.setCourseName(orderHasCourse.getCourse().getCourseTitle());
                        getExpandedTransactionResponse.setListPrice(orderHasCourse.getCurrrency() + decimalFormat.format(orderHasCourse.getListPrice()));
                        getExpandedTransactionResponse.setItemPrice(orderHasCourse.getCurrrency() + decimalFormat.format(orderHasCourse.getItemPrice()));
                        getExpandedTransactionResponse.setChannel(orderHasCourse.getCoursePurchaseType().getPurchaseType());
                        getExpandedTransactionResponses.add(getExpandedTransactionResponse);
                    }
                    getAllTransactionHistoryResponse.setCourses(getExpandedTransactionResponses);
                    getAllTransactionHistoryResponses.add(getAllTransactionHistoryResponse);
                }

                return getAllTransactionHistoryResponses;

            } else {
                throw new ErrorException("Only admin can access", VarList.RSP_NO_DATA_FOUND);
            }
        } else {
            throw new ErrorException("User not found", VarList.RSP_NO_DATA_FOUND);
        }
    }
}
