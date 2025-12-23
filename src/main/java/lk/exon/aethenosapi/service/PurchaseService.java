package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;
import java.util.Set;

public interface PurchaseService {

    SuccessResponse addToStudentsPurchasedCourses(AddPurchasedCoursesRequest addPurchasedCoursesRequest);

    CouponValidationResponse getCouponValidationByCode(String code);

    List<GetCoursesDataResponse> getCoursesPurchasedByTheStudent();

    GetCoursesDataResponse getPurchasedCourseDetailsByItemCode(String itemCode);

    SuccessResponse submitReview(SubmitReviewRequest submitReviewRequest);

    List<ReviewResponse> getReviewsByItemCode(String itemCode);

    SuccessResponse addRespondToReview(AddRespondToReviewRequest addRespondToReviewRequest);

    List<GetCourseWithReviewsResponse> getCourseWithReviewsByCourseCode(String courseCode);

    SuccessResponse updateOrderHasCourseProgress(UpdateOrderHasCourseProgressRequest updateOrderHasCourseProgressRequest);

    SuccessResponse addReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest);

    GetReadCurriculumItemResponse getReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest);

    GetTransactionDetailsResponse getTransactionDetailsByTransActionCode(String transActionCode);

    List<GetPurchaseHistoryResponse> getPurchaseHistory();

    SuccessResponse addRefund(AddRefundRequest addRefundRequest);

    SuccessResponse updateRefundStatus(UpdateRefundStatusRequest updateRefundStatusRequest);

    List<GetAllRefundsResponse> getAllRefunds();

    List<CheckRefundStatusResponse> checkRefundStatus();

    List<ReviewResponse> getReviewsByCourseCode(String courseCode);

    List<GetOwnAllRefundsResponse> getOwnAllRefunds();

    List<GetCompletedRefundsResponse> getCompletedRefunds();

    SuccessResponse addPreviousView(AddPreviousViewRequest addPreviousViewRequest);

    GetPreviousViewResponse getPreviousView(String itemCode);

    SuccessResponse updatePreviousViewDuration(UpdatePreviousViewDurationRequest updatePreviousViewDurationRequest);

    String getCertificate(String itemCode);

    Set<GetCoursesDataResponse> getExcelCoursesPurchasedByTheStudent();
}
