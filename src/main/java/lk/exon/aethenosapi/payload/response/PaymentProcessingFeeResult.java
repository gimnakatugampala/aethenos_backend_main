package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentProcessingFeeResult {
    private double processingFee;
    private double stripePfExchangeRate;
    private String processingFeeCurrency;

    public PaymentProcessingFeeResult(double processingFee, double stripePfExchangeRate, String processingFeeCurrency) {
        this.processingFee = processingFee;
        this.stripePfExchangeRate = stripePfExchangeRate;
        this.processingFeeCurrency = processingFeeCurrency;
    }

    public PaymentProcessingFeeResult(double processingFee, double stripePfExchangeRate) {
        this.processingFee = processingFee;
        this.stripePfExchangeRate = stripePfExchangeRate;
    }
}
