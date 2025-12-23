package lk.exon.aethenosapi.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "verification_approval")
public class VerificationApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "approved_date")
    private Instant approvedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_verification_id", nullable = false)
    private InstructorVerification instructorVerification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "general_user_profile_id", nullable = false)
    private GeneralUserProfile generalUserProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approval_type_id", nullable = false)
    private ApprovalType approvalType;

    @Column(name = "comment", length = 45)
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Instant approvedDate) {
        this.approvedDate = approvedDate;
    }

    public InstructorVerification getInstructorVerification() {
        return instructorVerification;
    }

    public void setInstructorVerification(InstructorVerification instructorVerification) {
        this.instructorVerification = instructorVerification;
    }

    public GeneralUserProfile getGeneralUserProfile() {
        return generalUserProfile;
    }

    public void setGeneralUserProfile(GeneralUserProfile generalUserProfile) {
        this.generalUserProfile = generalUserProfile;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}