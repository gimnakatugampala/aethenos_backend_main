package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetCategoryResponse {
    private String category;
    private String categoryLinkName;
    private List<GetSubCategoryResponse> subCategoryList;
}

