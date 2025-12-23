package lk.exon.aethenosapi.controller;

import lk.exon.aethenosapi.payload.request.*;
import lk.exon.aethenosapi.payload.response.*;
import lk.exon.aethenosapi.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;

    @GetMapping("/getCouponValidationByCode/{code}")
    public CouponValidationResponse getCouponValidationByCode(@PathVariable("code") String code) {
        return purchaseService.getCouponValidationByCode(code);
    }

    @PostMapping("/addToStudentsPurchasedCourses")
    public SuccessResponse addToStudentsPurchasedCourses(@RequestBody AddPurchasedCoursesRequest addPurchasedCoursesRequest) {
        return purchaseService.addToStudentsPurchasedCourses(addPurchasedCoursesRequest);
    }

    @GetMapping("/getCoursesPurchasedByTheStudent")
    public List<GetCoursesDataResponse> getCoursesPurchasedByTheStudent() {
        return purchaseService.getCoursesPurchasedByTheStudent();
    }

    @GetMapping("/getPurchasedCourseDetailsByItemCode/{itemCode}")
    public GetCoursesDataResponse getPurchasedCourseDetailsByItemCode(@PathVariable("itemCode") String itemCode) {
        return purchaseService.getPurchasedCourseDetailsByItemCode(itemCode);
    }

    @PostMapping("/submitReview")
    public SuccessResponse submitReview(SubmitReviewRequest submitReviewRequest) {
        return purchaseService.submitReview(submitReviewRequest);
    }

    @GetMapping("/getReviewsByItemCode/{itemCode}")
    public List<ReviewResponse> getReviewsByItemCode(@PathVariable("itemCode") String itemCode) {
        return purchaseService.getReviewsByItemCode(itemCode);
    }

    @PostMapping("/addRespondToReview")
    public SuccessResponse addRespondToReview(AddRespondToReviewRequest addRespondToReviewRequest) {
        return purchaseService.addRespondToReview(addRespondToReviewRequest);
    }

    @GetMapping("/getCourseWithReviewsByCourseCode/{courseCode}")
    public List<GetCourseWithReviewsResponse> getCourseWithReviewsByCourseCode(@PathVariable("courseCode") String courseCode) {
        return purchaseService.getCourseWithReviewsByCourseCode(courseCode);
    }

    @PutMapping("/updateOrderHasCourseProgress")
    public SuccessResponse updateOrderHasCourseProgress(UpdateOrderHasCourseProgressRequest updateOrderHasCourseProgressRequest) {
        return purchaseService.updateOrderHasCourseProgress(updateOrderHasCourseProgressRequest);
    }

    @PostMapping("/addReadCurriculumItem")
    public SuccessResponse addReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest) {
        return purchaseService.addReadCurriculumItem(addReadCurriculumItemRequest);
    }

    @GetMapping("/getReadCurriculumItem")
    public GetReadCurriculumItemResponse getReadCurriculumItem(AddReadCurriculumItemRequest addReadCurriculumItemRequest) {
        return purchaseService.getReadCurriculumItem(addReadCurriculumItemRequest);
    }

    @GetMapping("/getTransactionDetailsByTransActionCode/{TransActionCode}")
    public GetTransactionDetailsResponse getTransactionDetailsByTransActionCode(@PathVariable("TransActionCode") String transActionCode) {
        return purchaseService.getTransactionDetailsByTransActionCode(transActionCode);
    }

    @GetMapping("/getPurchaseHistory")
    public List<GetPurchaseHistoryResponse> getPurchaseHistory() {
        return purchaseService.getPurchaseHistory();
    }

    @PostMapping("/addRefund")
    public SuccessResponse addRefund(AddRefundRequest addRefundRequest) {
        return purchaseService.addRefund(addRefundRequest);
    }

    @PutMapping("/updateRefundStatus")
    public SuccessResponse updateRefundStatus(UpdateRefundStatusRequest updateRefundStatusRequest) {
        return purchaseService.updateRefundStatus(updateRefundStatusRequest);
    }

    @GetMapping("/getAllRefunds")
    public List<GetAllRefundsResponse> getAllRefunds() {
        return purchaseService.getAllRefunds();
    }

    @GetMapping("/checkRefundStatus")
    public List<CheckRefundStatusResponse> checkRefundStatus() {
        return purchaseService.checkRefundStatus();
    }

    @GetMapping("/getReviewsByCourseCode/{courseCode}")
    public List<ReviewResponse> getReviewsByCourseCode(@PathVariable("courseCode") String courseCode) {
        return purchaseService.getReviewsByCourseCode(courseCode);
    }

    @GetMapping("/getOwnAllRefunds")
    public List<GetOwnAllRefundsResponse> getOwnAllRefunds() {
        return purchaseService.getOwnAllRefunds();
    }

    @GetMapping("/getCompletedRefunds")
    public List<GetCompletedRefundsResponse> getCompletedRefunds() {
        return purchaseService.getCompletedRefunds();
    }

    @PostMapping("/addPreviousView")
    public SuccessResponse addPreviousView(AddPreviousViewRequest addPreviousViewRequest) {
        return purchaseService.addPreviousView(addPreviousViewRequest);
    }

    @GetMapping("/getPreviousView/{itemCode}")
    public GetPreviousViewResponse getPreviousView(@PathVariable("itemCode") String itemCode) {
        return purchaseService.getPreviousView(itemCode);
    }

    @PutMapping("/updatePreviousViewDuration")
    public SuccessResponse updatePreviousViewDuration(UpdatePreviousViewDurationRequest updatePreviousViewDurationRequest) {
        return purchaseService.updatePreviousViewDuration(updatePreviousViewDurationRequest);
    }

    @GetMapping("/getCertificate/{itemCode}")
    public String getCertificate(@PathVariable("itemCode") String itemCode) {
        return purchaseService.getCertificate(itemCode);
    }
    @GetMapping("/getExcelCoursesPurchasedByTheStudent")
    public Set<GetCoursesDataResponse> getExcelCoursesPurchasedByTheStudent() {
        return purchaseService.getExcelCoursesPurchasedByTheStudent();
    }

}
