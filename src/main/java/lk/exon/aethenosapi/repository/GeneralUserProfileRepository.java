package lk.exon.aethenosapi.repository;

import lk.exon.aethenosapi.entity.GeneralUserProfile;
import lk.exon.aethenosapi.entity.GupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneralUserProfileRepository extends JpaRepository<GeneralUserProfile, Integer> {

    GeneralUserProfile getGeneralUserProfileByEmail(String email);
    List<GeneralUserProfile> findByGupType_Id(int i);
    GeneralUserProfile getGeneralUserProfileById(int adminId);
    List<GeneralUserProfile> getGeneralUserProfileByGupType(GupType gupType);
    GeneralUserProfile getGeneralUserProfileByUserCode(String userCode);

    // Fetches ghost users enrolled in a specific course who haven't left a review yet.
    @Query(value = "SELECT g.* FROM general_user_profile g " +
            "JOIN orders o ON g.id = o.general_user_profile_id " +
            "JOIN order_has_course ohc ON o.id = ohc.order_id " +
            "WHERE g.is_synthetic = 1 " +
            "AND ohc.course_id = :courseId " +
            "AND NOT EXISTS (SELECT 1 FROM review r WHERE r.gup_id = g.id AND r.course_id = :courseId)",
            nativeQuery = true)
    List<GeneralUserProfile> findAvailableGhostsForCourse(@Param("courseId") Integer courseId);

    // Fetches ALL synthetic users for a specific course (so we can safely delete them)
    @Query(value = "SELECT g.* FROM general_user_profile g " +
            "JOIN orders o ON g.id = o.general_user_profile_id " +
            "JOIN order_has_course ohc ON o.id = ohc.order_id " +
            "WHERE g.is_synthetic = 1 AND ohc.course_id = :courseId",
            nativeQuery = true)
    List<GeneralUserProfile> findAllSyntheticUsersForCourse(@Param("courseId") Integer courseId);
}