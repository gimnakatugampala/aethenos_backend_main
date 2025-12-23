package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetTopicCategorySubCategoryByTopicResponse {
    private String topic;
    private String category;
    private String category_linkName;
    private String subCategory;
    private String subCategory_linkName;
}
