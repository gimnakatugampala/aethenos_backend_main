package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCategoryAndSubCategorynameResponse {
    private String CategoryName;
    private String subCategoryName;
}
