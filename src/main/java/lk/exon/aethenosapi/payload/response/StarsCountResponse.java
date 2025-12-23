package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StarsCountResponse {
    private int oneRatingCount;
    private int onePointFiveRatingCount;
    private int twoRatingCount;
    private int twoPointFiveRatingCount;
    private int threeRatingCount;
    private int threePointFiveRatingCount;
    private int fourRatingCount;
    private int fourPointFiveRatingCount;
    private int fiveRatingCount;

}
