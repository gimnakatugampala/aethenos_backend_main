package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class GetSubCategoryResponse {
        private String subCategory;
        private String subCategoryLinkName;
        private List<GetTopicsResponse> topics;
}
