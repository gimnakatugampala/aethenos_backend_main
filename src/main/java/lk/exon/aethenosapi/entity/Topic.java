package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "topic")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "topic", length = 45)
    private String topic;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subCategoryId")
    private CourseSubCategory subCategory;
    @Column(name = "link_name", length = 45)
    private String linkName;
}
