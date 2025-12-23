package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.request.CourseRequest;
import lk.exon.aethenosapi.payload.response.CourseResponse;
import lk.exon.aethenosapi.payload.response.SuccessResponse;

public interface EditCourseService {

    CourseResponse getCourseByID(String courseID);

    SuccessResponse updateCourse(CourseRequest courseRequest);

}
