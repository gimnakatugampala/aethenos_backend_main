package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetTopicsResponse {
    private String topic;
    private String topicLinkName;
}
