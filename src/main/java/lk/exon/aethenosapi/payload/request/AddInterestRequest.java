package lk.exon.aethenosapi.payload.request;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddInterestRequest {
    private List<String> interest;
}
