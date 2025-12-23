package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class AmountDataSetsResponse {
    private Date timestamp;
    private double amount;
}
