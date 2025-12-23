package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class SetTopicRequest {
    private List<Integer> topic;
}
