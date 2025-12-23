package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetSubCategoryDetailsResponse {
    private int id;
    private String subCategory;
    private String subLinkName;
}
