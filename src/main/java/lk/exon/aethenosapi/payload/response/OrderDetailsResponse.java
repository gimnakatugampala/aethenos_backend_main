package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Currency;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderDetailsResponse {
    private String buyDate;
    private String currency;
    private double disCount;
    private String total;
    private String paymentMethod;
    private List<OrderHasCourseResponse> orderHasItems;
}
