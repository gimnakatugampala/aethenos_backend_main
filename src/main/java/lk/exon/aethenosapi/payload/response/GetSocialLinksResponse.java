package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetSocialLinksResponse {
    private String link;
    private String target;
    private String icon;
}
