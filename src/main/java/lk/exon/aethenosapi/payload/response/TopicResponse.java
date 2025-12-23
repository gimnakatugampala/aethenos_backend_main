package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TopicResponse {
    private int id;
    private String link_name;
    private String topic;

}
