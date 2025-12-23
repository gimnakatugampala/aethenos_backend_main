package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Country;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponPriceList {
    private double discount;
    private double discountAmount;
    private double discountPrice;
    private double listPrice;
    private String country;
}
