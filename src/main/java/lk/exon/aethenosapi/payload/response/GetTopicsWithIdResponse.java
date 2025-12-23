package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetTopicsWithIdResponse {
    private int id;
    private String topic;
}
