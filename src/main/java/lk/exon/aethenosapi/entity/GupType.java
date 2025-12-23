package lk.exon.aethenosapi.entity;

import javax.persistence.*;

@Entity
@Table(name = "gup_type")
public class GupType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 10)
    private String name;

    public static GupType valueOf(String gupType) {
        GupType type = new GupType();
        type.setName(gupType);
        return type;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}