package lk.exon.aethenosapi.entity;

import javax.persistence.*;

@Entity
@Table(name = "verification_attachments")
public class VerificationAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "doc1", length = 1000)
    private String doc1;

    @Column(name = "doc2", length = 1000)
    private String doc2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_verification_id", nullable = false)
    private InstructorVerification instructorVerification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDoc1() {
        return doc1;
    }

    public void setDoc1(String doc1) {
        this.doc1 = doc1;
    }

    public String getDoc2() {
        return doc2;
    }

    public void setDoc2(String doc2) {
        this.doc2 = doc2;
    }

    public InstructorVerification getInstructorVerification() {
        return instructorVerification;
    }

    public void setInstructorVerification(InstructorVerification instructorVerification) {
        this.instructorVerification = instructorVerification;
    }

}