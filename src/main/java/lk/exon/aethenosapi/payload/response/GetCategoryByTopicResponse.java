package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetCategoryByTopicResponse {
    private String category;
    private String categoryLinkName;
}
