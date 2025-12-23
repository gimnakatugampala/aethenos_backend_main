package lk.exon.aethenosapi.payload.request;

import lk.exon.aethenosapi.entity.Coupon;
import lk.exon.aethenosapi.entity.Course;
import lk.exon.aethenosapi.entity.CoursePrice;
import lk.exon.aethenosapi.entity.Currency;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseWithPrice {
    private Course course;
    private Currency currency;
    private Double itemPrice;
    private Double listPrice;
    private CoursePrice coursePrice;
    private Coupon coupon;
}
