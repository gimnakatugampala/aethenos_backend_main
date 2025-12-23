package lk.exon.aethenosapi.payload.response;

import lk.exon.aethenosapi.entity.Course;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GetAllRequestedCourseResponse {
    private List<RequestedCourseResponse> course;
}
