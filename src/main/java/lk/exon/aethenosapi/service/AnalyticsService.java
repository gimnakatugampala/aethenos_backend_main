package lk.exon.aethenosapi.service;

import lk.exon.aethenosapi.payload.response.GetCoursesByInstructorResponse;
import lk.exon.aethenosapi.payload.response.StudentsEnrollResponse;

import java.util.List;

public interface AnalyticsService {
    List<GetCoursesByInstructorResponse> getCoursesByInstructor();

    List<StudentsEnrollResponse> getStudentsEnrollByCourse(String courseCode);
}
