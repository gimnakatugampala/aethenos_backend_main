package lk.exon.aethenosapi.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "answer")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", columnDefinition = "LONGTEXT")
    private String name;

    @Column(name = "explanation", columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(name = "correct_answer")
    private Boolean correctAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

}
