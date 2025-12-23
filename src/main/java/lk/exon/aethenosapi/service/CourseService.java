package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.payload.request.AddDiscountCouponRequest;
import lk.exon.aethenosapi.payload.request.AddExternalCourseLinkAndRatingsRequest;
import lk.exon.aethenosapi.payload.request.AddFreeCouponRequest;
import lk.exon.aethenosapi.payload.request.CourseRequest;
import lk.exon.aethenosapi.payload.response.*;

import java.util.List;

public interface CourseService {
    SuccessResponse addCourse(CourseRequest courseRequest);

    List<GetCategoryResponse> getAllCategorySubCategoryTopics();

    SuccessResponse getReferralCodeByCourseCode(String courseCode);

    List<CouponTypeResponse> getCourseType();

    SuccessResponse addFreeCoupon(AddFreeCouponRequest addFreeCouponRequest);

    List<CouponResponse> getCouponsFromCourseCode(String courseCode);

    SuccessResponse activeDeactiveCoupon(String couponCode);

    SuccessResponse addDiscountCoupon(AddDiscountCouponRequest addDiscountCouponRequest);

    List<GetTopicsWithIdResponse> getTopicsBySubCategory(int subCategoryId);

    int CheckOwnCourse(String courseCode);

    SuccessResponse addExternalCourseLinkAndRatings(AddExternalCourseLinkAndRatingsRequest addExternalCourseLinkAndRatingsRequest);
}
