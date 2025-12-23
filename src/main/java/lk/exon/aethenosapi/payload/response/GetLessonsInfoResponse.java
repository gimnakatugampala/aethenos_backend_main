package lk.exon.aethenosapi.payload.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetLessonsInfoResponse {
    private String title;
    private String text;
    private List<GetlessonResponse> lessons;
}
